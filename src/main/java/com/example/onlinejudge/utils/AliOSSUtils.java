package com.example.onlinejudge.utils;

import com.aliyun.oss.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author admin
 * @version 1.0.0
 * @ClassName AliOSSUtils.java
 * @Description TODO
 * @createTime 2023年06月13日 15:08:00
 */
@Component
public class AliOSSUtils {

    @Autowired
    private AliOSSProperties aliOSSProperties;

    public String upload(MultipartFile file) throws Exception {
        //OSS参数
        String endpoint = aliOSSProperties.getEndpoint();
        String accessKeyId = aliOSSProperties.getAccessKeyId();
        String accessKeySecret = aliOSSProperties.getAccessKeySecret();
        String bucketName = aliOSSProperties.getBucketName();

        //获取输入流
        InputStream inputStream = file.getInputStream();

        //防止覆盖
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传到OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ossClient.putObject(bucketName,filename,inputStream);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + filename;

        //关闭OSS
        ossClient.shutdown();

        return url;
    }
}
