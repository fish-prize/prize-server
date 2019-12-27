package com.ykm.server.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/7/12-16:34
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Component
public class OSSUtils {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    private static final String bucketName = "cximg";

    public boolean uploadFile(File file, String name){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectMetadata metadata = new ObjectMetadata();


        metadata.setHeader("Content-Type",ContentTypeUtil.getContentType(file));
        metadata.setContentLength(file.length());
        String key = "mng/"+file.getName();
        boolean isExist = ossClient.doesObjectExist(bucketName, key);
        if(isExist){
            ossClient.deleteObject(bucketName, key);
        }
        PutObjectResult result = ossClient.putObject(bucketName, key, file,metadata);
        ossClient.shutdown();
        return result!=null;
    }
}
