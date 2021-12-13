package com.ghf.exchange.boss.common.resourcefile.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.resourcefile.client.MinioStorageClient;
import com.ghf.exchange.boss.common.resourcefile.dto.*;
import com.ghf.exchange.boss.common.resourcefile.entity.QResourceFile;
import com.ghf.exchange.boss.common.resourcefile.entity.ResourceFile;
import com.ghf.exchange.boss.common.resourcefile.enums.ResourceFileStatusEnum;
import com.ghf.exchange.boss.common.resourcefile.repository.ResourceFileRepository;
import com.ghf.exchange.boss.common.resourcefile.service.ResourceFileService;
import com.ghf.exchange.dto.BaseIdDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class ResourceFileServiceImpl extends BaseServiceImpl<ResourceFile, Long> implements ResourceFileService {
    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private MinioStorageClient minioStorageClient;
    @Lazy
    @Resource
    private ResourceFileService resourceFileService;

    public ResourceFileServiceImpl(ResourceFileRepository repository) {
        super(repository);
    }

    @Override
    public Result<PageRespDTO<ResourceFileRespDTO>> pageResourceFile(
            PageResourceFileReqDTO pageResourceFileReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageResourceFileReqDTO.getName())) {
            predicate.and(QResourceFile.resourceFile.name.contains(pageResourceFileReqDTO.getName()));
        }
        PageRespDTO<ResourceFileRespDTO> pageRespDTO = this.page(predicate, pageResourceFileReqDTO, ResourceFileRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Override
    public Result<List<ResourceFileRespDTO>> listResourceFile(
            ListResourceFileReqDTO listResourceFileReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listResourceFileReqDTO.getName())) {
            predicate.and(QResourceFile.resourceFile.name.contains(listResourceFileReqDTO.getName()));
        }
        List<ResourceFileRespDTO> list = this.list(predicate, ResourceFileRespDTO.class);
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<ResourceFileRespDTO> getResourceFile(BaseIdDTO getResourceFileReqDTO) {
        //TODO 权限判断
        long id = getResourceFileReqDTO.getId();
        ResourceFile resourceFile = this.get(id);
        //返回
        ResourceFileRespDTO resourceFileRespDTO = ModelMapperUtil.map(resourceFile, ResourceFileRespDTO.class);
        return new Result<>(resourceFileRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<ResourceFileRespDTO> getResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        //TODO 权限判断
        String md5 = getResourceFileByMd5AndNameReqDTO.getMd5();
        String name = getResourceFileByMd5AndNameReqDTO.getName();
        Predicate predicate = QResourceFile.resourceFile.md5.eq(md5).and(QResourceFile.resourceFile.name.eq(name));
        ResourceFile resourceFile = this.get(predicate);
        //返回
        ResourceFileRespDTO resourceFileRespDTO = ModelMapperUtil.map(resourceFile, ResourceFileRespDTO.class);
        return new Result<>(resourceFileRespDTO);
    }

    @SneakyThrows
    private void downloadResourceFile(ResourceFile resourceFile) {
        String md5 = resourceFile.getMd5();
        InputStream inputStream = minioStorageClient.download(md5);
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        OutputStream outputStream = response.getOutputStream();

        try {
            response.addHeader("pargam", "no-cache");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(resourceFile.getName(), "UTF-8"));
            response.setContentType(resourceFile.getContentType());
            response.setContentLength((int) resourceFile.getSize());
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @Override
    @SneakyThrows
    public void downloadResourceFile(BaseIdDTO getResourceFileReqDTO) {
        //TODO 权限判断
        long id = getResourceFileReqDTO.getId();
        ResourceFile resourceFile = this.get(id);
        downloadResourceFile(resourceFile);
    }

    @Override
    @SneakyThrows
    public void downloadResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        //TODO 权限判断
        String md5 = getResourceFileByMd5AndNameReqDTO.getMd5();
        String name = getResourceFileByMd5AndNameReqDTO.getName();
        Predicate predicate = QResourceFile.resourceFile.md5.eq(md5).and(QResourceFile.resourceFile.name.eq(name));
        ResourceFile resourceFile = this.get(predicate);
        downloadResourceFile(resourceFile);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsResourceFileByMd5(GetResourceFileByMd5ReqDTO getResourceFileByMd5ReqDTO) {
        //TODO 权限判断
        String md5 = getResourceFileByMd5ReqDTO.getMd5();
        Predicate predicate = QResourceFile.resourceFile.md5.eq(md5);
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        //TODO 权限判断
        String md5 = getResourceFileByMd5AndNameReqDTO.getMd5();
        String name = getResourceFileByMd5AndNameReqDTO.getName();
        Predicate predicate = QResourceFile.resourceFile.md5.eq(md5).and(QResourceFile.resourceFile.name.eq(name));
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<ResourceFileRespDTO> uploadResourceFile(UploadResourceFileReqDTO addResourceFileReqDTO) {
        String md5 = addResourceFileReqDTO.getMd5();
        MultipartFile file = addResourceFileReqDTO.getFile();
        int resourceUsage = addResourceFileReqDTO.getResourceUsage();

        ResourceFile resourceFile = new ResourceFile();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        resourceFile.setCreateUserId(currentLoginUser.getId());
        resourceFile.setCreateUserName(currentLoginUser.getUsername());

        resourceFile.setCreateTime(new Date());

        //md5没有传入时后台生成一个
        if (ObjectUtils.isEmpty(md5)) {
            md5 = DigestUtils.md5Hex(file.getInputStream());
        }
        //获得下载路径
        String path = null;
        GetResourceFileByMd5ReqDTO getResourceFileByMd5ReqDTO = new GetResourceFileByMd5ReqDTO();
        getResourceFileByMd5ReqDTO.setMd5(md5);
        boolean b = this.existsResourceFileByMd5(getResourceFileByMd5ReqDTO).getData();
        if (b) {
            path = minioStorageClient.getObjectPrefixUrl() + md5;

        } else {
            path = minioStorageClient.uploadMultipartFile(md5, file);
        }

        //初始化id
        resourceFile.setId(IdUtil.generateLongId());
        resourceFile.setMd5(md5);
        resourceFile.setName(file.getOriginalFilename());
        String contentType = file.getContentType();
        resourceFile.setContentType(contentType);
        long size = file.getSize();
        resourceFile.setSize(size);
        resourceFile.setPath(path);
        resourceFile.setSliceCount(1);
        resourceFile.setMaxSliceSize(size);
        resourceFile.setResourceUsage(resourceUsage);
        resourceFile.setStatus(ResourceFileStatusEnum.FINISH.getCode());
        //新增到数据库
        this.add(resourceFile);

        //返回
        ResourceFileRespDTO resourceFileRespDTO = ModelMapperUtil.map(resourceFile, ResourceFileRespDTO.class);
        return new Result<>(resourceFileRespDTO);

    }

    @Override
    @SneakyThrows
    public Result<ResourceFileRespDTO> updateResourceFileByMd5AndName(UpdateResourceFileByMd5AndNameReqDTO updateResourceFileByMd5AndNameReqDTO) {
        String md5 = updateResourceFileByMd5AndNameReqDTO.getMd5();
        String oldName = updateResourceFileByMd5AndNameReqDTO.getOldName();
        String newName = updateResourceFileByMd5AndNameReqDTO.getNewName();
        int resourceUsage = updateResourceFileByMd5AndNameReqDTO.getResourceUsage();
        GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO = new GetResourceFileByMd5AndNameReqDTO();
        getResourceFileByMd5AndNameReqDTO.setMd5(md5);
        getResourceFileByMd5AndNameReqDTO.setName(oldName);
        ResourceFileRespDTO resourceFileRespDTO = this.getResourceFileByMd5AndName(getResourceFileByMd5AndNameReqDTO).getData();
        ResourceFile resourceFile = ModelMapperUtil.map(resourceFileRespDTO, ResourceFile.class);
        resourceFile.setName(newName);
        resourceFile.setResourceUsage(resourceUsage);
        this.update(resourceFile);

        //更新到数据库
        this.update(resourceFile);
        //返回
        ResourceFileRespDTO resourceFileRespDTO2 = ModelMapperUtil.map(resourceFile, ResourceFileRespDTO.class);
        return new Result<>(resourceFileRespDTO2);
    }

    @Override
    @SneakyThrows
    public Result<Void> deleteResourceFileByMd5AndName(GetResourceFileByMd5AndNameReqDTO getResourceFileByMd5AndNameReqDTO) {
        //TODO 权限判断
        String md5 = getResourceFileByMd5AndNameReqDTO.getMd5();
        String name = getResourceFileByMd5AndNameReqDTO.getName();
        Predicate predicate = QResourceFile.resourceFile.md5.eq(md5).and(QResourceFile.resourceFile.name.eq(name));
        this.delete(predicate);
        return new Result<>();
    }

}
