package top.doublewin.core.interceptor;


import top.doublewin.core.support.cache.CacheKey;
import top.doublewin.core.util.InstanceUtil;

import java.util.Map;

/**
 * 常量表
 * 
 * @author ShenHuaJie
 * @version $Id: Constants.java, v 0.1 2014-2-28 上午11:18:28 ShenHuaJie Exp $
 */
public interface Constants {
    /**
     * 异常信息统一头信息<br>
     * 非常遗憾的通知您,程序发生了异常
     */
    static final String Exception_Head = "出现异常! 详细信息如下:";
    /** 当前会话用户键值 */
    static final String CURRENT_SESSION_USER = "CURRENT_SESSION_USER";


    /** 缓存键值 */
    static final Map<Class<?>, CacheKey> cacheKeyMap = InstanceUtil.newHashMap();
    /** 操作名称 */
    static final String OPERATION_NAME = "OPERATION_NAME";
    /** 客户端语言 */
    static final String USERLANGUAGE = "userLanguage";
    /** 客户端主题 */
    static final String WEBTHEME = "webTheme";
    /** 当前用户 */
    static final String CURRENT_USER = "CURRENT_USER";
    /** 当前用户 */
    static final String CURRENT_APP_USER = "CURRENT_APP_USER";
    /** 客户端信息 */
    static final String USER_AGENT = "USER-AGENT";
    /** 客户端信息 */
    static final String USER_IP = "USER_IP";
    /** 登录地址 */
    static final String LOGIN_URL = "/login.html";
    /** 缓存命名空间 */
    static final String CACHE_NAMESPACE = "doubleWin:";
    /** 缓存命名空间 */
    static final String SYSTEM_CACHE_NAMESPACE = "S:doubleWin:";
    /** 缓存命名空间 */
    static final String CACHE_NAMESPACE_LOCK = "L:doubleWin:";
    /** 上次请求地址 */
    static final String PREREQUEST = CACHE_NAMESPACE + "PREREQUEST";
    /** 上次请求时间 */
    static final String PREREQUEST_TIME = CACHE_NAMESPACE + "PREREQUEST_TIME";
    /** 非法请求次数 */
    static final String MALICIOUS_REQUEST_TIMES = CACHE_NAMESPACE + "MALICIOUS_REQUEST_TIMES";
    /** 在线用户数量 */
    static final String ALLUSER_NUMBER = SYSTEM_CACHE_NAMESPACE + "ALLUSER_NUMBER";
    /** TOKEN */
    static final String TOKEN_KEY = SYSTEM_CACHE_NAMESPACE + "TOKEN_KEY:";
    /** shiro cache */
    static final String REDIS_SHIRO_CACHE = SYSTEM_CACHE_NAMESPACE + "SHIRO-CACHE:";
    /** SESSION */
    static final String REDIS_SHIRO_SESSION = SYSTEM_CACHE_NAMESPACE + "SHIRO-SESSION:";
    /** CACHE */
    static final String MYBATIS_CACHE = "D:doubleWin:MYBATIS:";
    /** 默认数据库密码加密key */
    static final String DB_KEY = "90139119";
    /** 临时目录 */
    static final String TEMP_DIR = "/temp/";
    /** 请求报文体 */
    static final String REQUEST_BODY = "DoubleWin.requestBody";
    static final String SSO_USER_KEY = "SSO_USER_KEY:";
    static final String APP_TOKEN_EXPIRE = "app_token_expire";
    static final String APP_SMS_EXPIRE = "app_sms_expire";
    static final String REGISTRY_MSGCENTER = "/registry/msgcenter";
    static final String WEBSOCKETPATHPERFIX = "/send";
    /** 点对点 */
    static final String P2PPUSHBASEPATH = "/p2p";//点对点消息推送地址前缀
    static final String P2PPUSHPATH = "/msg";//点对点消息推送地址后缀,地址: /user/用户识别码/msg
    static final String MSGMAPPING_BROADCAST= "/broadcast";
    static final String RESPONSE_BROADCAST = "/broadcast/response";
    /** 默认密码 */
    static final String DEFAULT_PASSWORD = "123456";
    /** 积分规则 */
    String SCORE_RULES_KEY = "SCORE_RULES_MAP";
    /** 消息配置 */
    String MESSAGE_CONFIG_KEY = "MESSAGE_CONFIG_MAP";
    /**微信access_token**/
    static final String WECHAT_USER_ACCESS_TOKEN = "WECHAT_USER_ACCESS_TOKEN";


    //系统通知
    static final String MQ_SYSNOTICE="MQ.SYSNOTICE";
    //短消息
    static final String MQ_SMS  = "MQ.SMS";
    //Email
    static final String MQ_EMAIL ="MQ.EMAIL";
    //微信公众号
    static final String MQ_WXPUBLIC = "MQ.WXPUBLIC";
    //微信小程序
    static final String MQ_WXMINI = "MQ.WXMINI";
    //信鸽推送程序
    static final String MQ_XGPUSH = "MQ.XGPUSH";

   public static enum MessageType {
        INFO,
        WARNING,
        ERROR
    }

    /** 日志表状态 */
    interface JOBSTATE {
        /**
         * 日志表状态，初始状态，插入
         */
        static final String INIT_STATS = "I";
        /**
         * 日志表状态，成功
         */
        static final String SUCCESS_STATS = "S";
        /**
         * 日志表状态，失败
         */
        static final String ERROR_STATS = "E";
        /**
         * 日志表状态，未执行
         */
        static final String UN_STATS = "N";
    }

    /** 短信验证码类型 */
    public interface MSGCHKTYPE {
        /** 注册 */
        public static final String REGISTER = CACHE_NAMESPACE + "REGISTER:";
        /** 登录 */
        public static final String LOGIN = CACHE_NAMESPACE + "LOGIN:";
        /** 修改密码验证码 */
        public static final String CHGPWD = CACHE_NAMESPACE + "CHGPWD:";
        /** 身份验证验证码 */
        public static final String VLDID = CACHE_NAMESPACE + "VLDID:";
        /** 信息变更验证码 */
        public static final String CHGINFO = CACHE_NAMESPACE + "CHGINFO:";
        /** 活动确认验证码 */
        public static final String AVTCMF = CACHE_NAMESPACE + "AVTCMF:";
    }

    public interface TIMES {
        static final long SECOND = 1000; // 1秒 java已毫秒为单位
        static final long MINUTE = SECOND * 60; // 一分钟
        static final long HOUR = MINUTE * 60; // 一小时
        static final long DAY = HOUR * 24; // 一天
        static final long WEEK = DAY * 7; // 一周
        static final long YEAR = DAY * 365; // 一年
    }
}
