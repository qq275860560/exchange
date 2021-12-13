package com.ghf.exchange.otc.advertiseamountlog.entity;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_advertise_amount_log")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class AdvertiseAmountLog {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("广告库存数量日志编码， 唯一")
    @Column(name = "advertise_amount_log_code")
    private String advertiseAmountLogCode;

    @ApiModelProperty("广告编码")
    @Column(name = "advertise_code")
    private String advertiseCode;

    @ApiModelProperty("操作类型，1:冻结广告库存数量,2:解冻广告库存数量3:扣减广告冻结库存数量")
    @Column(name = "advertise_amount_log_type")
    private int advertiseAmountLogType;

    @ApiModelProperty("变动库存数量")
    @Column(name = "amount")
    private BigDecimal amount;

    @ApiModelProperty("变动之前总库存数量")
    @Column(name = "before_advertise_total_amount")
    private BigDecimal beforeAdvertiseTotalAmount;

    @ApiModelProperty("变动之前可用库存数量")
    @Column(name = "before_advertise_available_amount")
    private BigDecimal beforeAdvertiseAvailableAmount;

    @ApiModelProperty("变动之前冻结库存数量")
    @Column(name = "before_advertise_frozen_amount")
    private BigDecimal beforeAdvertiseFrozenAmount;

    @ApiModelProperty("变动之后总库存数量")
    @Column(name = "after_advertise_total_amount")
    private BigDecimal afterAdvertiseTotalAmount;

    @ApiModelProperty("变动之后可用库存数量")
    @Column(name = "after_advertise_available_amount")
    private BigDecimal afterAdvertiseAvailableAmount;

    @ApiModelProperty("变动之后冻结库存数量")
    @Column(name = "after_advertise_frozen_amount")
    private BigDecimal afterAdvertiseFrozenAmount;

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
