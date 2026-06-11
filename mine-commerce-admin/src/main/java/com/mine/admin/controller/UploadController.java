package com.mine.admin.controller;

import com.mine.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${upload.path:C:/mine-upload/}")
    private String uploadPath;

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.failed("请选择文件");
        }
        
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
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
