package top.doublewin.core.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.doublewin.core.util.WeChatUtil;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 微信小程序接口配置
 * </p>
 *
 * @author migro
 * @since 2020/9/15 17:05
 */
public class WxMiniAppConfig {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${miniapp.appid}", autoRefreshed = true)
    private String appId;
    @NacosValue(value = "${miniapp.secret}", autoRefreshed = true)
    private String secret;

    @PostConstruct
    public void init() {
        WeChatUtil.setMaAppId(appId);
        WeChatUtil.setMaSecret(secret);
        logger.info("=======================     完成微信小程序配置初始化      =========================");
    }

}
