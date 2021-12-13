package com.ghf.exchange.otc.ordermessage.event;

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
public class AddOrderMessageEvent extends ApplicationEvent {

    @ApiModelProperty("id")

    private long id;

    @ApiModelProperty("消息编号，唯一")

    private String orderMessageCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("订单消息")

    private String orderMessageContent;

    @ApiModelProperty("息发送者的登陆用户名,用户英文名称，用户编码")

    private String orderMessageSenderUsername;

    @ApiModelProperty("消消息接收者的登陆用户名,用户英文名称，用户编码")

    private String orderMessageReceiverUsername;

    @ApiModelProperty("消息发送时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    public AddOrderMessageEvent() {
        super(IdUtil.generateLongId());
    }
}
