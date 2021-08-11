package top.doublewin.core.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

/**
 * 图片处理辅助类
 *
 * @author ShenHuaJie
 * @since 2012-03-21
 */
public final class ImageUtil {
    protected static Logger logger = LogManager.getLogger();

    private ImageUtil() {
    }

    /**
     * 生成二维码图片的base64编码串
     *
     * @param
     * @return
     */
    public static final String genQRCodeBase64(String text) throws Exception {
        return genQRCodeBase64(text, 400, 400, "png");
    }

    /**
     * 生成带Logo的二维码图片的base64编码串
     *
     * @param
     * @return
     */
    public static final String genLogoQRCodeBase64(String text, String logoUrl) throws Exception {
        return genLogoQRCodeBase64(text, 400, 400, "png", logoUrl);
    }

    /**
     * 生成二维码图片的base64编码串
     *
     * @param
     * @return
     */
    public static final String genQRCodeBase64(String text, int width, int height, String format) throws Exception {
        ByteArrayOutputStream outputStream = genQRCodeImage(text, width, height, format);
        return imageToBase64(outputStream, format);
    }

    public static final String genLogoQRCodeBase64(String text, int width, int height, String format, String logoUrl) throws Exception {
        ByteArrayOutputStream outputStream = genLogoQRCodeImage(text, width, height, format, logoUrl);
        return imageToBase64(outputStream, format);
    }

    public static final String imageToBase64(ByteArrayOutputStream outputStream, String format) {
        String head = "data:image/" + format + ";base64,";
        Base64.Encoder encoder = Base64.getEncoder();
        String code = encoder.encodeToString(outputStream.toByteArray());
        return head + code;
    }

    public static final String imageToBase64(ByteArrayOutputStream outputStream){
        Base64.Encoder encoder = Base64.getEncoder();
        String code = encoder.encodeToString(outputStream.toByteArray());
        return code;
    }

