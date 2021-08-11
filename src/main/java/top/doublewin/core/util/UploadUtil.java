package top.doublewin.core.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import top.doublewin.core.interceptor.Constants;
import top.doublewin.core.minio.MinioFileUtils;
import top.doublewin.model.FileInfo;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 上传辅助类 与Spring.multipartResolver冲突
 *
 * @author ShenHuaJie
 */
public final class UploadUtil {
    private UploadUtil() {
    }

    private static final Logger logger = LogManager.getLogger();

    /**
     * 上传文件缓存大小限制
     */
    private static int fileSizeThreshold = 1024 * 1024 * 10;
    /**
     * 上传文件临时目录
     */
    private static final String uploadFileDir = "/WEB-INF/upload";



    /**
     * 获取所有文本域
     */
    public static final List<?> getFileItemList(HttpServletRequest request, File saveDir) throws FileUploadException {
        if (!saveDir.isDirectory()) {
            saveDir.mkdir();
        }
        List<?> fileItems = null;
        RequestContext requestContext = new ServletRequestContext(request);
        if (FileUploadBase.isMultipartContent(requestContext)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(saveDir);
            factory.setSizeThreshold(fileSizeThreshold);
            ServletFileUpload upload = new ServletFileUpload(factory);
            fileItems = upload.parseRequest(request);
        }
        return fileItems;
    }

    /**
     * 获取文本域
     */
    public static final FileItem[] getFileItem(HttpServletRequest request, File saveDir, String... fieldName)
            throws FileUploadException {
        if (fieldName == null || saveDir == null) {
            return null;
        }
        List<?> fileItemList = getFileItemList(request, saveDir);
        FileItem fileItem = null;
        FileItem[] fileItems = new FileItem[fieldName.length];
        for (int i = 0; i < fieldName.length; i++) {
            for (Object name : fileItemList) {
                fileItem = (FileItem) name;
                // 根据名字获得文本域
                if (fieldName[i] != null && fieldName[i].equals(fileItem.getFieldName())) {
                    fileItems[i] = fileItem;
                    break;
                }
            }
        }
        return fileItems;
    }

    /**
     * 上传文件处理(支持批量)
     */
    public static List<String> uploadFile(HttpServletRequest request) {
        List<String> fileNames = InstanceUtil.newArrayList();
        List<FileInfo> files = uploadFiles(request);
        for (FileInfo fileInfo : files) {
            fileNames.add(fileInfo.getFileName());
        }
        return fileNames;
    }

