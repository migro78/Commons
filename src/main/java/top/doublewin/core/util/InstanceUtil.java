package top.doublewin.core.util;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import top.doublewin.core.exception.DataParseException;
import top.doublewin.core.exception.InstanceException;
import top.doublewin.model.ExcludeSet;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 实例辅助类
 * 
 * @author ShenHuaJie
 * @since 2012-07-18
 */
public final class InstanceUtil {
    protected static Logger logger = LogManager.getLogger();

    /**
     * 实例化并复制属性
     * @param
     * @return
     */
    public static final <T> T to(Object orig, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
            to(orig,bean,null);
        } catch (Exception e) {
            logger.error("to Error " + e);
        }
        return bean;
    }

    public static final void to(Object orig, Object bean) {
        to(orig,bean,null);
    }

    /**
     * 对象属性复制
     * @param orig 源对象
     * @param bean 目标对象
     * @param exclude 排除属性
     * @return
     */
    public static final void to(Object orig, Object bean, ExcludeSet exclude) {
        try {
            if(DataUtil.isEmpty(exclude)){
                exclude = ExcludeSet.getInstance();
            }
            Class<?> cls = orig.getClass();
            BeanInfo orgInfo = Introspector.getBeanInfo(cls);
            PropertyDescriptor[] orgPty = orgInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertyMap = newHashMap();
            for (PropertyDescriptor property : orgPty) {
                propertyMap.put(property.getName(), property);
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤排除属性
                if(exclude.contains(key)){
                    logger.debug("exclude key name is {}",key);
                    continue;
                }
                // 过滤class属性
                if (!"class".equals(key) && propertyMap.containsKey(key)) {
                    Method getter = propertyMap.get(key).getReadMethod();
                    Method setter = property.getWriteMethod();
                    Object value = "";
                    try {
                        value = getter.invoke(orig);
                        value = TypeParseUtil.convert(value, property.getPropertyType(), null);
                        setter.invoke(bean, value);
                    } catch (Exception e) {
                        logger.debug("unknown field <" + key + ":" + value + ">" + e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("to Error " + e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final <T> T parse(String json, Class<T> clazz) {
        try {
            Map map = JSON.parseObject(json, Map.class);
            return (T)transMap2Bean(map, clazz);
        } catch (Exception e) {
            logger.error("parse", e);
        }
        return null;
    }

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static <T> T transMap2Bean(Map<String, Object> map, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(bean, TypeParseUtil.convert(value, property.getPropertyType(), null));
                }
            }
        } catch (Exception e) {
            logger.error("transMap2Bean Error ", e);
        }
        return bean;
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) {
        Map<String, Object> map = newHashMap();
        String key = null;
        if (obj == null) {
            return map;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                key = property.getName();
                // 过滤class属性
                if (!"class".equals(key)) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    if(DataUtil.isNotEmpty(getter)) {
                        Object value = getter.invoke(obj);
                        if (DataUtil.isNotEmpty(value)) {
                            map.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("transBean2Map key is {} , Error ms :" , key, e);
        }
        return map;
    }

    /**
     * @param oldBean
     * @param newBean
     * @return
     */
    public static <T> T getDiff(T oldBean, T newBean) {
        if (oldBean == null && newBean != null) {
            return newBean;
        } else if (newBean == null) {
            return null;
        } else {
            Class<?> cls1 = oldBean.getClass();
            try {
                @SuppressWarnings("unchecked")
                T object = (T)cls1.newInstance();
                BeanInfo beanInfo = Introspector.getBeanInfo(cls1);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    // 过滤class属性
                    if (!"class".equals(key)) {
                        // 得到property对应的getter方法
                        Method getter = property.getReadMethod();
                        // 得到property对应的setter方法
                        Method setter = property.getWriteMethod();
                        Object oldValue = getter.invoke(oldBean);
                        Object newValue = getter.invoke(newBean);
                        if (setter != null && newValue != null && !newValue.equals(oldValue)) {
                            setter.invoke(object, newValue);
                        }
                    }
                }
                return object;
            } catch (Exception e) {
                throw new DataParseException(e);
            }
        }
    }

    /**
     * Return the specified class. Checks the ThreadContext classloader first,
     * then uses the System classloader. Should replace all calls to
     * <code>Class.forName( claz )</code> (which only calls the System class
     * loader) when the class might be in a different classloader (e.g. in a
     * webapp).
     * 
     * @param clazz
     *            the name of the class to instantiate
     * @return the requested Class object
     */
    public static final Class<?> getClass(String clazz) {
        /**
         * Use the Thread context classloader if possible
         */
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            if (loader != null) {
                return Class.forName(clazz, true, loader);
            }
            /**
             * Thread context classloader isn't working out, so use system
             * loader.
             */
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new InstanceException(e);
        }
    }

    /**
     * 封装实体
     * 
     * @param cls
     *            实体类
     * @param list
     *            实体Map集合
     * @return
     */
    public static final <E> List<E> getInstanceList(Class<E> cls, List<?> list) {
        List<E> resultList = newArrayList();
        E object = null;
        for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
            Map<?, ?> map = (Map<?, ?>)iterator.next();
            object = newInstance(cls, map);
            resultList.add(object);
        }
        return resultList;
    }

    /**
     * 封装实体
     * 
     * @param cls
     *            实体类
     * @param rs
     *            数据查询结果集
     * @return
     */
    public static final <E> List<E> getInstanceList(Class<E> cls, ResultSet rs) {
        List<E> resultList = newArrayList();
        try {
            E object = cls.newInstance();
            Field[] fields = cls.getDeclaredFields();
            while (rs.next()) {
                object = cls.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i].getName();
                    PropertyUtils.setProperty(object, fieldName, rs.getObject(fieldName));
                }
                resultList.add(object);
            }
        } catch (Exception e) {
            throw new InstanceException(e);
        }
        return resultList;
    }

    /**
     * 新建实例
     * 
     * @param cls
     *            实体类
     * @param map
     *            实体属性Map
     * @return
     */
    public static final <E> E newInstance(Class<E> cls, Map<String, ?> map) {
        E object = null;
        try {
            object = cls.newInstance();
            BeanUtils.populate(object, map);
        } catch (Exception e) {
            throw new InstanceException(e);
        }
        return object;
    }

    /**
     * Return a new instance of the given class. Checks the ThreadContext
     * classloader first, then uses the System classloader. Should replace all
     * calls to <code>Class.forName( claz ).newInstance()</code> (which only
     * calls the System class loader) when the class might be in a different
     * classloader (e.g. in a webapp).
     * 
     * @param clazz
     *            the name of the class to instantiate
     * @return an instance of the specified class
     */
    public static final Object newInstance(String clazz) {
        try {
            return getClass(clazz).newInstance();
        } catch (Exception e) {
            throw new InstanceException(e);
        }
    }

    public static final <K> K newInstance(Class<K> cls, Object... args) {
        try {
            Class<?>[] argsClass = null;
            if (args != null) {
                argsClass = new Class[args.length];
                for (int i = 0, j = args.length; i < j; i++) {
                    argsClass[i] = args[i].getClass();
                }
            }
            Constructor<K> cons = cls.getConstructor(argsClass);
            return cons.newInstance(args);
        } catch (Exception e) {
            throw new InstanceException(e);
        }
    }

    public static Map<String, Class<?>> clazzMap = new HashMap<String, Class<?>>();

    /**
     * 新建实例
     * 
     * @param className
     *            类名
     * @param args
     *            构造函数的参数
     * @return 新建的实例
     */
    public static final Object newInstance(String className, Object... args) {
        try {
            Class<?> newoneClass = clazzMap.get(className);
            if (newoneClass == null) {
                newoneClass = Class.forName(className);
                clazzMap.put(className, newoneClass); // 缓存class对象
            }
            return newInstance(newoneClass, args);
        } catch (Exception e) {
            throw new InstanceException(e);
        }
    }

    public static Map<String, MethodAccess> methodMap = new HashMap<String, MethodAccess>();

    /**
     * 执行某对象方法
     * 
     * @param owner
     *            对象
     * @param methodName
     *            方法名
     * @param args
     *            参数
     * @return 方法返回值
     */
    public static final Object invokeMethod(Object owner, String methodName, Object... args) {
        Class<?> ownerClass = owner.getClass();
        String key = null;
        if (args != null) {
            Class<?>[] argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                if (args[i] != null) {
                    argsClass[i] = args[i].getClass();
                }
            }
            key = ownerClass + "_" + methodName + "_" + StringUtils.join(argsClass, ","); // 用于区分重载的方法
        } else {
            key = ownerClass + "_" + methodName; // 用于区分重载的方法
        }
        MethodAccess methodAccess = methodMap.get(key);
        if (methodAccess == null) {
            methodAccess = MethodAccess.get(ownerClass);
            methodMap.put(key, methodAccess); // 缓存Method对象
        }
        return methodAccess.invoke(owner, methodName, args);
    }

    /**
     * Constructs an empty ArrayList.
     */
    public static final <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * Constructs an empty ArrayList.
     */
    @SuppressWarnings("unchecked")
    public static final <E> ArrayList<E> newArrayList(E... e) {
        ArrayList<E> list = newArrayList();
        Collections.addAll(list, e);
        return list;
    }

    /**
     * Constructs an empty HashMap.
     */
    public static final <k, v> HashMap<k, v> newHashMap() {
        return new HashMap<k, v>();
    }

    /**
     * Constructs an empty HashSet.
     */
    public static final <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    /**
     * Constructs an empty Hashtable.
     */
    public static final <k, v> Hashtable<k, v> newHashtable() {
        return new Hashtable<k, v>();
    }

    /**
     * Constructs an empty LinkedHashMap.
     */
    public static final <k, v> LinkedHashMap<k, v> newLinkedHashMap() {
        return new LinkedHashMap<k, v>();
    }

    /**
     * Constructs an empty LinkedHashSet.
     */
    public static final <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<E>();
    }

    /**
     * Constructs an empty LinkedList.
     */
    public static final <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    /**
     * Constructs an empty TreeMap.
     */
    public static final <k, v> TreeMap<k, v> newTreeMap() {
        return new TreeMap<k, v>();
    }

    /**
     * Constructs an empty TreeSet.
     */
    public static final <E> TreeSet<E> newTreeSet() {
        return new TreeSet<E>();
    }

    /**
     * Constructs an empty Vector.
     */
    public static final <E> Vector<E> newVector() {
        return new Vector<E>();
    }

    /**
     * Constructs an empty WeakHashMap.
     */
    public static final <k, v> WeakHashMap<k, v> newWeakHashMap() {
        return new WeakHashMap<k, v>();
    }

    /**
     * Constructs an HashMap.
     */
    public static final <k, v> HashMap<k, v> newHashMap(k key, v value) {
        HashMap<k, v> map = newHashMap();
        map.put(key, value);
        return map;
    }

    /**
     * Constructs an LinkedHashMap.
     */
    public static final <k, v> LinkedHashMap<k, v> newLinkedHashMap(k key, v value) {
        LinkedHashMap<k, v> map = newLinkedHashMap();
        map.put(key, value);
        return map;
    }

    /**
     * Constructs an empty ConcurrentHashMap.
     */
    public static final <k, v> ConcurrentHashMap<k, v> newConcurrentHashMap() {
        return new ConcurrentHashMap<k, v>();
    }

    /**
     * Constructs an empty ConcurrentSkipListSet.
     */
    public static final <e> ConcurrentSkipListSet<e> newConcurrentSkipListSet() {
        return new ConcurrentSkipListSet<e>();
    }

    /**
     * Constructs an empty ConcurrentLinkedDeque.
     */
    public static final <e> ConcurrentLinkedDeque<e> newConcurrentLinkedDeque() {
        return new ConcurrentLinkedDeque<e>();
    }

    /**
     * Constructs an empty ConcurrentLinkedQueue.
     */
    public static final <e> ConcurrentLinkedQueue<e> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<e>();
    }

    /**
     * @param e
     * @return
     */
    public static <E> Set<E> newHashSet(E[] e) {
        Set<E> set = newHashSet();
        Collections.addAll(set, e);
        return set;
    }

    public static Set<Class<?>>  getAnnotatedClass(String packageName,Class<? extends Annotation> annotation)
    {
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.filterInputsBy(new FilterBuilder().includePackage(packageName));
        config.addUrls(ClasspathHelper.forPackage(packageName));
        config.setScanners(new TypeAnnotationsScanner(),new SubTypesScanner());
        Reflections reflections = new Reflections(config);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
        return classes;
    }

    /**
     * 给类属性添加前缀值
     *
     * @param clazz  目标对象类
     * @param field  对象属性名
     * @param prefix 前缀值
     * @return
     */
    public static void addFieldPrefix(Object clazz, String field, String prefix) {
        if (DataUtil.isNotEmpty(clazz) && DataUtil.isNotEmpty(field) && field.length() > 1) {
            // 转换field首字母大写
            field = field.substring(0, 1).toUpperCase() + field.substring(1);
            // 获得属性原有值
            Object value = invokeMethod(clazz, "get" + field, null);
            if (DataUtil.isNotEmpty(value) && !"null".equals(value)) {
                prefix = prefix + value;
            }
            // 添加前缀后，赋值回对象中
            invokeMethod(clazz, "set" + field, prefix);
        } else {
            throw new RuntimeException("addFieldPrefix: null or wrong params !");
        }
    }

    /**
     * 给类属性添加前缀值
     *
     * @param list   目标对象集合
     * @param field  对象属性名
     * @param prefix 前缀值
     * @return
     */
    public static void addFieldPrefix(List list, String field, String prefix) {
        if (DataUtil.isNotEmpty(list)) {
            for (Object obj : list) {
                addFieldPrefix(obj, field, prefix);
            }
        }
    }

}
