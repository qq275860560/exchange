package com.ghf.exchange.otc.orderappeal.entity;

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
@Table(name = "t_order_appeal")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class OrderAppeal {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("申诉编码，唯一")
    @Column(name = "order_appeal_code")
    private String orderAppealCode;

    @ApiModelProperty("订单编码")
    @Column(name = "order_code")
    private String orderCode;

    @ApiModelProperty("订单顾客的登陆用户名,用户英文名称，用户编码")
    @Column(name = "order_customer_username")
    private String orderCustomerUsername;

    @ApiModelProperty("广告编码")
    @Column(name = "advertise_code")
    private String advertiseCode;

    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")
    @Column(name = "advertise_business_username")
    private String advertiseBusinessUsername;

    @ApiModelProperty("申诉内容")
    @Column(name = "order_appeal_content")
    private String orderAppealContent;

    @ApiModelProperty("申诉状态,1:已申诉,2:已审核")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("申诉人的登陆用户名,用户英文名称，用户编码")
    @Column(name = "order_appeal_username")
    private String orderAppealUsername;

    @ApiModelProperty("申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("审核人的登陆用户名,用户英文名称，用户编码")
    @Column(name = "order_appeal_audit_username")
    private String orderAppealAuditUsername;

    @ApiModelProperty("审核时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "order_appeal_audit_time")
    private Date orderAppealAuditTime;

    @ApiModelProperty("审核结果")
    @Column(name = "order_appeal_audit_result")
    private String orderAppealAuditResult;

}
