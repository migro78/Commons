package top.doublewin.core.util;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.doublewin.core.exception.BusinessException;
import top.doublewin.core.support.context.RegexType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常见的辅助类
 *
 * @author ShenHuaJie
 * @since 2011-11-08
 */
public final class DataUtil {
    private DataUtil() {
    }

    private static Logger logger = LogManager.getLogger();



    /**
     * 十进制字节数组转十六进制字符串
     *
     * @param b
     * @return
     */
    public static final String byte2hex(byte[] b) { // 一个字节数，转成16进制字符串
        StringBuilder hs = new StringBuilder(b.length * 2);
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString(); // 转成大写
    }

    /**
     * 十六进制字符串转十进制字节数组
     *
     * @param hs
     * @return
     */
    public static final byte[] hex2byte(String hs) {
        byte[] b = hs.getBytes();
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个十进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 这个方法可以通过与某个类的class文件的相对路径来获取文件或目录的绝对路径。 通常在程序中很难定位某个相对路径，特别是在B/S应用中。
     * 通过这个方法，我们可以根据我们程序自身的类文件的位置来定位某个相对路径。
     * 比如：某个txt文件相对于程序的Test类文件的路径是../../resource/test.txt，
     * 那么使用本方法Path.getFullPathRelateClass("../../resource/test.txt",Test.class)
     * 得到的结果是txt文件的在系统中的绝对路径。
     *
     * @param relatedPath 相对路径
     * @param cls         用来定位的类
     * @return 相对路径所对应的绝对路径
     */
    public static final String getFullPathRelateClass(String relatedPath, Class<?> cls) {
        String path = null;
        if (relatedPath == null) {
            throw new NullPointerException();
        }
        String clsPath = getPathFromClass(cls);
        File clsFile = new File(clsPath);
        String tempPath = clsFile.getParent() + File.separator + relatedPath;
        File file = new File(tempPath);
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取class文件所在绝对路径
     *
     * @param cls
     * @return
     */
    public static final String getPathFromClass(Class<?> cls) {
        String path = null;
        if (cls == null) {
            throw new NullPointerException();
        }
        URL url = getClassLocationURL(cls);
        if (url != null) {
            path = url.getPath();
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                try {
                    path = new URL(path).getPath();
                } catch (MalformedURLException e) {
                }
                int location = path.indexOf("!/");
                if (location != -1) {
                    path = path.substring(0, location);
                }
            }
            File file = new File(path);
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 判断对象是否Empty(null或元素为0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    public static final boolean isEmpty(Object pObj) {
        if (pObj == null || "".equals(pObj)) {
            return true;
        }
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection<?>) {
            if (((Collection<?>) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map<?, ?>) {
            if (((Map<?, ?>) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 批量判断变量是否为Empty
     *
     * @param objs 多个对象变量
     * @return
     */
    public static final boolean isAnyEmpty(Object... objs) {
        List ret = InstanceUtil.newArrayList();
        Arrays.stream(objs).forEach(t -> {
            if (DataUtil.isEmpty(t)) {
                ret.add(t);
            }
        });
        return ret.size() > 0;
    }

    /**
     * 判断对象是否为NotEmpty(!null或有元素)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    public static final boolean isNotEmpty(Object pObj) {
        if (pObj == null || "".equals(pObj)) {
            return false;
        }
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return false;
            }
        } else if (pObj instanceof Collection<?>) {
            if (((Collection<?>) pObj).size() == 0) {
                return false;
            }
        } else if (pObj instanceof Map<?, ?>) {
            if (((Map<?, ?>) pObj).size() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * JS输出含有\n的特殊处理
     *
     * @param pStr
     * @return
     */
    public static final String replace4JsOutput(String pStr) {
        pStr = pStr.replace("\r\n", "<br/>&nbsp;&nbsp;");
        pStr = pStr.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        pStr = pStr.replace(" ", "&nbsp;");
        return pStr;
    }

    /**
     * 分别去空格
     *
     * @param paramArray
     * @return
     */
    public static final String[] trim(String[] paramArray) {
        if (ArrayUtils.isEmpty(paramArray)) {
            return paramArray;
        }
        String[] resultArray = new String[paramArray.length];
        for (int i = 0; i < paramArray.length; i++) {
            String param = paramArray[i];
            resultArray[i] = StringUtils.trim(param);
        }
        return resultArray;
    }

    /**
     * 获取类的class文件位置的URL
     *
     * @param cls
     * @return
     */
    private static URL getClassLocationURL(final Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null input: cls");
        }
        URL result = null;
        final String clsAsResource = cls.getName().replace('.', '/').concat(".class");
        final ProtectionDomain pd = cls.getProtectionDomain();
        if (pd != null) {
            final CodeSource cs = pd.getCodeSource();
            if (cs != null) {
                result = cs.getLocation();
            }
            if (result != null) {
                if ("file".equals(result.getProtocol())) {
                    try {
                        if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip")) {
                            result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
                        } else if (new File(result.getFile()).isDirectory()) {
                            result = new URL(result, clsAsResource);
                        }
                    } catch (MalformedURLException ignore) {
                    }
                }
            }
        }
        if (result == null) {
            final ClassLoader clsLoader = cls.getClassLoader();
            result = clsLoader != null ? clsLoader.getResource(clsAsResource)
                    : ClassLoader.getSystemResource(clsAsResource);
        }
        return result;
    }

    /**
     * 初始化设置默认值
     */
    public static final <K> K nvl(K k, K defaultValue) {
        if (k == null) {
            return defaultValue;
        }
        return k;
    }

    public static String xssEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '>':
                    sb.append('＞');// 全角大于号
                    break;
                case '<':
                    sb.append('＜');// 全角小于号
                    break;
                case '\'':
                    sb.append('‘');// 全角单引号
                    break;
                case '\"':
                    sb.append('“');// 全角双引号
                    break;
                case '&':
                    sb.append('＆');// 全角
                    break;
                case '\\':
                    sb.append('＼');// 全角斜线
                    break;
                case '#':
                    sb.append('＃');// 全角井号
                    break;
                case '(':
                    sb.append('（');//
                    break;
                case ')':
                    sb.append('）');//
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 检查是否存在非法字符，防止SQL注入
     *
     * @param str 被检查的字符串
     * @return ture-字符串中存在非法字符，false-不存在非法字符
     */
    public static boolean checkSQLInject(String str) {
        // 判断黑名单
        String[] inj_stra = {"script", "mid", "master", "truncate", "insert", "select", "delete", "update ", "declare",
                "iframe", "'", "onreadystatechange", "alert", "atestu", "xss", ";", "'", "\"", "<", ">", "(", ")", "\\",
                "svg", "confirm", "prompt", "onload", "onmouseover", "onfocus", "onerror"};
        str = str.toLowerCase(); // sql不区分大小写

        for (int i = 0; i < inj_stra.length; i++) {
            if (str.indexOf(inj_stra[i]) >= 0) {
                logger.info("传入str=" + str + ",包含特殊字符：" + inj_stra[i]);
                return true;
            }
        }
        return false;
    }

    /**
     * 下划线转驼峰法
     *
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        if (line.contains("_")) {
            return line.toUpperCase();
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    /**
     * 判断是否是正确的IP地址
     *
     * @param ip
     * @return boolean true,通过，false，没通过
     */
    public static boolean isIp(String ip) {
        if (isEmpty(ip)) {
            return false;
        }
        return ip.matches(RegexType.IP.value());
    }

    /**
     * 判断是否是正确的邮箱地址
     *
     * @param email
     * @return boolean true,通过，false，没通过
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return email.matches(RegexType.EMAIL.value());
    }

    /**
     * 判断是否正整数
     *
     * @param number 数字
     * @return boolean true,通过，false，没通过
     */
    public static boolean isNumber(String number) {
        if (isEmpty(number)) {
            return false;
        }
        return number.matches(RegexType.NUMBER.value());
    }

    /**
     * 判断几位小数
     *
     * @param decimal 数字
     * @param count   小数位数
     * @return boolean true,通过，false，没通过
     */
    public static boolean isDecimal(String decimal, int count) {
        if (isEmpty(decimal)) {
            return false;
        }
        String regex = "^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){" + count + "})?$";
        return decimal.matches(regex);
    }

    /**
     * 判断是否是手机号码
     *
     * @param phoneNumber 手机号码
     * @return boolean true,通过，false，没通过
     */
    public static boolean isPhone(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return false;
        }
        return phoneNumber.matches(RegexType.PHONE.value());
    }

    /**
     * 验证固话号码
     *
     * @param telephone
     * @return
     */
    public static boolean isTelephone(String telephone) {
        if (isEmpty(telephone)) {
            return false;
        }
        return telephone.matches(RegexType.TELEPHONE.value());
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param text
     * @return boolean true,通过，false，没通过
     */
    public static boolean hasSpecialChar(String text) {
        if (isEmpty(text)) {
            return false;
        }
        if (text.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
            // 如果不包含特殊字符
            return true;
        }
        return false;
    }

    /**
     * 强密码验证
     *
     * @param value
     * @return
     */
    public static boolean isPassword(String value) {
        if (isEmpty(value)) {
            return false;
        }
        return value.matches(RegexType.PASSWORD.value());
    }

    /**
     * 判断是否含有中文，仅适合中国汉字，不包括标点
     *
     * @param text
     * @return boolean true,通过，false，没通过
     */
    public static boolean isChinese(String text) {
        if (isEmpty(text)) {
            return false;
        }
        Pattern p = Pattern.compile(RegexType.CHINESE.value());
        Matcher m = p.matcher(text);
        return m.find();
    }

    /**
     * 适应CJK（中日韩）字符集，部分中日韩的字是一样的
     */
    public static boolean isChinese2(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static String arrayToString(String[] pa) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < pa.length; i++) {
            if (i == 0) {
                s.append(pa[i]);
            } else {
                s.append("," + pa[i]);
            }
        }
        return s.toString();
    }


    /**
     * 参数字符串转map
     *
     * @param
     * @return
     */
    public static Map<String, String> stringToMap(String str, boolean decode) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String[] strs = str.split("&");
        for (String str2 : strs) {
            String[] ss = str2.split("=");
            if (decode) {
                ss[1] = URLDecoder.decode(ss[1], "utf-8");
            }
            map.put(ss[0], ss[1]);
        }
        return map;
    }

    /**
     * map转参数字符串
     *
     * @param
     * @return
     */
    public static String mapToString(Map map) {
        StringBuffer buffer = new StringBuffer();
        if (DataUtil.isNotEmpty(map)) {
            for (Object key : map.keySet()) {
                if (buffer.length() != 0) {
                    buffer.append("&");
                }
                buffer.append(key);
                buffer.append("=");
                buffer.append(map.get(key));
            }
        }
        return buffer.toString();
    }

    /**
     * 字符串掩码处理
     *
     * @param strText  原始字符串
     * @param start    开始索引
     * @param end      结束索引
     * @param maskChar 掩码字符串
     * @return
     */
    public static String maskString(String strText, int start, int end, String maskChar) {
        if (DataUtil.isEmpty(strText)) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end > strText.length()) {
            end = strText.length();
        }
        if (start > end) {
            return ("结束索引不能大于开始索引");
        }
        int maskLength = end - start;
        if (maskLength == 0) {
            return strText;
        }
        String strMaskString = StringUtils.repeat(maskChar, maskLength);
        return StringUtils.overlay(strText, strMaskString, start, end);
    }

    /**
     * @Author aw
     * @Description 列表查询处理时间
     */
    public static void operatingTime(List<String> splitDate, Map<String, Object> param) {
        if (DataUtil.isNotEmpty(splitDate)) {
            splitDate.forEach(t -> {
                if (DataUtil.isNotEmpty(param.get(t))) {
                    List<String> list;
                    if (param.get(t) instanceof JSONArray) {
                        list = JSONArray.parseArray(JSONArray.toJSONString(param.get(t)), String.class);
                    } else {
                        String str = (String) param.get(t);
                        list = Arrays.asList(str.split(","));
                    }
                    param.put(t + "Start", DateUtil.stringToDate(list.get(0)));
                    param.put(t + "End", DateUtil.stringToDate(list.get(1).trim().length() < 11 ? list.get(1).trim() + " 23:59:59" : list.get(1).trim()));
                    param.remove(t);
                }
            });
        }
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return "";
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    public static void resolveTimeClass(Map<String, Object> param) {
        resolveTimeClass(param, null, 1, 1, 2, 3, 3, 6);
    }

    public static void resolveTimeClass(Map<String, Object> param, String strName, Integer... tClass) {
        if (tClass.length % 2 == 0) {
            strName = strName == null ? "timeClass" : strName;
            Integer timeClass = (Integer) param.get(strName);
            if (timeClass != null) {
                for (int i = 0; i < tClass.length; i += 2) {
                    if (tClass[i].equals(timeClass)) {
                        param.put("timeClass", tClass[i + 1]);
                    }
                }
            }
        } else {
            throw new BusinessException("参数异常");
        }
    }

    /**
     * 模板字符串中参数替换，参数源为变长数组，参数格式 {}
     * @param template 模板字符串
     * @param params 参数源数组
     * @return
     */
    public static String paramsFill(String template, String... params) {
        if (DataUtil.isNotEmpty(template) && DataUtil.isNotEmpty(params)) {
            for (int i = 0; i < params.length; i++) {
                template = template.replaceFirst("\\{\\}", params[i]);
            }
        }
        return template;
    }

    /**
     * 模板字符串中参数替换，参数源Map，参数格式 ${name}
     * @param template 模板字符串
     * @param map 参数源
     * @return
     */
    public static String paramsFill(String template, Map<String, Object> map) {
        Matcher m = PatternUtil.paramPattern.matcher(template);
        StringBuffer buffer = new StringBuffer();
        String value = null;
        while (m.find()) {
            String group = m.group();
            String express = group.substring(2, group.length() - 1);
            // 解析模板，并且获取含有参数的值，如果是计算表达式，则进行计算
            value = parseParamAndCalc(express,map);

            m.appendReplacement(buffer, value);
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 参数解析：将带参数的计算表达式解析为数值的计算表达式
     * @param param 带参数的计算表达式
     * @param map 参数值存放集合
     * @return 数值计算表达式
     */
    private static String parseParamAndCalc(String param,Map<String, Object> map){
        if(DataUtil.isEmpty(param)){
            return null;
        }
        String ret = null;
        logger.debug("原始参数表达式  {}",param);
        // 判断参数表达式是否带有计算符号
        StringBuffer buffer = new StringBuffer();
        if(PatternUtil.operSymbolPattern.matcher(param).find()){
            // 带有运算符号,解析参数
            Matcher m = PatternUtil.fourMixedWithParamPattern.matcher(param);
            while (m.find()) {
                String group = m.group();
                String value = null;
                if(group.matches("[a-zA-Z]\\w+")){
                    // 如果是参数，则从集合中获取参数值
                    value = paramsGet(group,map);
                    m.appendReplacement(buffer, value);
                }
            }
            m.appendTail(buffer);
            // 获得数值计算表达式
            logger.debug("数值计算表达式  {}",buffer.toString());
            try {
                ret = MathUtil.fourMixedOperations(buffer.toString());
            } catch (Exception e) {
                logger.error("四则运算出错！",e);
            }

        } else {
            // 未带运算符号，直接获取参数对应的值
            ret = paramsGet(param.trim(),map);
        }
        logger.debug("表达式结果值  {}",ret);
        return ret;
    }

    /**
     * 从Map集合中获取参数对应值
     * @param
     * @return
     */
    private static String paramsGet(Object param,Map<String, Object> map){
        Object object = map.get(param);
        String value = null;
        // 数据类型处理
        if(DataUtil.isNotEmpty(object)){
            if(object instanceof Date){
                // 处理日期类型
                value = DateUtil.format(object,"yyyy-MM-dd HH:mm:ss");
                if(value.contains("00:00:00")){
                    value = value.substring(0,10);
                }
            } else {
                value = object.toString();
            }
        } else {
            value = "";
        }
        logger.debug("paramsGet=======>>>>{}=={}",param,value);
        return value;
    }

    public static void main(String[] args) {
        String str = "这是参数 1{} 2{} 3{}";
        String[] param = {"一", "二", "三"};
        //System.out.println(paramsFill(str, "1", "2", "3", "4"));
        String s = "creAteTime";
        System.out.println(DataUtil.camel2Underline(s));
    }

}
