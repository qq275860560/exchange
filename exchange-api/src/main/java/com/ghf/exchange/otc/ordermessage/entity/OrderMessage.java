package com.ghf.exchange.otc.ordermessage.entity;

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
@Table(name = "t_order_message")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class OrderMessage {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("消息编号，唯一")
    @Column(name = "order_message_code")
    private String orderMessageCode;

    @ApiModelProperty("订单编码")
    @Column(name = "order_code")
    private String orderCode;

    @ApiModelProperty("订单消息类型,1:文本,2:图片")
    @Column(name = "order_message_type")
    private int orderMessageType;

    @ApiModelProperty("订单消息内容")
    @Column(name = "order_message_content")
    private String orderMessageContent;

    @ApiModelProperty("息发送者的登陆用户名,用户英文名称，用户编码")
    @Column(name = "order_message_sender_username")
    private String orderMessageSenderUsername;

    @ApiModelProperty("消消息接收者的登陆用户名,用户英文名称，用户编码")
    @Column(name = "order_message_receiver_username")
    private String orderMessageReceiverUsername;

    @ApiModelProperty("消息发送时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
