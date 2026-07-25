package com.kmbeast.controller;

import com.kmbeast.pojo.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${app.upload.base-url:}")
    private String uploadBaseUrl;

    /** 上传文件保存路径 */
    private static final String UPLOAD_DIR = "uploads/";

    /** 允许的图片类型 */
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        // 1. 验证文件是否为空
        if (file.isEmpty()) {
            return R.error("请选择要上传的文件");
        }

        // 2. 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return R.error("文件名不能为空");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex <= 0 || dotIndex == originalFilename.length() - 1) {
            return R.error("文件扩展名不合法");
        }
        String extension = originalFilename.substring(dotIndex).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            return R.error("仅支持 jpg、jpeg、png 格式的图片");
        }

        // 3. 验证文件大小和真实图片内容
        if (file.getSize() > MAX_FILE_SIZE) {
            return R.error("文件大小不能超过5MB");
        }
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                return R.error("文件内容不是有效图片");
            }
            if (image.getWidth() > 8000 || image.getHeight() > 8000) {
                return R.error("图片尺寸不能超过8000×8000像素");
            }
        } catch (IOException e) {
            log.warn("图片内容校验失败：{}", originalFilename, e);
            return R.error("图片内容校验失败");
        }

        try {
            // 4. 创建上传目录
            String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                return R.error("上传目录创建失败");
            }

            // 5. 生成唯一文件名
            String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

            // 6. 保存文件
            File destFile = new File(uploadPath + newFileName);
            file.transferTo(destFile);

            // 7. 返回访问URL
            String normalizedBaseUrl = uploadBaseUrl == null ? "" : uploadBaseUrl.trim();
            String url = normalizedBaseUrl.isEmpty()
                    ? ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/").path(newFileName).toUriString()
                    : normalizedBaseUrl.replaceAll("/$", "") + "/uploads/" + newFileName;
            log.info("文件上传成功：{}", url);

            return R.ok(url, "上传成功");
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败，请稍后重试");
        }
    }
}
