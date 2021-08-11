package top.doublewin.core.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.doublewin.core.interceptor.JwtTokenInterceptor;
import top.doublewin.core.util.DataUtil;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 加载JWT拦截器的配置类
 * </p>
 *
 * @author migro
 * @since 2020/1/14 10:12
 */
public class JwtConfig implements WebMvcConfigurer {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${jwt.skip:false}", autoRefreshed = true)
    private boolean jwtSkip;
    @NacosValue(value = "${jwt.token:null}", autoRefreshed = true)
    private String jwtToken;
    @NacosValue(value = "${jwt.exclude.path:}", autoRefreshed = true)
    private String jwtExcludePath;

    /**
     * 默认JWT排除url
     */
    private String DEFAULT_JWT_EXCLUDE_PATH = "/**/*login*,/**/swagger**/**,/**/api-docs,/**/upload**/**,/**/receiveCode,/**/**.**";

    /**
     * J添加JWT处理拦截器
     *
     * @param
     * @return
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.debug("jwt.skip value is {}", jwtSkip);
        logger.debug("jwt.token value is {}", jwtToken);
        logger.debug("jwt.exclude path is {}", jwtExcludePath);
        JwtTokenInterceptor interceptor = new JwtTokenInterceptor();
        interceptor.setJwtSkip(jwtSkip);
        interceptor.setJwtToken(jwtToken);
        if(DataUtil.isEmpty(jwtExcludePath)){
            jwtExcludePath = DEFAULT_JWT_EXCLUDE_PATH;
        }
        List<String> excludeList = Arrays.asList(jwtExcludePath.split(","));
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(excludeList);
        logger.info("=======================     完成JWT拦截器加载     =========================");
    }

}
