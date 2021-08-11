package top.doublewin.core.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.doublewin.core.util.WeChatUtil;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 微信公众号接口配置类
 * </p>
 *
 * @author migro
 * @since 2020/7/17 10:10
 */
public class WeChatConfig {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${wechat.appid}", autoRefreshed = true)
    private String appId;
    @NacosValue(value = "${wechat.secret}", autoRefreshed = true)
    private String secret;
    @NacosValue(value = "${wechat.token}", autoRefreshed = true)
    private String token;
    @NacosValue(value = "${wechat.aeskey}", autoRefreshed = true)
    private String aeskey;

    @PostConstruct
    public void init() {
        WeChatUtil.setAppId(appId);
        WeChatUtil.setSecret(secret);
        WeChatUtil.setToken(token);
        WeChatUtil.setAeskey(aeskey);
        logger.info("=======================     完成微信公众号配置初始化      =========================");
    }

}
