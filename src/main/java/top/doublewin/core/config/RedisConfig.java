package top.doublewin.core.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import top.doublewin.core.support.cache.RedisUtil;
import top.doublewin.core.util.CacheUtil;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 加载Redis内存数据库处理类
 * </p>
 *
 * @author migro
 * @since 2020/4/2 16:10
 */
public class RedisConfig {
    protected static Logger logger = LogManager.getLogger();
    @Autowired
    RedisUtil redisUtil;

    @PostConstruct
    public void initCacheUtil(){
        CacheUtil.setCache(redisUtil);
        logger.info("=======================     完成RedisConfig加载   =========================");
    }
}
