package top.doublewin.core.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.doublewin.core.minio.MinioClientUtils;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 加载MinIO对象存储配置类
 * </p>
 *
 * @author migro
 * @since 2020/4/3 10:28
 */
public class MinioConfig {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${minio.url}", autoRefreshed = true)
    private String url;
    @NacosValue(value = "${minio.access.key}", autoRefreshed = true)
    private String username;
    @NacosValue(value = "${minio.secret.key}", autoRefreshed = true)
    private String password;
    @NacosValue(value = "${minio.project.name}", autoRefreshed = true)
    private String bucketName;

    @PostConstruct
    public void init() {
        MinioClientUtils.setBucketName(bucketName);
        MinioClientUtils.setPassword(password);
        MinioClientUtils.setUrl(url);
        MinioClientUtils.setUsername(username);
        logger.info("=======================     完成MinIO 初始化      =========================");
    }
}
