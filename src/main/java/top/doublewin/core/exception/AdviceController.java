package top.doublewin.core.exception;

/**
 * @Author: QinWen
 * @Description:
 * @Date: Created in 19:58 2018/7/3
 * @ModifiuedBy:
 */

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import top.doublewin.core.support.context.Constants;
import top.doublewin.core.support.http.HttpCode;

/**
 * @author ShenHuaJie
 * @since 2018年5月24日 上午9:24:09
 */
@ControllerAdvice
public class AdviceController {
    private Logger logger = LogManager.getLogger();


    /**
     * 异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ModelMap> exceptionHandler(Exception ex){
        logger.error(Constants.Exception_Head, ex);
        logger.info("===========exceptionHandler==========,,, exception class name {}",ex.getClass().getName());
        ModelMap modelMap = new ModelMap();
        if (ex instanceof BaseException) {
            ((BaseException) ex).handler(modelMap);
        } else if (ex instanceof IllegalArgumentException) {
            new IllegalParameterException(ex.getMessage()).handler(modelMap);
        } else if ("org.apache.shiro.authz.UnauthorizedException".equals(ex.getClass().getName())) {
            modelMap.put("code", HttpCode.FORBIDDEN.value().toString());
            modelMap.put("msg", HttpCode.FORBIDDEN.msg());
        } else if (ex instanceof NullPointerException) {
            modelMap.put("code", HttpCode.INTERNAL_SERVER_ERROR.value().toString());
            modelMap.put("msg", "空指针异常:" + ex.getStackTrace()[0].getClassName() + "." + ex.getStackTrace()[0].getMethodName() +
                    "(" + ex.getStackTrace()[0].getFileName() + ":" + ex.getStackTrace()[0].getLineNumber() + ")");
        } else if (ex instanceof ExpiredJwtException) {
            modelMap.put("code", HttpCode.UNAUTHORIZED.value().toString());
            modelMap.put("msg", "您还没有登录" );
        }else {
            modelMap.put("code", HttpCode.INTERNAL_SERVER_ERROR.value().toString());
            String msg = StringUtils.defaultIfBlank(ex.getMessage(), HttpCode.INTERNAL_SERVER_ERROR.msg());
            logger.debug(msg);
            modelMap.put("msg", msg.length() > 200 ? msg.substring(0, 200) : msg);
        }
        modelMap.put("timestamp", System.currentTimeMillis());
        logger.info("response===>" + JSON.toJSON(modelMap));
        return ResponseEntity.ok(modelMap);
    }

}