    public static final String imageToBase64(String url){
        InputStream ins = null;
        ByteArrayOutputStream out = null;
        String ret = null;
        try {
            // 获得网络图片输入流
            ins = readImageFromUrl(url);
            if(DataUtil.isEmpty(ins)){
                logger.error("网络图片读取失败！");
                return null;
            }
            // 转成字节输出流
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = ins.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            // 输出流转base64
            ret = imageToBase64(out);
        }catch (Exception e){
            logger.error(e,e);
        }finally {
            if(DataUtil.isNotEmpty(ins)){
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(DataUtil.isNotEmpty(out)){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    private static final ByteArrayOutputStream genQRCodeImage(String text, int width, int height, String format) throws Exception {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
        return outputStream;
    }

    public static final ByteArrayOutputStream genLogoQRCodeImage(String text, int width, int height, String format, String logoUrl) throws Exception {
        // 1、读取二维码图片，并构建绘图对象
        ByteArrayOutputStream outputStream = genQRCodeImage(text, width, height, format);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        BufferedImage image = ImageIO.read(inputStream);
        BufferedImage graphImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graph = graphImg.createGraphics();
        // 2、读取logo图片
        BufferedImage logo = ImageIO.read(readImageFromUrl(logoUrl));

        int widthLogo = image.getWidth() / 6;
        int heightLogo = image.getHeight() / 6;

        // 3、计算图片放置的位置
        int x = (image.getWidth() - widthLogo) / 2;
        int y = (image.getHeight() - heightLogo) / 2;

        // 4、绘制图片
        graph.drawImage(image, 0, 0, null);
        graph.drawImage(logo, x, y, widthLogo, heightLogo, null);
        graph.drawRoundRect(x, y, widthLogo, heightLogo, 10, 10);
        graph.setStroke(new BasicStroke(2));
        graph.setColor(Color.WHITE);
        graph.drawRect(x, y, widthLogo, heightLogo);
        graph.dispose();

        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        ImageIO.write(graphImg, format, ret);

        return ret;
    }

    public static final InputStream readImageFromUrl(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            //byte[] buffer = new byte[conn.getContentLength()];
            //int r = in.read(buffer);
            return in;
        } else {
            return null;
        }
    }

    /**
     * * 转换图片大小，不变形
     *
     * @param img    图片文件
     * @param width  图片宽
     * @param height 图片高
     */
    public static final void changeImge(File img, int width, int height) {
        try {
            Thumbnails.of(img).size(width, height).keepAspectRatio(false).toFile(img);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("图片转换出错！", e);
        }
    }

    /**
     * 根据比例缩放图片
     *
     * @param orgImg     源图片路径
     * @param scale      比例
     * @param targetFile 缩放后的图片存放路径
     * @throws IOException
     */
    public static final void scale(BufferedImage orgImg, double scale, String targetFile) throws IOException {
        Thumbnails.of(orgImg).scale(scale).toFile(targetFile);
    }

    public static final void scale(String orgImgFile, double scale, String targetFile) throws IOException {
        Thumbnails.of(orgImgFile).scale(scale).toFile(targetFile);
    }

    /**
     * 图片格式转换
     *
     * @param orgImgFile
     * @param width
     * @param height
     * @param suffixName
     * @param targetFile
     * @throws IOException
     */
    public static final void format(String orgImgFile, int width, int height, String suffixName, String targetFile)
            throws IOException {
        Thumbnails.of(orgImgFile).size(width, height).outputFormat(suffixName).toFile(targetFile);
    }

    /**
     * 根据宽度同比缩放
     *
     * @param orgImg      源图片
     * @param targetWidth 缩放后的宽度
     * @param targetFile  缩放后的图片存放路径
     * @throws IOException
     */
    public static final double scaleWidth(BufferedImage orgImg, int targetWidth, String targetFile) throws IOException {
        int orgWidth = orgImg.getWidth();
        // 计算宽度的缩放比例
        double scale = targetWidth * 1.00 / orgWidth;
        // 裁剪
        scale(orgImg, scale, targetFile);

        return scale;
    }

    public static final void scaleWidth(String orgImgFile, int targetWidth, String targetFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(orgImgFile));
        scaleWidth(bufferedImage, targetWidth, targetFile);
    }

    /**
     * 根据高度同比缩放
     *
     * @param orgImg       源图片
     * @param targetHeight 缩放后的高度
     * @param targetFile   缩放后的图片存放地址
     * @throws IOException
     */
    public static final double scaleHeight(BufferedImage orgImg, int targetHeight, String targetFile) throws IOException {
        int orgHeight = orgImg.getHeight();
        double scale = targetHeight * 1.00 / orgHeight;
        scale(orgImg, scale, targetFile);
        return scale;
    }

    public static final void scaleHeight(String orgImgFile, int targetHeight, String targetFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(orgImgFile));
        // int height = bufferedImage.getHeight();
        scaleHeight(bufferedImage, targetHeight, targetFile);
    }

    // 原始比例缩放
    public static final void scaleWidth(File file, Integer width) throws IOException {
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        String postFix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        // 缩放
        BufferedImage bufferedImg = ImageIO.read(file);
        String targetFile = filePath + "_s" + postFix;
        scaleWidth(bufferedImg, width, targetFile);
        String targetFile2 = filePath + "@" + width;
        new File(targetFile).renameTo(new File(targetFile2));
    }

    private static Color[] colors = {
            new Color(242, 128, 51),
            new Color(162, 209, 72),
            new Color(254, 166, 142),
            new Color(85, 157, 236),
            new Color(158, 216, 204),
            new Color(248, 217, 104),
            new Color(116, 97, 194)
    };

    //绘制首文字文字图标
    public static final Image firstStrAvator(String text) {
        //测试方法
        //String rootPath = "C://image_test";
        //  BufferedImage img = (BufferedImage)ImageUtil.firstStrAvator("WEI");
        // ImageIO.write(img, "png", Paths.get(rootPath, "b" + 4000 + ".png").toFile());
        String firstStr = text.substring(0, 1);
        String fontName = "黑体";
        if (firstStr.matches("^[a-zA-Z]*")) {
            fontName = "Verdana";
        }
        Integer rectSize = 200;
        Integer fontSize = 130;
        try {
            int index = (int) (Math.random() * colors.length);
            Color backColor = colors[index];
            Image img = centerStringImage(firstStr, fontName, rectSize, fontSize, backColor);
            // ImageIO.write(img, "png", OutputStream);
            return img;
        } catch (Exception e) {
            logger.error("firstStrAvator", e);
        }
        return null;
    }

    public static final Image centerStringImage(String text, String fontName, Integer rectSize, Integer fontSize, Color backColor) {
        //正方形
        BufferedImage image = new BufferedImage(rectSize, rectSize,
                BufferedImage.TYPE_INT_BGR);//创建图片画布
        Graphics2D g2d = image.createGraphics();
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        centerString(g2d, new Rectangle(rectSize, rectSize), text, font, backColor);
        return image;
    }

    private static final void centerString(Graphics2D g, Rectangle r, String s,
                                           Font font, Color backColor) {
        FontRenderContext frc =
                new FontRenderContext(null, true, true);
        Rectangle2D r2D = font.getStringBounds(s, frc);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());
        int a = (r.width / 2) - (rWidth / 2) - rX;
        int b = (r.height / 2) - (rHeight / 2) - rY;
        //防止锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(backColor);
        g.fillRect(0, 0, r.width, r.height);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(s, r.x + a, r.y + b);
    }


}
