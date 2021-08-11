package top.doublewin.core.support.wechat;


import com.alibaba.fastjson.JSONObject;
import top.doublewin.core.util.CacheUtil;
import top.doublewin.core.util.DataUtil;
import top.doublewin.core.util.HttpUtil;
import top.doublewin.core.util.InstanceUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 微信公众平台公共接口调用支持类
 * </p>
 *
 * @author migro
 * @since 2019/1/22 15:01
 */
public class WechatCgi {


    /**
     * 获取Token
     *
     * @param appId
     * @param secret
     * @param code
     * @return
     */
    public static Map<String,Object> getAccessToken(String appId, String secret,String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + appId + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        String ret = HttpUtil.get(url);
        return JSONObject.parseObject(ret);
    }

    /**
     * 获取AccessToken
     *
     * @param appId
     * @param secret
     * @return
     */
    public static Map<String,Object> getAccessToken(String appId, String secret) {
        String accessToken = (String) CacheUtil.getCache().get("accessToken");
        if(DataUtil.isEmpty(accessToken)){
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
                    appId + "&secret=" + secret;
            String ret = HttpUtil.get(url);
            Map<String,Object> retData = JSONObject.parseObject(ret);
            accessToken = (String) retData.get("access_token");
            if(DataUtil.isNotEmpty(accessToken)){
                //失效7200
                CacheUtil.getCache().setEx("accessToken",accessToken,3600,TimeUnit.SECONDS);
            }
            return retData;
        }else {
            return InstanceUtil.newHashMap("access_token",accessToken);
        }
    }

    /**
     * 刷新Token
     *
     * @param appId
     * @param refreshToken
     * @return
     */
    public static Map<String,Object> refreshAccessToken(String appId, String refreshToken) {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + appId + "&grant_type=refresh_token&refresh_token=" + refreshToken;
        String ret = HttpUtil.get(url);
        return JSONObject.parseObject(ret);
    }


    /**
     * 获取用户信息
     *
     * @param accessToken
     * @param openid
     * @return
     */
    public static Map<String,Object> getUserInfo(String accessToken, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
        String ret = HttpUtil.get(url);
        return JSONObject.parseObject(ret);
    }
}
