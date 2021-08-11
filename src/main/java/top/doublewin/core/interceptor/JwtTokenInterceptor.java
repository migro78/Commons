package top.doublewin.core.interceptor;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.doublewin.core.support.context.Constants;
import top.doublewin.core.support.http.HttpCode;
import top.doublewin.core.util.JwtUtil;
import top.doublewin.core.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * JWT Token验证处理器
 * </p>
 *
 * @author migro
 * @since 2019/3/28 18:29
 */
public class JwtTokenInterceptor extends HandlerInterceptorAdapter {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${jwt.skip:false}", autoRefreshed = true)
    private boolean jwtSkip;
    @NacosValue(value = "${jwt.token:null}", autoRefreshed = true)
    private String jwtToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean flag = false;
        logger.info("请求地址 ===> {}",request.getServletPath());

        // 获取 HTTP HEAD 中的 TOKEN
        String authorization = request.getHeader("Authorization");
        // 作弊设置，开发调试时可配置使用固定JWT串跳过验证环节
        if(jwtSkip){
            logger.info("################################################################################");
            logger.info("#####   启用跳过JWT验证，使用配置中Token验证，如提示过期，请更新配置中Token   #####");
            logger.info("################################################################################");
            authorization = jwtToken;
        }

        // 校验 TOKEN
        flag = StringUtils.isNotBlank(authorization) ? JwtUtil.checkJWT(authorization) : false;
        // 如果校验未通过，返回 401 状态
        if (!flag) {
            logger.info("JWT TOKEN 验证失败！Authorization = {}，请求地址 ===> {}",authorization,request.getServletPath());
            WebUtil.write(response, HttpCode.UNAUTHORIZED.value(), HttpCode.UNAUTHORIZED.msg());
            return false;
        }

        // 解析用户信息
        Jws<Claims> jws = JwtUtil.parseJWT(authorization);
        request.setAttribute(Constants.CURRENT_SESSION_USER,jws.getBody().getSubject());
        logger.debug("从JWTToken中解析出的当前登录用户为====>>>>>>> {}",jws.getBody().getSubject());

        return flag;
    }

    public boolean isJwtSkip() {
        return jwtSkip;
    }

    public void setJwtSkip(boolean jwtSkip) {
        this.jwtSkip = jwtSkip;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
