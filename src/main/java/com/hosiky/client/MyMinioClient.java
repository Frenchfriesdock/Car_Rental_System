package com.hosiky.client;

import com.hosiky.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class MyMinioClient {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    public String upload(MultipartFile file) throws FileUploadException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("上传失败: {}", fileName, e);
            throw new FileUploadException("上传失败: " + fileName, e);
        }
        return minioProperties.getBaseUrl() + "/" + fileName;
    }
}