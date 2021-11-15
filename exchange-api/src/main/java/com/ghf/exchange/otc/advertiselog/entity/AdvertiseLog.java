package com.ghf.exchange.otc.advertiselog.entity;

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
@Table(name = "t_advertise_log")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class AdvertiseLog {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("广告日志编码， 唯一")
    @Column(name = "advertise_log_code")
    private String advertiseLogCode;

    @ApiModelProperty("广告编码")
    @Column(name = "advertise_code")
    private String advertiseCode;

    @ApiModelProperty("操作类型，1:发布,2:上架:3:下架,4:删除")
    @Column(name = "advertise_log_type")
    private int advertiseLogType;

    @ApiModelProperty("登录客户端")
    @Column(name = "advertise_log_client_id")
    private String advertiseLogClientId;

    @ApiModelProperty("操作人，如果是后端服务器进行操作，此字段为空")
    @Column(name = "advertise_log_username")
    private String advertiseLogUsername;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("IP地址")
    @Column(name = "advertise_log_ip_addr")
    private String advertiseLogIpAddr;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark;

}
