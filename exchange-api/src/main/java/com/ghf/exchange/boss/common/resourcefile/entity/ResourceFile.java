package com.ghf.exchange.boss.common.resourcefile.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Proxy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_resource_file")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class ResourceFile {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("文件md5，唯一")
    @Column(name = "md5")
    private String md5;

    @ApiModelProperty("文件名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty("文件类型")
    @Column(name = "content_type")
    private String contentType;

    @ApiModelProperty("文件大小，单位字节")
    @Column(name = "size")
    private long size;

    @ApiModelProperty("文件路径,前端能直接访问的路径")
    @Column(name = "path")
    private String path;

    @ApiModelProperty("分片总数")
    @Column(name = "slice_count")
    private int sliceCount;

    @ApiModelProperty("最大分片大小，单位字节")
    @Column(name = "max_slice_size")
    private long maxSliceSize;

    @ApiModelProperty("用途,0:普通文件,1:滑块验证码背景图片，默认为0，也就是默认为普通文件")
    @Column(name = "resource_usage")
    private int resourceUsage;

    @ApiModelProperty("文件状态{0:未上传完毕,1:上传完毕}")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建人id")
    @Column(name = "create_user_id")
    private long createUserId;

    @ApiModelProperty("创建人名称")
    @Column(name = "create_user_name")
    private String createUserName;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;
}
