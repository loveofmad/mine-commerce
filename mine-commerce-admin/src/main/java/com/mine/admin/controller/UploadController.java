package com.mine.admin.controller;

import com.mine.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${upload.path:C:/mine-upload/}")
    private String uploadPath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.failed("请选择文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.failed("文件大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.failed("文件格式不正确");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(suffix)) {
            return Result.failed("仅支持 jpg、jpeg、png、gif、webp 格式");
        }

        String fileName = UUID.randomUUID().toString() + suffix;
        File dest = new File(uploadPath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            String url = "/upload/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.failed("上传失败");
        }
    }
}
