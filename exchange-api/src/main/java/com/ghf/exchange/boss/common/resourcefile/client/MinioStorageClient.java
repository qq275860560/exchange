package com.ghf.exchange.boss.common.resourcefile.client;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
public interface MinioStorageClient {

    /**
     * 文件url前半段
     *
     * @return 前半段
     */
    String getObjectPrefixUrl();

    /**
     * 上传本地文件
     *
     * @param objectKey   文件key
     * @param filePath    文件路径
     * @param contentType MINE内容类型,比如image/png
     * @return 文件url
     */
    String uploadFile(String objectKey, String filePath, String contentType);

    /**
     * 流式上传文件
     *
     * @param objectKey   文件key
     * @param inputStream 文件输入流
     * @param contentType MINE内容类型,比如image/png
     * @return 文件url
     */
    String uploadInputStream(String objectKey, InputStream inputStream, String contentType);

    /**
     * 上传MultipartFile文件
     *
     * @param objectKey 文件key
     * @param file      MultipartFile文件
     * @return 文件url
     */
    String uploadMultipartFile(String objectKey, MultipartFile file);

    /**
     * 下载文件
     *
     * @param objectKey 文件key
     * @return 文件流
     */
    InputStream download(String objectKey);

    /**
     * 文件复制
     *
     * @param sourceObjectKey 源文件key
     * @param objectKey       文件key
     * @return 新文件url
     */
    String copyFile(String sourceObjectKey, String objectKey);

    /**
     * 删除文件
     *
     * @param objectKey 文件key
     */
    void deleteFile(String objectKey);

    /**
     * 获取文件签名url
     *
     * @param objectKey 文件key
     * @param expires   签名有效时间  单位秒
     * @return 文件签名地址
     */
    String getSignedUrl(String objectKey, int expires);

    /**
     * 根据前缀获取url列表
     *
     * @param prefix
     * @return
     */
    List<String> getListByPrefix(String prefix);
}
