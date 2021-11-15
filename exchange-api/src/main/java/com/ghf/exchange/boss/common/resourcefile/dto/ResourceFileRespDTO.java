package com.ghf.exchange.boss.common.resourcefile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
public class ResourceFileRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("文件md5，唯一")
    private String md5;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty("文件大小，单位字节")
    private long size;

    @ApiModelProperty("文件路径,前端能直接访问的路径")
    private String path;

    @ApiModelProperty("分片总数")
    private int sliceCount;

    @ApiModelProperty("最大分片大小，单位字节")
    private long maxSliceSize;

    @ApiModelProperty("用途,0:普通文件,1:滑块验证码背景图片，默认为0，也就是默认为普通文件")
    private int resourceUsage;

    @ApiModelProperty("文件状态{0:未上传完毕,1:上传完毕}")
    private int status;

    @ApiModelProperty("创建人id")
    private long createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
