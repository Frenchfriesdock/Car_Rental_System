package com.hosiky.config;

import com.hosiky.client.MyMinioClient;
import com.hosiky.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration   // 告诉 Spring 这是一个配置类
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties props) {
        return MinioClient.builder()
                .endpoint(props.getEndpoint())
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }

    @Bean
    public MyMinioClient myMinioClient(MinioClient minioClient,
                                       MinioProperties props) {
        return new MyMinioClient(minioClient, props);
    }
}