package top.doublewin.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.util.WebUtils;
import top.doublewin.core.support.context.Constants;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * Web层辅助类
 * </p>
 *
 * @author migro
 * @since ${DATE} ${TIME}
 */
public final class WebUtil {
    private WebUtil() {
    }

    private static Logger logger = LogManager.getLogger();

    /**
     * 获取当前会话用户信息对象
     *
     * @param
     * @return
     */
    public static final <T> T getSessionUser(HttpServletRequest request, Class<T> userClass) {
        T o = null;
        if (DataUtil.isNotEmpty(userClass)) {
            String userJson = (String) request.getAttribute(Constants.CURRENT_SESSION_USER);
            o = JSON.parseObject(userJson, userClass);
        }
        return o;
    }

    public static final Long getSessionUserId(HttpServletRequest request) {
        String userJson = (String) request.getAttribute(Constants.CURRENT_SESSION_USER);
        if(DataUtil.isEmpty(userJson)){
            logger.debug("当前会话用户信息为空! user_json is {}",userJson);
            return null;
        }
        JSONObject obj = JSON.parseObject(userJson);
        String id = obj.getString("id");
        return Long.parseLong(id);
    }

    public static final String getSessionUserOpenId(HttpServletRequest request) {
        String userJson = (String) request.getAttribute(Constants.CURRENT_SESSION_USER);
        JSONObject obj = JSON.parseObject(userJson);
        String openId = obj.getString("openId");
        return openId;
    }

    /**
     * 获取指定Cookie的值
     *
     * @param request
     * @param cookieName   cookie名字
     * @param defaultValue 缺省值
     * @return
     */
    public static final String getCookieValue(HttpServletRequest request, String cookieName, String defaultValue) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return defaultValue;
        }
        return cookie.getValue();
    }


    /**
     * 写出响应
     */
    public static boolean write(ServletResponse response, Integer code, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> modelMap = InstanceUtil.newLinkedHashMap();
        modelMap.put("code", code.toString());
        modelMap.put("msg", msg);
        modelMap.put("timestamp", System.currentTimeMillis());
        logger.info("****响应===>" + JSON.toJSON(modelMap));
        response.getOutputStream().write(JSON.toJSONBytes(modelMap, SerializerFeature.DisableCircularReferenceDetect));
        return false;
    }

    /**
     * 写出响应
     */
    public static void write(ServletResponse response, Map<String, Object> modelMap) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        modelMap.put("timestamp", System.currentTimeMillis());
        logger.info("****响应===>" + JSON.toJSON(modelMap));
        response.getOutputStream().write(JSON.toJSONBytes(modelMap, SerializerFeature.DisableCircularReferenceDetect));
    }



    /**
     * 判断是否是白名单
     */
    public static boolean isWhiteRequest(String url, int size, List<String> whiteUrls) {
        if (url == null || "".equals(url) || size == 0) {
            return true;
        } else {
            url = url.toLowerCase();
            for (String urlTemp : whiteUrls) {
                //System.out.println("url="+url+",whiteurl="+urlTemp);
                if (url.indexOf(urlTemp.toLowerCase()) > -1) {
                    //System.out.println("白名单=true");
                    return true;
                }
            }
        }
        return false;
    }

}
