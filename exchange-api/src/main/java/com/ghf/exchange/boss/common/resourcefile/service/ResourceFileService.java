package com.ghf.exchange.boss.common.resourcefile.service;

import com.ghf.exchange.boss.common.resourcefile.dto.*;
import com.ghf.exchange.boss.common.resourcefile.entity.ResourceFile;
import com.ghf.exchange.dto.BaseIdDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface ResourceFileService extends BaseService<ResourceFile, Long> {

    /**
     * 分页搜索资源文件
     *
     * @param pageResourceFileReqDTO
     * @return
     */
    Result<PageRespDTO<ResourceFileRespDTO>> pageResourceFile(PageResourceFileReqDTO pageResourceFileReqDTO);

    /**
     * 列出资源文件
     *
     * @param listResourceFileReqDTO
     * @return
     */
    Result<List<ResourceFileRespDTO>> listResourceFile(ListResourceFileReqDTO listResourceFileReqDTO);

    /**
     * 根据资源文件id获取资源文件详情
     *
     * @param getResourceFileReqDTO
     * @return
     */
    Result<ResourceFileRespDTO> getResourceFile(BaseIdDTO getResourceFileReqDTO);

    /**
     * 根据资源文件md5和文件名称获取资源文件详情
     *
     * @param getResourceFileByMd5AndNameReqDTO
     * @return
     */
    Result<ResourceFileRespDTO> getResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO);

    /**
     * 根据资源文件id下载资源文件
     *
     * @param getResourceFileReqDTO
     */
    void downloadResourceFile(BaseIdDTO getResourceFileReqDTO);

    /**
     * 根据资源文件md5和名称下载资源文件
     *
     * @param getResourceFileByMd5AndNameReqDTO
     */
    void downloadResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO);

    /**
     * 根据资源文件md5判断资源文件是否存在
     *
     * @param getResourceFileByMd5ReqDTO
     * @return
     */
    Result<Boolean> existsResourceFileByMd5(GetResourceFileByMd5ReqDTO getResourceFileByMd5ReqDTO);

    /**
     * 根据资源文件md5和名称判断资源文件是否存在
     *
     * @param getResourceFileByMd5AndNameReqDTO
     * @return
     */
    Result<Boolean> existsResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO);

    /**
     * 上传资源文件
     *
     * @param uploadResourceFileReqDTO
     * @return
     */
    Result<ResourceFileRespDTO> uploadResourceFile(UploadResourceFileReqDTO uploadResourceFileReqDTO);

    /**
     * 根据资源文件md5和名称修改资源文件
     *
     * @param updateResourceFileByMd5AndNameReqDTO
     * @return
     */
    Result<ResourceFileRespDTO> updateResourceFileByMd5AndName(UpdateResourceFileByMd5AndNameReqDTO updateResourceFileByMd5AndNameReqDTO);

    /**
     * 根据资源文件md5和名称删除资源文件
     *
     * @param getResourceFileByMd5AndNameReqDTO
     * @return
     */
    Result<Void> deleteResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO);

}