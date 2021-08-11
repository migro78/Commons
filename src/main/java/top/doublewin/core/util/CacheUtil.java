package top.doublewin.core.util;

import top.doublewin.core.support.cache.RedisUtil;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2020/4/2 16:06
 */
public class CacheUtil {
    private static RedisUtil redisUtil;

    public static RedisUtil getCache(){
        return redisUtil;
    }

    public static void setCache(RedisUtil util){
        redisUtil = util;
    }
}
