package com.ghf.exchange.otc.ordermessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Proxy;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Document(collection = "t_order_message")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class OrderMessageWithMongo {

    @ApiModelProperty("id")
    @Id
    @Field(name = "id")
    private long id;

    @ApiModelProperty("消息编号，唯一")
    @Field(name = "order_message_code")
    private String orderMessageCode;

    @ApiModelProperty("订单编码")
    @Field(name = "order_code")
    private String orderCode;

    @ApiModelProperty("订单消息")
    @Field(name = "order_message_content")
    private String orderMessageContent;

    @ApiModelProperty("息发送者的登陆用户名,用户英文名称，用户编码")
    @Field(name = "order_message_sender_username")
    private String orderMessageSenderUsername;

    @ApiModelProperty("消息接收者的登陆用户名,用户英文名称，用户编码")
    @Field(name = "order_message_receiver_username")
    private String orderMessageReceiverUsername;

    @ApiModelProperty("消息发送时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(name = "create_time")
    private Date createTime;

}
