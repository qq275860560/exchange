package com.ghf.exchange.otc.orderappeal.dto;

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
public class OrderAppealRespDTO {
    @ApiModelProperty("id")

    private long id;

    @ApiModelProperty("申诉编码，唯一")

    private String orderAppealCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("申诉类型,1:对方未付款 2：对方未放行 3:其他")
    private int orderAppealType;

    @ApiModelProperty("订单申诉前状态,1:已下单,2:已付款,3:已放行,4:申诉中,5:已取消")
    private String orderOldStatus;

    @ApiModelProperty("订单顾客的登陆用户名,用户英文名称，用户编码")

    private String orderCustomerUsername;

    @ApiModelProperty("广告编码")

    private String advertiseCode;

    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")

    private String advertiseBusinessUsername;

    @ApiModelProperty("申诉内容")

    private String orderAppealContent;

    @ApiModelProperty("申诉状态,1:已申诉,2:已审核,3:已取消")
    private int status;

    @ApiModelProperty("申诉人的登陆用户名,用户英文名称，用户编码")

    private String orderAppealUsername;

    @ApiModelProperty("申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    @ApiModelProperty("审核人的登陆用户名,用户英文名称，用户编码")

    private String orderAppealAuditUsername;

    @ApiModelProperty("审核时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date orderAppealAuditTime;

    @ApiModelProperty("审核结果")

    private String orderAppealAuditResult;

    @ApiModelProperty("取消人的登陆用户名,用户英文名称，用户编码")

    private String orderAppealCanelUsername;

    @ApiModelProperty("取消时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date orderAppealCanelTime;

    @ApiModelProperty("取消结果")

    private String orderAppealCancelResult;

}
