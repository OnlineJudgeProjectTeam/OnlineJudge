package com.example.onlinejudge.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @version 1.0.0
 * @ClassName AliOSSProperties.java
 * @Description TODO
 * @createTime 2023年06月13日 14:59:00
 */
@Data
@Component
@ConfigurationProperties(prefix="aliyun.oss")
public class AliOSSProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}

