package top.doublewin.core.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.doublewin.core.exception.BusinessException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 生成PDF文档辅助类
 * </p>
 *
 * @author migro
 * @since 2021/4/27 10:55
 */
public class PdfUtil {
    protected static Logger logger = LogManager.getLogger();
    /**
     * 常量枚举
     */
    public static final String STRING_MAP = "string_map";
    public static final String IMAGE_MAP = "image_map";

    /**
     * 默认字体大小
     */
    private static final Float DEFAULT_FONT_SIZE = 10f;

    /**
     * PDF模板打印
     *
     * @param templatePath 模板文件路径
     * @param params       参数集合
     * @return String PDF文件url
     */
    public static String templatePrint(String templatePath, Map<String, Object> params) {
        return templatePrint(templatePath, params, null, DEFAULT_FONT_SIZE, UUID.randomUUID().toString());
    }

    public static String templatePrint(String templatePath, Map<String, Object> params, String fileName) {
        return templatePrint(templatePath, params, null, DEFAULT_FONT_SIZE, fileName);
    }


    public static String templatePrint(String templatePath, Map<String, Object> params, Object fontSize) {
        return templatePrint(templatePath, params, null, fontSize, UUID.randomUUID().toString());
    }

    public static String templatePrint(String templatePath, Map<String, Object> params, String fileName, String fontPath) throws Exception {
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        return templatePrint(templatePath, params, bf, fileName);
    }

    public static String templatePrint(String templatePath, Map<String, Object> params, BaseFont bf, String fileName) {
        return templatePrint(templatePath, params, bf, DEFAULT_FONT_SIZE, fileName);
    }

    public static String templatePrint(String templatePath, Map<String, Object> params, BaseFont bf, Object fontSize, String fileName) {
        PdfReader reader = null;
        FileOutputStream out = null;
        ByteArrayOutputStream bos = null;
        PdfStamper stamper = null;
        Document doc = null;

        if (DataUtil.isAnyEmpty(templatePath, params, fontSize)) {
            throw new BusinessException("PDF打印，参数缺失!");
        }
        String outputFileUrl = outputTemporaryPath + fileName + ".pdf";

        try {
            //读取pdf模板
            reader = new PdfReader(templatePath);
            //新文件输出流
            out = new FileOutputStream(outputFileUrl);
            //字节输出流
            bos = new ByteArrayOutputStream();
            //pdf模板中的内容到字节输出流
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();

            //文字类的内容处理
            Map<String, Object> stringMap = (Map<String, Object>) params.get(STRING_MAP);
            if (DataUtil.isNotEmpty(bf)) {
                //指定字体
                form.addSubstitutionFont(bf);
            }
            if (DataUtil.isNotEmpty(stringMap)) {
                for (String key : stringMap.keySet()) {
                    Object value = stringMap.get(key);
                    form.setFieldProperty(key, "textsize", fontSize, null);
                    form.setField(key, objToString(value));
                }
            }

            // 图片类的内容处理
            Map<String, Object> imageMap = (Map<String, Object>) params.get(IMAGE_MAP);
            for (String key : imageMap.keySet()) {
                String imagePath = (String) imageMap.get(key);
                int pageNo = form.getFieldPositions(key).get(0).page;
                Rectangle signRect = form.getFieldPositions(key).get(0).position;
                float x = signRect.getLeft();
                float y = signRect.getBottom();
                //根据路径读取图片
                Image image = Image.getInstance(imagePath);
                //获取图片页面
                PdfContentByte under = stamper.getOverContent(pageNo);
                //图片大小自适应
                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                //添加图片
                image.setAbsolutePosition(x, y);
                under.addImage(image);
            }
            // 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
            stamper.setFormFlattening(true);
            stamper.close();
            doc = new Document(PageSize.A4);
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
        } catch (Exception e) {
            logger.error(e, e);
            throw new BusinessException("PDF文件打印失败:" + e.getMessage());
        } finally {
            try {
                if (DataUtil.isNotEmpty(doc)) {
                    doc.close();
                }
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return outputFileUrl;
    }

    private static String objToString(Object value) {
        if (DataUtil.isNotEmpty(value)) {
            if (value instanceof String || value instanceof Integer || value instanceof Double) {
                return value.toString();
            }
            if (value instanceof Date) {
                return DateUtil.format(value);
            }
        }
        return null;
    }


    /**
     * 全局配置参数
     */

    /**
     * PDF模板源文件目录
     */
    private static String templatePath;
    /**
     * PDF文件输出目录
     */
    private static String outputPath;
    /**
     * PDF文件临时输出目录
     */
    private static String outputTemporaryPath;

    public static void setTemplatePath(String templatePath) {
        PdfUtil.templatePath = templatePath;
    }

    public static void setOutputTemporaryPath(String outputTemporaryPath) {
        PdfUtil.outputTemporaryPath = outputTemporaryPath;
    }

    public static void setOutputPath(String outputPath) {
        PdfUtil.outputPath = outputPath;
    }

    public static String getTemplatePath() {
        return templatePath;
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static String getOutputTemporaryPath() {
        return outputTemporaryPath;
    }

    public static void main(String[] args) {
        String path = "F:/studyworkspaces/pdftest/files/";
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> stringMap = new HashMap();
        stringMap.put("patName", "西门吹雪");
        stringMap.put("patSex", "男");
        stringMap.put("patAge", "48");
        stringMap.put("patTel", "13888888888");
        stringMap.put("patIdNo", "500000199901010011");
        stringMap.put("patAddr", "红锦大道125号嘉州协信中心A栋2209");
        stringMap.put("outOrgName", "成都市双楠医院");
        stringMap.put("diag", "开放性颅脑损伤，脑疝，颅内高压");
        stringMap.put("createTime", "2021-04-26");
        stringMap.put("refType", "住院");
        stringMap.put("refType2", "住院");
        stringMap.put("inDeptName", "神经外科");
        stringMap.put("remark", "联系电话66668888");

        Map<String, Object> imageMap = new HashMap();
        imageMap.put("orgSign", path + "syy_ref_sign.png");
        imageMap.put("logo", path + "syy_logo.png");
        imageMap.put("docSign", path + "doc_sign.png");

        params.put(PdfUtil.STRING_MAP, stringMap);
        params.put(PdfUtil.IMAGE_MAP, imageMap);

        PdfUtil.setOutputTemporaryPath("e:/temp/");
        String fileName = PdfUtil.templatePrint(path + "syy_ref_print1.pdf", params);
        System.out.println(fileName);
        String url = UploadUtil.uploadMino(fileName);
        System.out.println(url);
    }
}
