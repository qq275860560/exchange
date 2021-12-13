package com.ghf.exchange.boss.common.resourcefile.client.impl;

import com.ghf.exchange.boss.common.resourcefile.client.MinioStorageClient;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class MinioStorageClientImpl implements MinioStorageClient {

    /**
     * minio参数
     */
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.serverUrl}")
    private String serverUrl;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.defaultBucketName}")
    private String bucket;

    private MinioClient client;

    @PostConstruct
    @SneakyThrows
    public MinioClient init() {
        log.info("创建Minio开始:建立连接");
        client = MinioClient.builder().endpoint(serverUrl).credentials(accessKey, secretKey).build();

        // 判断桶是否存在
        boolean isExist = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!isExist) {
            // 新建桶
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        // 设置桶读写权限
        client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
        log.info("创建minio结束");
        return client;
    }

    /**
     * 桶占位符
     */
    private static final String BUCKET_PARAM = "${bucket}";
    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只读
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";

    @SneakyThrows
    @Override
    public String getObjectPrefixUrl() {
        return String.format("%s/%s/", endpoint, bucket);
    }

    @SneakyThrows
    @Override
    public String uploadFile(String objectKey, String filePath, String contentType) {
        client.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(objectKey).filename(filePath).contentType(contentType).build());
        return getObjectPrefixUrl() + objectKey;
    }

    @SneakyThrows
    @Override
    public String uploadInputStream(String objectKey, InputStream inputStream, String contentType) {
        client.putObject(PutObjectArgs.builder().bucket(bucket).object(objectKey).stream(inputStream, inputStream.available(), -1).contentType(contentType).build());
        return getObjectPrefixUrl() + objectKey;
    }

    @SneakyThrows
    @Override
    public String uploadMultipartFile(String objectKey, MultipartFile file) {
        InputStream inputStream = file.getInputStream();
        String contentType = file.getContentType();
        return this.uploadInputStream(objectKey, inputStream, contentType);
    }

    @SneakyThrows
    @Override
    public InputStream download(String objectKey) {
        return client.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
    }

    @SneakyThrows
    @Override
    public String copyFile(String sourceObjectKey, String objectKey) {
        CopySource source = CopySource.builder().bucket(bucket).object(sourceObjectKey).build();
        client.copyObject(CopyObjectArgs.builder().bucket(bucket).object(objectKey).source(source).build());
        return getObjectPrefixUrl() + objectKey;
    }

    @SneakyThrows
    @Override
    public void deleteFile(String objectKey) {
        client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
    }

    @SneakyThrows
    @Override
    public String getSignedUrl(String objectKey, int expires) {
        return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(objectKey).expiry(expires).build());
    }

    @SneakyThrows
    @Override
    public List<String> getListByPrefix(String prefix) {
        ListObjectsArgs.Builder builder = ListObjectsArgs.builder();
        builder.bucket(bucket);
        builder.prefix(prefix);
        builder.recursive(true);
        ListObjectsArgs args = builder.build();
        Iterable<Result<Item>> results = client.listObjects(args);
        List<String> fullPaths = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            fullPaths.add(getObjectPrefixUrl() + item.objectName());
        }
        return fullPaths;
    }
}
