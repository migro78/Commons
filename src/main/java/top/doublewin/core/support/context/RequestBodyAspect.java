package top.doublewin.core.support.context;

/**
 * @Author: QinWen
 * @Description:
 * @Date: Created in 19:36 2018/7/3
 * @ModifiuedBy:
 */

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import top.doublewin.core.util.ExceptionUtil;
import top.doublewin.core.util.InstanceUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RequestBodyAspect {
    private final Logger logger = LogManager.getLogger();
    private static Map<Class<?>, Method[]> methodMap = InstanceUtil.newHashMap();

    @Pointcut("execution(* *..*.web..*Controller.*(..))")
    public void requestBody() {
    }

    /**
     * 前置通知,使用在方法aspect()上注册的切入点
     * @throws Throwable
     */
    @Before("requestBody()")
    public void before(JoinPoint pjp) {
        try {
            String methodName = pjp.getSignature().getName();
            Class<?> cls = pjp.getTarget().getClass();
            Method[] methods = getMethods(cls);
            L: for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Parameter[] ps = method.getParameters();
                    for (int i = 0; i < ps.length; i++) {
                        Parameter parameter = ps[i];
                        Object value = pjp.getArgs()[i];
                        RequestBody rb = parameter.getAnnotation(RequestBody.class);
                        if (rb != null) {
                            logger.info("请求方法 ===> {}" , cls.getName() + "." + methodName);
                            String body = JSON.toJSONString(value);
                            logger.info("请求内容 ===> {}", body);
                            break L;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTraceAsString(e));
        }
    }





    private Method[] getMethods(Class<?> cls) {
        if (methodMap.containsKey(cls)) {
            return methodMap.get(cls);
        }
        Method[] methods = cls.getDeclaredMethods();
        methodMap.put(cls, methods);
        return methods;
    }
}
