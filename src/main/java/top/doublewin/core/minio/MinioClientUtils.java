package top.doublewin.core.minio;


import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MinioClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioClientUtils.class);


    private static MinioClientUtils minioClientUtils;

    private MinioClient minioClient;

    private static int RETRY_NUM = 3;

    private static final String bucketPublicPolicy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Resource\":[\"arn:aws:s3:::test\"],\"Sid\":\"\"},{\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Resource\":[\"arn:aws:s3:::test/*\"],\"Sid\":\"\"}]}";

    private static String url;
    private static String username;
    private static String password;
    private static String bucketName;
    private static String picPrefix;


    public static void setUrl(String url) {
        MinioClientUtils.url = url;
    }

    public static void setUsername(String username) {
        MinioClientUtils.username = username;
    }

    public static void setPassword(String password) {
        MinioClientUtils.password = password;
    }

    public static void setBucketName(String bucketName) {
        MinioClientUtils.bucketName = bucketName;
    }

    public static String getPicPrefix() {
        return picPrefix;
    }

    public static void setPicPrefix(String picPrefix) {
        MinioClientUtils.picPrefix = picPrefix;
    }



    public static MinioClientUtils getInstance() {

        if (null != minioClientUtils) {
            return minioClientUtils;
        }
        synchronized (MinioClientUtils.class) {
            if (null == minioClientUtils) {
                minioClientUtils = new MinioClientUtils(url, username, password, bucketName);
            }
        }
        return minioClientUtils;
    }


    private MinioClientUtils(String url, String username, String password, String bucketName) {
        init(url, username, password, bucketName);
    }

    private void init(String url, String username, String password, String bucketName) {
        try {
            if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                minioClient = new MinioClient(url, username, password, false);
            }
        } catch (Exception e) {
            LOGGER.error("restClient.close occur error", e);
        }
    }

    public String uploadJpegFile(String bucketName, String minioPath, String jpgFilePath) {
        return uploadFile(bucketName, minioPath, jpgFilePath, MediaType.IMAGE_JPEG_VALUE);
    }

    public String uploadJpegStream(String bucketName, String minioPath, InputStream inputStream) {
        return uploadStream(minioPath, inputStream, MediaType.IMAGE_JPEG_VALUE);
    }

    public String uploadStream(String minioFilePath, InputStream inputStream, String mediaType) {
        LOGGER.info("uploadStream for bucketName={} minioFilePath={} inputStream.getclass={}, mediaType={}", bucketName,
                minioFilePath, inputStream.getClass(), mediaType);
        if (StringUtils.isBlank(mediaType)) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        try {
            putObjectWithRetry(bucketName, minioFilePath, inputStream, mediaType);
            return cleanUrlByRemoveIp(minioClient.getObjectUrl(bucketName, minioFilePath));
        } catch (Exception e) {
            LOGGER.error("uploadStream occur error:", e);
            throw new RuntimeException(e);
        }
    }

    public String cleanUrlByRemoveIp(String url) {
        if (url != null && !"".equals(url.trim())) {
            return url.substring(url.lastIndexOf("/") + 1).toLowerCase();
        }
        return null;
    }

    public String uploadFile(String bucketName, String minioFilePath, String localFile, String mediaType) {
        LOGGER.info("uploadFile for bucketName={} minioFilePath={} localFile={}, mediaType={}", bucketName,
                minioFilePath, localFile, mediaType);
        if (StringUtils.isBlank(mediaType)) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        try {
            putObjectWithRetry(bucketName, minioFilePath, localFile, mediaType);
            return cleanUrlByRemoveIp(minioClient.getObjectUrl(bucketName, minioFilePath));
        } catch (Exception e) {
            LOGGER.error("uploadFile occur error:", e);
            throw new RuntimeException(e);
        }
    }

    public List<MinioEntity> listFilesSwap(String bucketName, String prefix, boolean recursive) {
        LOGGER.info("list files for bucketName={} prefix={} recursive={}", bucketName, prefix, recursive);
        return swapResultToEntityList(minioClient.listObjects(bucketName, prefix, recursive));
    }

    public Iterable<Result<Item>> listFiles(String bucketName, String prefix, boolean recursive) {
        LOGGER.info("list files for bucketName={} prefix={} recursive={}", bucketName, prefix, recursive);
        return minioClient.listObjects(bucketName, prefix, recursive);
    }


    public List<MinioEntity> listFilesByBucketNameSwap(String bucketName) {
        LOGGER.info("listFilesByBucketName for bucketName={}", bucketName);
        return swapResultToEntityList(minioClient.listObjects(bucketName, null, true));
    }

    public Iterable<Result<Item>> listFilesByBucketName(String bucketName) {
        LOGGER.info("listFilesByBucketName for bucketName={}", bucketName);
        return minioClient.listObjects(bucketName, null, true);
    }

    public Iterable<Result<Item>> listFilesByBucketAndPrefix(String bucketName, String prefix) {
        LOGGER.info("listFilesByBucketAndPrefix for bucketName={} and prefix={}", bucketName, prefix);
        return minioClient.listObjects(bucketName, prefix, true);
    }

    public List<MinioEntity> listFilesByBucketAndPrefixSwap(String bucketName, String prefix) {
        LOGGER.info("listFilesByBucketAndPrefix for bucketName={} and prefix={}", bucketName, prefix);
        return swapResultToEntityList(minioClient.listObjects(bucketName, prefix, true));
    }

    private MinioEntity swapResultToEntity(Result<Item> result) {
        MinioEntity minioEntity = new MinioEntity();
        try {
            if (result.get() != null) {
                Item item = result.get();
                minioEntity.setObjectName(cleanUrlByRemoveIp(item.objectName()));
                minioEntity.setDir(item.isDir());
                minioEntity.setEtag(item.etag());
                minioEntity.setLastModified(item.lastModified());
                minioEntity.setSize(item.size());
                minioEntity.setStorageClass(item.storageClass());
            }
        } catch (Exception e) {
            LOGGER.error("UrlUtils error, e={}", e.getMessage());
        }
        return minioEntity;
    }

    private List<MinioEntity> swapResultToEntityList(Iterable<Result<Item>> results) {
        List<MinioEntity> minioEntities = new ArrayList<>();
        for (Result<Item> result : results) {
            minioEntities.add(swapResultToEntity(result));
        }
        return minioEntities;
    }

    public void putObjectWithRetry(String bucketName, String objectName, InputStream stream, String contentType) {
        int current = 0;
        boolean isSuccess = false;
        while (!isSuccess && current < RETRY_NUM) {
            try {
                minioClient.putObject(bucketName, objectName, stream, contentType);
                isSuccess = true;
            } catch (Exception e) {
                LOGGER.warn("[minio] putObject stream, ErrorResponseException occur for time =" + current, e);
                current++;
            }
        }
        if (current == RETRY_NUM) {
            LOGGER.error("[minio] putObject, backetName={}, objectName={}, failed finally!");
        }
    }

    public void putObjectWithRetry(String bucketName, String objectName, String fileName, String contentType) throws InvalidBucketNameException, InsufficientDataException, IOException, XmlPullParserException, ErrorResponseException, InternalException, InsufficientDataException {
        int current = 0;
        boolean isSuccess = false;
        while (!isSuccess && current < RETRY_NUM) {
            try {
                minioClient.putObject(bucketName, objectName, fileName, contentType);
                isSuccess = true;
            } catch (Exception e) {
                current++;
                LOGGER.debug("[minio] putObject file, ErrorResponseException occur!");
            }
        }
        if (current == RETRY_NUM) {
            LOGGER.error("[minio] putObject, backetName={}, objectName={}, failed finally!");
        }
    }

    /**
     * 获取所有根目录
     */
    public static List<String> getBucketsList() {
        try {
            List<Bucket> buckets = MinioClientUtils.getInstance().minioClient.listBuckets();
            if (buckets != null && buckets.size() > 0) {
                return buckets.stream().map(Bucket::name).collect(Collectors.toList());
            }
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | InvalidKeyException | IOException | XmlPullParserException | NoResponseException | InternalException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建公共的bucket
     *
     * @param region
     * @return
     */
    public boolean createBucketPublic(String region) {
        boolean isCreated;
        try {
            if (minioClient.bucketExists(bucketName)) {
                isCreated = false;
                return isCreated;
            }
            if (region == null) {
                minioClient.makeBucket(bucketName);
            } else {
                minioClient.makeBucket(bucketName, region);
            }
            //minioClient.setBucketPolicy(bucketName, bucketPublicPolicy);
            isCreated = true;
        } catch (Exception e) {
            isCreated = false;
            LOGGER.error("createBucketPublic", e);
            e.printStackTrace();
        }
        return isCreated;
    }

    public InputStream getStreamByName(String bucketName, String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        bucketName = bucketName == null ? bucketName : bucketName;
        return minioClient.getObject(bucketName, objectName);
    }

    /**
     * 删除文件
     *
     * @param buckName
     * @param objectName
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws XmlPullParserException
     * @throws ErrorResponseException
     * @throws InvalidArgumentException
     */
    @SuppressWarnings("unchecked")
    public static void deleteFileByName(String buckName, Object objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidArgumentException {
        MinioClientUtils instance = MinioClientUtils.getInstance();
        if (objectName == null) {
            //删除buck
            instance.minioClient.removeBucket(buckName);
        } else {
            if (objectName instanceof String) {
                //删除单个文件
                instance.minioClient.removeObject(buckName, (String) objectName);
            } else {
                //删除多个
                Iterable<Result<DeleteError>> results = instance.minioClient.removeObject(buckName, (Iterable<String>) objectName);
                /*Iterator<Result<DeleteError>> iterator = results.iterator();
                Result<DeleteError> next = iterator.next();
                DeleteError deleteError = next.get();*/
            }
        }
    }

    /**
     * 获取单个bucket
     *
     * @param bucketName
     * @return
     */
    public static List<String> getBucketsByName(String bucketName) {
        try {
            List<Bucket> buckets = MinioClientUtils.getInstance().minioClient.listBuckets();
            if (buckets != null && buckets.size() > 0) {
                return buckets.stream().map(Bucket::name).collect(Collectors.toList());
            }
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | InvalidKeyException | IOException | XmlPullParserException | NoResponseException | InternalException | ErrorResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置Bucket访问权限
     *
     * @param bucketName
     */
    public void setBucketPublic(String bucketName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidObjectPrefixException, NoResponseException, InvalidBucketNameException, XmlPullParserException, InternalException, ErrorResponseException {
        //公开访问
        String builder = "{\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"s3:GetBucketLocation\",\n" +
                "                \"s3:ListBucket\"\n" +
                "            ],\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": \"*\",\n" +
                "            \"Resource\": \"arn:aws:s3:::" + bucketName + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Action\": \"s3:GetObject\",\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": \"*\",\n" +
                "            \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"Version\": \"2012-10-17\"\n" +
                "}\n";
        minioClient.setBucketPolicy(bucketName, builder);
    }

    /**
     * @param bucketName
     * @param policy
     * @return
     */
    public boolean createBucketPolicy(String bucketName, String policy) {
        try {
//            if (minioClient.bucketExists(bucketName)) {
//                isCreated = false;
//            }
            minioClient.setBucketPolicy(bucketName, policy);
            //minioClient.setBucketPolicy(bucketName, bucketPublicPolicy);

        } catch (Exception e) {

            LOGGER.error("createBucketPublic", e);
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            builder.append("    \"Statement\": [\n");
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:GetBucketLocation\",\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Allow\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::custom-bucket\"\n");
            builder.append("        },\n");
            builder.append("        {\n");
            builder.append("            \"Action\": \"s3:GetObject\",\n");
            builder.append("            \"Effect\": \"Allow\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::custom-bucket/*\"\n");
            builder.append("        }\n");
            builder.append("    ],\n");
            builder.append("    \"Version\": \"2012-10-17\"\n");
            builder.append("}\n");
            //MinioClientUtils.getInstance().minioClient.setBucketPolicy("custom-bucket",builder.toString());
            //MinioClientUtils.getInstance().createBucketPublic("custom-bucket-two","us-east-1");
            //MinioClientUtils.getInstance().createBucketPolicy("central-platform",bucketPublicPolicy);
            Iterable<Result<Item>> results = MinioClientUtils.getInstance().listFilesByBucketName("project-name");
            Iterable<Result<Item>> excel = MinioClientUtils.getInstance().minioClient.listObjects("project-name", "excel");
            Iterator<Result<Item>> iterator = results.iterator();
            while (iterator.hasNext()) {
                Result<Item> result = iterator.next();
                Item item = result.get();
                System.out.println();
                System.out.println(item.objectName() + " " + item.isDir());
            }

            Iterator<Result<Item>> resultIterator = excel.iterator();
            while (resultIterator.hasNext()) {
                Result<Item> result = resultIterator.next();
                Item item = result.get();
                System.out.println();
                System.out.println(item.objectName() + " " + item.isDir());
            }
        } catch (Exception e) {

        }
    }
}