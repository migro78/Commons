package top.doublewin.core.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.doublewin.core.util.PdfUtil;

import javax.annotation.PostConstruct;

/**
 * <p>
 * PDF打印配置类
 * </p>
 *
 * @author migro
 * @since 2021/4/27 11:29
 */
public class PdfPrintConfig {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${pdf.template.path}", autoRefreshed = true)
    private String templatePath;
    @NacosValue(value = "${pdf.output.temp.path}", autoRefreshed = true)
    private String outputTemporaryPath;
    @NacosValue(value = "${pdf.output.path}", autoRefreshed = true)
    private String outputPath;

    @PostConstruct
    public void init() {
        PdfUtil.setTemplatePath(templatePath);
        PdfUtil.setOutputTemporaryPath(outputTemporaryPath);
        PdfUtil.setOutputPath(outputPath);
        logger.info("=======================     完成PDF打印配置初始化      =========================");
    }


}
