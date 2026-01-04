package com.polo.Blog.Controller;

import com.polo.Blog.Utils.Result;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private MinioClient minioClient; // 1. 注入 MinIO 客户端

    // 从配置文件读取桶名和地址，避免硬编码
    @Value("${minio.bucketName:travel}")
    private String bucketName;

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @PostMapping("/file")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        try {
            // A. 生成新文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            // B. 【核心修改】上传到 MinIO
            // 也就是把流通过网络发给 9000 端口
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)      // 桶名: travel
                            .object(newFileName)     // 文件名
                            .stream(file.getInputStream(), file.getSize(), -1) // 文件流
                            .contentType(file.getContentType()) // 文件类型
                            .build()
            );

            // C. 拼接 MinIO 的访问地址
            // 格式: http://localhost:9000/travel/文件名
            String fileUrl = endpoint + "/" + bucketName + "/" + newFileName;

            return Result.success(fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("MinIO上传失败: " + e.getMessage());
        }
    }
}