    /**
     * 上传文件处理(支持批量)
     */
    public static List<FileInfo> uploadFiles(HttpServletRequest request) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        List<FileInfo> fileNames = InstanceUtil.newArrayList();
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            String pathDir = getUploadDir(request);
            File dirFile = new File(pathDir);
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext(); ) {
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if (multipartFile != null) {
                    FileInfo fileInfo = new FileInfo();
                    String name = multipartFile.getOriginalFilename();
                    fileInfo.setOrgName(name);
                    if (name.indexOf(".") == -1 && "blob".equals(name)) {
                        name = name + ".png";
                    }
                    String uuid = UUID.randomUUID().toString();
                    String postFix = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                    fileInfo.setFileType(postFix);
                    String fileName = uuid + "." + postFix;
                    String filePath = pathDir + File.separator + fileName;
                    File file = new File(filePath);
                    file.setWritable(true, false);
                    fileInfo.setFileSize(multipartFile.getSize());
                    try {
                        multipartFile.transferTo(file);
                        fileInfo.setFileName(fileName);
                        fileNames.add(fileInfo);
                    } catch (Exception e) {
                        logger.error(name + "保存失败", e);
                    }
                }
            }
        }
        return fileNames;
    }


    /**
     * 上传文件处理(支持批量)
     */
    public static List<FileInfo> uploadMino(HttpServletRequest request) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        List<FileInfo> fileNames = InstanceUtil.newArrayList();
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext(); ) {
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if (multipartFile != null) {
                    FileInfo fileInfo = MinioFileUtils.upload(multipartFile);
                    fileNames.add(fileInfo);
                }
            }
        }
        return fileNames;
    }

    /**
     * 上传文件到Mino
     * @param
     * @return
     */
    public static String uploadMino(String fileUrl){
        String ret = null;
        if(DataUtil.isEmpty(fileUrl)){
            return null;
        }

        return MinioFileUtils.upload(fileUrl);
    }


    /**
     * 上传文件处理(支持批量)
     */
    public static List<String> uploadImage(HttpServletRequest request, boolean lessen) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        List<String> fileNames = InstanceUtil.newArrayList();
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            String pathDir = getUploadDir(request);
            File dirFile = new File(pathDir);
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext(); ) {
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if (multipartFile != null) {
                    String name = multipartFile.getOriginalFilename();
                    if (name.indexOf(".") == -1 && "blob".equals(name)) {
                        name = name + ".png";
                    }
                    String uuid = UUID.randomUUID().toString();
                    String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
                    String fileName = uuid + postFix;
                    String filePath = pathDir + File.separator + fileName;
                    File file = new File(filePath);
                    file.setWritable(true, false);
                    try {
                        multipartFile.transferTo(file);
                        fileNames.add(fileName);
                    } catch (Exception e) {
                        logger.error(name + "保存失败", e);
                    }
                    if (lessen) {
                        try { // 缩放
                            BufferedImage bufferedImg = ImageIO.read(file);
                            int orgwidth = bufferedImg.getWidth();// 原始宽度
                            ImageUtil.scaleWidth(file, 100);
                            if (orgwidth > 300) {
                                ImageUtil.scaleWidth(file, 300);
                            }
                            if (orgwidth > 500) {
                                ImageUtil.scaleWidth(file, 500);
                            }
                        } catch (Exception e) {
                            logger.error(name + "缩放失败", e);
                        }
                    }
                }
            }
        }
        return fileNames;
    }

    public static List<String> uploadImageData(HttpServletRequest request) {
        List<String> fileNames = InstanceUtil.newArrayList();
        Enumeration<String> params = request.getParameterNames();
        String pathDir = getUploadDir(request);
        File dir = new File(pathDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        while (params.hasMoreElements()) {
            String key = params.nextElement();
            String fileStr = request.getParameter(key);
            if (fileStr != null && !"".equals(fileStr)) {
                int index = fileStr.indexOf("base64");
                if (index > 0) {
                    try {
                        String fileName = UUID.randomUUID().toString();
                        String preStr = fileStr.substring(0, index + 7);
                        String prefix = preStr.substring(preStr.indexOf("/") + 1, preStr.indexOf(";")).toLowerCase();
                        fileStr = fileStr.substring(fileStr.indexOf(",") + 1);
                        byte[] bb = Base64.getDecoder().decode(fileStr);
                        for (int j = 0; j < bb.length; ++j) {
                            if (bb[j] < 0) {// 调整异常数据
                                bb[j] += 256;
                            }
                        }
                        String distPath = pathDir + fileName + "." + prefix;
                        OutputStream out = new FileOutputStream(distPath);
                        out.write(bb);
                        out.flush();
                        out.close();
                        fileNames.add(fileName + "." + prefix);
                    } catch (Exception e) {
                        logger.error("上传文件异常：", e);
                    }
                }
            }
        }
        return fileNames;
    }

    /**
     * 获取上传文件临时目录
     */
    public static String getUploadDir(HttpServletRequest request) {
        return request.getServletContext().getRealPath(uploadFileDir) + File.separator;
    }

//    /**
//     * 移动文件到fastDFS
//     */
//    public static FileModel remove2FDFS(String filePath) {
//        FileModel fastDFSFile = new FileModel(filePath);
//        FileManager.getInstance().upload(fastDFSFile);
//        new File(filePath).delete();
//        return fastDFSFile;
//    }
//
//    /**
//     * 移动文件到fastDFS
//     */
//    public static FileModel remove2FDFS(String groupName, String filePath) {
//        FileModel fastDFSFile = new FileModel(groupName, filePath);
//        FileManager.getInstance().upload(fastDFSFile);
//        new File(filePath).delete();
//        return fastDFSFile;
//    }
//
//    /**
//     * 移动文件到SFTP
//     */
//    public static String remove2Sftp(String host,int port ,String userName,String password,Integer timeout,
//                                     Integer aliveMax, String keyFile,String baseDir,String filePath, String namespace) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            throw new RuntimeException("文件" + filePath + "不存在");
//        }
//        SftpClient client = SftpClient.connect(host,port ,userName,password,timeout,aliveMax, keyFile);
//        String fileName = namespace + File.separator + file.getName();
//        client.put(filePath, baseDir + fileName);
//        client.disconnect();
//        //return PropertiesUtil.getString("sftp.nginx.path") + fileName;
//        return fileName;
//    }
//
//    /**
//     * 批量移动文件到SFTP
//     */
//    public static List<String> remove2Sftp(String host,int port ,String userName,String password,Integer timeout,
//                                           Integer aliveMax, String keyFile,String baseDir,List<String> filePathList,String namespace){
//        List<String> ret = InstanceUtil.newArrayList();
//        if(DataUtil.isEmpty(filePathList)){
//            return null;
//        }
//        SftpClient client = SftpClient.connect(host,port ,userName,password,timeout,aliveMax, keyFile);
//        for(String filePath : filePathList) {
//            File file = new File(filePath);
//            if (!file.exists()) {
//                throw new RuntimeException("文件" + filePath + "不存在");
//            }
//            String fileName = namespace + File.separator + file.getName();
//            client.put(filePath, baseDir + fileName);
//            ret.add(fileName);
//        }
//        client.disconnect();
//        return ret;
//    }
//
//    public static String remove2Sftp(String host,int port ,String userName,String password,Integer timeout,Integer aliveMax, String keyFile,String baseDir,
//                                     InputStream inputStream, String namespace,String... postFix)
//    {
//        try
//        {
//            SftpClient client = SftpClient.connect(host,port ,userName,password,timeout,aliveMax, keyFile);
//            String fileName =  namespace + "/" + UUID.randomUUID().toString();
//            if (DataUtil.isNotEmpty(postFix))
//            {
//                fileName = fileName + "." + postFix[0];
//            }
//            client.put(inputStream, baseDir + fileName);
//            client.disconnect();
//            return fileName;
//        }
//        catch (Exception e)
//        {
//            logger.error("上传文件异常：", e);
//            return null;
//        }
//    }
//
//    public static String remove2Sftp(String host,int port ,String userName,String password,Integer timeout,Integer aliveMax, String keyFile,String baseDir,
//                                     File file,String namespace,String... postFix)
//    {
//        SftpClient client = SftpClient.connect(host,port ,userName,password,timeout,aliveMax, keyFile);
//        String fileName =  namespace + File.separator + UUID.randomUUID().toString();
//        if (DataUtil.isNotEmpty(postFix))
//        {
//            fileName = fileName + "." + postFix[0];
//        }
//        client.put(file, baseDir + fileName);
//        client.disconnect();
//        return fileName;
//    }

    /** 移动文件到正式目录 */
    public static String removeFile(String disRootPath, String disDir, String orgDir, String url) {
        if (url != null && url.startsWith(Constants.TEMP_DIR)) {
            String dir = disRootPath + "/" + disDir;
            File dirFile = new File(dir);
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            File file = new File(orgDir + "/" + url);
            if (file.exists()) {
                String fileName = url.replace(Constants.TEMP_DIR, "");
                file.renameTo(new File(dir + "/" + fileName));
                file.delete();
                url = "/" + disDir + "/" + fileName;
            }
        }
        return url;
    }
}
