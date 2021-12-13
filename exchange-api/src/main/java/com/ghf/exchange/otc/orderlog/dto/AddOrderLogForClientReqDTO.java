package com.ghf.exchange.otc.orderlog.dto;

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
public class AddOrderLogForClientReqDTO {

    @ApiModelProperty("订单日志编码， 唯一")

    private String orderLogCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("操作类型，1:下单,2:付款:3:放行,4:取消,5:恢复,6:同意未付款申诉,7:同意未放行申诉")

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
}
