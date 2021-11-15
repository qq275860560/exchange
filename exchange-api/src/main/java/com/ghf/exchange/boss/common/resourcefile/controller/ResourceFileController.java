package com.ghf.exchange.boss.common.resourcefile.controller;

import com.ghf.exchange.boss.common.resourcefile.dto.*;
import com.ghf.exchange.boss.common.resourcefile.service.ResourceFileService;
import com.ghf.exchange.dto.BaseIdDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "资源文件接口", tags = {"资源文件接口"})

@RestController
@Lazy
@Slf4j
public class ResourceFileController {
    @Lazy
    @Resource
    private ResourceFileService resourceFileService;

    @ApiOperation(value = "分页搜索资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/pageResourceFile")
    @SneakyThrows
    public Result<PageRespDTO<ResourceFileRespDTO>> pageResourceFile(@RequestBody PageResourceFileReqDTO pageResourceFileReqDTO) {
        return resourceFileService.pageResourceFile(pageResourceFileReqDTO);
    }

    @ApiOperation(value = "列出资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/listResourceFile")
    @SneakyThrows
    public Result<List<ResourceFileRespDTO>> listResourceFile(@RequestBody ListResourceFileReqDTO listResourceFileReqDTO) {
        return resourceFileService.listResourceFile(listResourceFileReqDTO);
    }

    @ApiOperation(value = "根据资id获取资源文件详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/getResourceFile")
    @SneakyThrows
    public Result<ResourceFileRespDTO> getResourceFile(@RequestBody BaseIdDTO getResourceFileReqDTO) {
        return resourceFileService.getResourceFile(getResourceFileReqDTO);
    }

    @ApiOperation(value = "根据资源文件md5和名称获取资源文件详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/getResourceFileByMd5AndName")
    @SneakyThrows
    public Result<ResourceFileRespDTO> getResourceFileByMd5AndName(@RequestBody GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        return resourceFileService.getResourceFileByMd5AndName(getResourceFileByMd5AndNameReqDTO);
    }

    //curl -i -X POST 'http://localhost:8080/api/resourceFile/uploadResourceFile'   -F "file=@456.png" -F "md5=1"  -H "Content-Type: multipart/form-data" -H "Authorization:bearer $token"

    @ApiOperation(value = "上传资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/uploadResourceFile")
    @SneakyThrows
    public Result<ResourceFileRespDTO> uploadResourceFile(@Valid UploadResourceFileReqDTO uploadResourceFileReqDTO) {
        return resourceFileService.uploadResourceFile(uploadResourceFileReqDTO);
    }

    //curl -i -X POST 'http://localhost:8080/api/resourceFile/downloadResourceFile'    -d '{"id":"xxx"}'  -H "Content-Type: application/json"  -H "Authorization:bearer $token"

    @ApiOperation(value = "下载资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/downloadResourceFile")
    @SneakyThrows
    public void downloadResourceFile(@RequestBody BaseIdDTO getResourceFileReqDTO) {
        resourceFileService.downloadResourceFile(getResourceFileReqDTO);
    }

    @ApiOperation(value = "根据资源文件md5和名称下载资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/downloadResourceFileByMd5AndName")
    @SneakyThrows
    public void downloadResourceFileByMd5AndName(@RequestBody GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        resourceFileService.downloadResourceFileByMd5AndName(getResourceFileByMd5AndNameReqDTO);
    }

    @ApiOperation(value = "根据资源文件md5和名称修改资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/updateResourceFileByMd5AndName")
    @SneakyThrows
    public Result<ResourceFileRespDTO> updateResourceFileByMd5AndName(@RequestBody UpdateResourceFileByMd5AndNameReqDTO updateResourceFileByMd5AndNameReqDTO) {
        return resourceFileService.updateResourceFileByMd5AndName(updateResourceFileByMd5AndNameReqDTO);
    }

    @ApiOperation(value = "根据资源文件md5和名称删除资源文件", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/resourceFile/deleteResourceFileByMd5AndName")
    @SneakyThrows
    public Result<Void> deleteResourceFileByMd5AndName(@RequestBody GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        return resourceFileService.deleteResourceFileByMd5AndName(getResourceFileByMd5AndNameReqDTO);
    }

}
