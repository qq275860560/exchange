package com.ghf.exchange.otc.order.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghf.exchange.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
@Setter
@Slf4j
public class AgreeUnPayOrderEvent extends ApplicationEvent {

    @ApiModelProperty("订单日志编码， 唯一")

    private String orderLogCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("操作类型，1:下单,2:付款:3:放行,4:取消,5:恢复,6:确认未付款,7:确认未放行")

    private int orderLogType;

    @ApiModelProperty("登录客户端")

    private String orderLogClientId;

    @ApiModelProperty("操作人，如果是后端服务器进行操作，此字段为空")

    private String orderLogUsername;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    @ApiModelProperty("IP地址")

    private String orderLogIpAddr;

    @ApiModelProperty("备注")

    private String remark;

    @ApiModelProperty("广告编码，唯一")
    private String advertiseCode;

    @ApiModelProperty("订单买卖类型,1:买币,2:卖币，跟广告买卖类型相反")
    private int orderBuySellType;
    @ApiModelProperty("订单顾客的登陆用户名,用户英文名称，用户编码")
    private String orderCustomerUsername;
    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")
    private String advertiseBusinessUsername;
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;
    @ApiModelProperty("付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;
    @ApiModelProperty("放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;
    @ApiModelProperty("申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appealTime;
    @ApiModelProperty("取消时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;
    @ApiModelProperty("确认未付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date agreeUnPayTime;
    @ApiModelProperty("确认未放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date agreeUnReleaseTime;

    public AgreeUnPayOrderEvent() {
        super(IdUtil.generateLongId());
    }
}
