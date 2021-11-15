package com.ghf.exchange.otc.ordermessage.dto;

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
public class OrderMessageRespDTO {
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

    @ApiModelProperty("消息接收者的登陆用户名,用户英文名称，用户编码")

    private String orderMessageReceiverUsername;

    @ApiModelProperty("状态,0:未读,1:已读")

    private int status;

    @ApiModelProperty("消息发送时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    @ApiModelProperty("消息读取时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date readTime;

}
