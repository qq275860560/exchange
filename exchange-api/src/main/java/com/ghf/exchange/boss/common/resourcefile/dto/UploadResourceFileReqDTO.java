package com.ghf.exchange.boss.common.resourcefile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author jiangyuanlin@163.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class UploadResourceFileReqDTO {

    @ApiModelProperty(value = "文件")
    @NotNull(message = "上传的文件不能为空")
    private MultipartFile file;

    @ApiModelProperty(value = "文件md5")
    private String md5;

    @ApiModelProperty("用途,0:普通文件,1:滑块验证码背景图片，默认为0，也就是默认为普通文件")
    private int resourceUsage;

}
