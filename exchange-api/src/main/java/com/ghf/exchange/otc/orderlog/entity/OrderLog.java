package com.ghf.exchange.otc.orderlog.entity;

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
@Table(name = "t_order_log")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class OrderLog {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("订单日志编码， 唯一")
    @Column(name = "order_log_code")
    private String orderLogCode;

    @ApiModelProperty("订单编码")
    @Column(name = "order_code")
    private String orderCode;

    @ApiModelProperty("操作类型，1:下单,2:付款:3:放行,4:取消,5:恢复,6:同意未付款申诉,7:同意未放行申诉")
    @Column(name = "order_log_type")
    private int orderLogType;

    @ApiModelProperty("登录客户端")
    @Column(name = "order_log_client_id")
    private String orderLogClientId;

    @ApiModelProperty("操作人，如果是后端服务器进行操作，此字段为空")
    @Column(name = "order_log_username")
    private String orderLogUsername;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("IP地址")
    @Column(name = "order_log_ip_addr")
    private String orderLogIpAddr;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark;

}
