package com.mc.web.controller.common;

import com.mc.common.config.RuoYiConfig;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.utils.StringUtils;
import com.mc.common.utils.file.FileUtils;
import com.mc.framework.config.ServerConfig;
import com.mc.web.controller.common.oss.OSSAliyunFileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 *
 * @author caidu
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private OSSAliyunFileStorageService ossAliyunFileStorageService;

    private static final String FILE_DELIMETER = ",";

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception {
        try {
            // 生成唯一文件名
            String objectName = generateObjectName(file.getOriginalFilename());

            // 上传到OSS
            String url = ossAliyunFileStorageService.store(objectName, file.getInputStream());

            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", objectName);
            ajax.put("newFileName", FileUtils.getName(objectName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */

    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files) {
                // 生成唯一文件名
                String objectName = generateObjectName(file.getOriginalFilename());

                // 上传到OSS
                String url = ossAliyunFileStorageService.store(objectName, file.getInputStream());

                urls.add(url);
                fileNames.add(objectName);
                newFileNames.add(FileUtils.getName(objectName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 生成对象存储的文件名（保留原始文件名+时间戳+随机数，处理特殊字符）
     *
     * @param originalFilename 原始文件名
     * @return 生成的唯一文件名
     */
    private String generateObjectName(String originalFilename) {
        // 1. 处理原始文件名：过滤特殊符号（避免存储/访问问题），替换为下划线
        String cleanOriginalName = filterSpecialChar(originalFilename);
        // 2. 拆分【文件名主体】和【扩展名】（分离最后一个.后的内容）
        String mainName = cleanOriginalName; // 文件名主体（无扩展名）
        String extension = "";               // 扩展名（含.，无则为空）
        int dotIndex = cleanOriginalName.lastIndexOf('.');
        if (dotIndex > 0) {
            mainName = cleanOriginalName.substring(0, dotIndex);  // 截取文件名主体
            extension = cleanOriginalName.substring(dotIndex);    // 截取扩展名（含.）
        }

        // 3. 生成唯一标识：时间戳（毫秒）+ 4位随机数
        long timestamp = System.currentTimeMillis();
        int randomNum = (int) (Math.random() * 10000);
        // 补零：让随机数固定4位（如123→0123），格式更统一
        String randomStr = String.format("%04d", randomNum);

        // 4. 拼接最终文件名：upload/原始文件名主体_时间戳_4位随机数.扩展名
        return String.format("upload/%s_%d_%s%s", mainName, timestamp, randomStr, extension);
    }

    /**
     * 过滤文件名中的特殊字符，替换为下划线
     * 过滤范围：/ \ : * ? " < > | 空格 （对象存储通用禁止字符）
     */
    private String filterSpecialChar(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unknown_file"; // 空文件名默认值
        }
        // 正则替换所有特殊字符为下划线，连续多个特殊字符合并为一个下划线
        return filename.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + FileUtils.stripPrefix(resource);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}
