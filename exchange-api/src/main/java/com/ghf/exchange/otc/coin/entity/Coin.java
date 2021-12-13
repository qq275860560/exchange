package com.ghf.exchange.otc.coin.entity;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_coin")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Coin {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("币种编码")
    @Column(name = "coin_code")
    private String coinCode;

    @ApiModelProperty("币种名称")
    @Column(name = "coin_name")
    private String coinName;

    @ApiModelProperty("币种单位")
    @Column(name = "coin_unit")
    private String coinUnit;

    @ApiModelProperty("市场价(美元)")
    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @ApiModelProperty("币种交易手续费比例")
    @Column(name = "coin_rate")
    private BigDecimal coinRate;

    @ApiModelProperty("单个广告最小库存量")
    @Column(name = "per_advertise_min_amount")
    private BigDecimal perAdvertiseMinAmount;

    @ApiModelProperty("单个广告最大库存量")
    @Column(name = "per_advertise_max_amount")
    private BigDecimal perAdvertiseMaxAmount;

    @ApiModelProperty("单笔订单最小交易量")
    @Column(name = "per_order_min_amount")
    private BigDecimal perOrderMinAmount;

    @ApiModelProperty("单笔订单最大交易量")
    @Column(name = "per_order_max_amount")
    private BigDecimal perOrderMaxAmount;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
