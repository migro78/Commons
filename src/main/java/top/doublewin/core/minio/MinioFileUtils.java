package top.doublewin.core.minio;

import io.minio.errors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;
import top.doublewin.core.exception.BusinessException;
import top.doublewin.core.util.DataUtil;
import top.doublewin.core.util.InstanceUtil;
import top.doublewin.model.FileInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class MinioFileUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * 设置bucket可访问
     *
     * @param bucketName
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidObjectPrefixException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws InternalException
     */
    public static void setBucketAccessible(String bucketName) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidObjectPrefixException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, InternalException {
        MinioClientUtils instance = MinioClientUtils.getInstance();
        instance.setBucketPublic(bucketName);
    }

    /**
     * 获取文件类型
     *
     * @param data
     * @return
     */
    public static FileTypeEnum getFileTypeEnum(MultipartFile data) {
        String fileName = data.getOriginalFilename();
        return getFileTypeEnum(fileName);
    }

    public static FileTypeEnum getFileTypeEnum(String fileName) {
        String type = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        FileTypeEnum fileTypeEnum;
        switch (type) {
            case "jpg":
            case "jpeg":
            case "gif":
            case "png":
                fileTypeEnum = FileTypeEnum.图片;
                break;
            case "xlsx":
            case "xls":
                fileTypeEnum = FileTypeEnum.EXCEL;
                break;
            case "doc":
            case "docx":
                fileTypeEnum = FileTypeEnum.WORD;
                break;
            default:
                fileTypeEnum = FileTypeEnum.其他文件;
        }
        return fileTypeEnum;
    }

    public static FileInfo upload(MultipartFile data) {
        FileTypeEnum fileTypeEnum = getFileTypeEnum(data);
        FileInfo fileInfo = upload(data, fileTypeEnum);
        return fileInfo;
    }

    public static String upload(String fileUrl){
        FileTypeEnum fileTypeEnum = getFileTypeEnum(fileUrl);
        MinioClientUtils instance = MinioClientUtils.getInstance();
        String derectorName = fileTypeEnum.getValue();
        instance.createBucketPublic(null);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        String filePath = derectorName + "/" + fileName;
        InputStream inputStream = null;
        File file = null;
        try {
            file = new File(fileUrl);
            inputStream = new BufferedInputStream(new FileInputStream(file));
            instance.uploadStream(filePath, inputStream, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            if(DataUtil.isNotEmpty(file)){
                inputStream.close();
                file.delete();
            }
        } catch (Exception e) {
            logger.error(e,e);
            throw new BusinessException("上传文件不存在！");
        } finally {
            if(DataUtil.isNotEmpty(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e,e);
                }
            }
        }

        return filePath;
    }

    /**
     * 上传文件
     *
     * @param data
     * @param fileTypeEnum
     */
    public static FileInfo upload(MultipartFile data, FileTypeEnum fileTypeEnum) {
        FileInfo fileInfo = new FileInfo();
        try {
            String fileName = data.getOriginalFilename();
            String type = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            fileInfo.setOrgName(fileName);
            fileInfo.setFileType(type);
            fileInfo.setFileSize(data.getSize());
            String contentType = data.getContentType();
            String uuid = UUID.randomUUID().toString();
            InputStream inputStream = data.getInputStream();
            MinioClientUtils instance = MinioClientUtils.getInstance();
            String derectorName = fileTypeEnum.getValue();
            instance.createBucketPublic(null);
            fileName = uuid + "." + type;
            String filePath = derectorName + "/" + fileName;
            //返回文件名
            String name = instance.uploadStream(filePath, inputStream, contentType);
            fileInfo.setFileName(filePath);
            return fileInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 上传网络图片
     *
     * @param imageUrl 图片地址
     * @return
     */
    public static Map<String,Object> uploadStreamByUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
            urlconn.connect();
            HttpURLConnection httpconn = (HttpURLConnection) urlconn;
            int responseCode = httpconn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlconn.getInputStream();
                //复制 inputStream
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                byteArrayOutputStream.flush();

                InputStream copyStreamOne = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                InputStream copyStreamTwo = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                String imageType = getImageType(copyStreamOne);
                MinioClientUtils instance = MinioClientUtils.getInstance();
                String fileName = UUID.randomUUID().toString() + "." + imageType;
                String filePath = FileTypeEnum.图片.getValue() + "/" + fileName;
                String name = instance.uploadStream(filePath, copyStreamTwo, null);
                Map<String,Object> fileMap = InstanceUtil.newHashMap();
                fileMap.put("name",name);
                fileMap.put("bytes",copyStreamOne.available());
                fileMap.put("suffix",imageType);
                return fileMap;
            } else {
                throw new RuntimeException("url连接失败!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断inputstream的图片类型
     *
     * @param stream
     * @return
     */
    public static String getImageType(InputStream stream) {

        byte[] bytes = new byte[10];
        char[] chars = new char[10];
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();

            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());


            inputStream.read(bytes, 0, 10);
            for (int i = 0; i < 10; i++) {
                chars[i] = (char) bytes[i];
            }
            String type;
            if (chars[0] == (byte) 'G' && chars[1] == (byte) 'I' && chars[2] == (byte) 'F') {
                type = "gif";
            } else if (chars[1] == (byte) 'P' && chars[2] == (byte) 'N' && chars[3] == (byte) 'G') {
                type = "png";
            } else if (chars[6] == (byte) 'J' && chars[7] == (byte) 'F' && chars[8] == (byte) 'I' && chars[9] == (byte) 'F') {
                type = "jpg";
            } else {
                type = "bmp";
            }
            return type;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param response
     * @param objectName
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidArgumentException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InternalException
     */
    public static Object downLoad(HttpServletResponse response, String objectName, String bucketName) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        MinioClientUtils instance = MinioClientUtils.getInstance();
        InputStream stream = instance.getStreamByName(bucketName, objectName);
        downloadFile(response, stream, objectName);
        return null;
    }


    public static void downloadFile(HttpServletResponse response, InputStream stream, String filename) {

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("utf-8"), "ISO-8859-1"));
            bis = new BufferedInputStream(stream);
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
