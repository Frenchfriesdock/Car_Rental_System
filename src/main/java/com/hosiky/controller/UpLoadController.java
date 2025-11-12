package com.hosiky.controller;


import com.hosiky.client.MyMinioClient;
import com.hosiky.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class UpLoadController {

    private MyMinioClient minioClient;

    @Operation(summary = "upload file")
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws FileUploadException {
        String upload = minioClient.upload(file);
        return Result.ok(upload);
    }
}
