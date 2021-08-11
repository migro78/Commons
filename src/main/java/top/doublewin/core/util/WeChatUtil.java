package top.doublewin.core.util;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * 微信公共网关调用辅助类
 * </p>
 *
 * @author migro
 * @since 2020/7/17 10:18
 */
public class WeChatUtil {
    protected static Logger logger = LogManager.getLogger();

    /**
     * 微信公众号配置参数
     */
    private static String appId;
    private static String secret;
    private static String token;
    private static String aeskey;

    /**
     * 微信小程序配置参数
     */
    private static String maAppId;
    private static String maSecret;


    /**
     * 微信公众号mp辅助类
     */
    private static WxMpService wxMpService;

    /**
     * 微信小程序ma辅助类
     */
    private static WxMaService wxMaService;

    /**
     * 获得公众号处理服务实例
     * @param
     * @return
     */
    public static WxMpService getMpInstance() {
        if (null != wxMpService) {
            return wxMpService;
        }
        synchronized (WeChatUtil.class) {
            if (null == wxMpService) {
                WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
                // 设置微信公众号的appid
                config.setAppId(appId);
                // 设置微信公众号的app corpSecret
                config.setSecret(secret);
                // 设置微信公众号的token
                config.setToken(token);
                // 设置微信公众号的EncodingAESKey
                config.setAesKey(aeskey);
                //  实例化mp服务类
                wxMpService = new WxMpServiceImpl();
                wxMpService.setWxMpConfigStorage(config);
            }
        }
        return wxMpService;
    }

    /**
     * 获得小程序处理服务实例
     * @param
     * @return
     */
    public static WxMaService getMaInstance() {
        if (null != wxMaService) {
            return wxMaService;
        }
        synchronized (WeChatUtil.class) {
            if (null == wxMaService) {
                WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
                // 设置微信小程序的appid
                config.setAppid(maAppId);
                // 设置微信小程序的app corpSecret
                config.setSecret(maSecret);
                //  实例化ma服务类
                wxMaService = new WxMaServiceImpl();
                wxMaService.setWxMaConfig(config);
            }
        }
        return wxMaService;
    }



    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        WeChatUtil.appId = appId;
    }

    public static String getSecret() {
        return secret;
    }

    public static void setSecret(String secret) {
        WeChatUtil.secret = secret;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        WeChatUtil.token = token;
    }

    public static String getAeskey() {
        return aeskey;
    }

    public static void setAeskey(String aeskey) {
        WeChatUtil.aeskey = aeskey;
    }

    public static String getMaAppId() {
        return maAppId;
    }

    public static void setMaAppId(String maAppId) {
        WeChatUtil.maAppId = maAppId;
    }

    public static String getMaSecret() {
        return maSecret;
    }

    public static void setMaSecret(String maSecret) {
        WeChatUtil.maSecret = maSecret;
    }
}
