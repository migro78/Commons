package top.doublewin.core.support.cache;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisHash;
import top.doublewin.core.support.context.Constants;
import top.doublewin.core.util.DataUtil;

public class CacheKey {
    private String value;

    public CacheKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static CacheKey getInstance(Class<?> cls) {
        CacheKey cackKey = Constants.cacheKeyMap.get(cls);
        if (DataUtil.isEmpty(cackKey)) {
            String cacheName;
            RedisHash redisHash = null;
//            ParameterizedType parameterizedType = (ParameterizedType) cls.getGenericSuperclass();
//            if (parameterizedType != null) {
//                Type[] actualTypes = parameterizedType.getActualTypeArguments();
//                if (actualTypes != null && actualTypes.length > 0) {
//                    // 实体注解@RedisHash
//                    redisHash = actualTypes[0].getClass().getAnnotation(RedisHash.class);
//                }
//            }
            if (redisHash != null) {
                cacheName = redisHash.value();
            } else {
                // Service注解CacheConfig
                CacheConfig cacheConfig = cls.getAnnotation(CacheConfig.class);
                if (cacheConfig != null && ArrayUtils.isNotEmpty(cacheConfig.cacheNames())) {
                    cacheName = cacheConfig.cacheNames()[0];
                } else {
                    cacheName = cls.getName();
                }
            }
            cackKey = new CacheKey(Constants.CACHE_NAMESPACE + cacheName);
            Constants.cacheKeyMap.put(cls, cackKey);
        }
        return cackKey;
    }
}

