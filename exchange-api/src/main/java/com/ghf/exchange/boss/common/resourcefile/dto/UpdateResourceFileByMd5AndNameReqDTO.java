package com.ghf.exchange.boss.common.resourcefile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
public class UpdateResourceFileByMd5AndNameReqDTO {

    @ApiModelProperty(value = "文件md5")
    private String md5;

    @ApiModelProperty(value = "旧文件名称")
    private String oldName;

    @ApiModelProperty(value = "新文件名称")
    private String newName;

    @ApiModelProperty("用途,0:普通文件,1:滑块验证码背景图片，默认为0，也就是默认为普通文件")
    private int resourceUsage;

}
