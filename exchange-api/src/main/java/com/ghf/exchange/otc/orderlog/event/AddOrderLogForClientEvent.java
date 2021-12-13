package com.ghf.exchange.otc.orderlog.event;

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
@Setter
@Getter
@Slf4j
public class AddOrderLogForClientEvent extends ApplicationEvent {

    @ApiModelProperty("订单日志编码， 唯一")

    private String orderLogCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("操作类型，1:下单,2:付款:3:放行,4:取消,5:恢复")

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

    public AddOrderLogForClientEvent() {
        super(IdUtil.generateLongId());
    }
}
