package com.ghf.exchange.otc.coin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import java.math.BigDecimal;
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
public class CoinRespDTO {
    @ApiModelProperty("id")
    private long id;

    @ApiModelProperty("数字货币编码")

    private String coinCode;

    @ApiModelProperty("数字货币名称")

    private String coinName;

    @ApiModelProperty("数字货币单位")

    private String coinUnit;

    @ApiModelProperty("市场价(美元)")

    private BigDecimal marketPrice;

    @ApiModelProperty("数字货币交易手续费比例")

    private BigDecimal coinRate;

    @ApiModelProperty("单个广告最小库存量")

    private BigDecimal perAdvertiseMinAmount;

    @ApiModelProperty("单个广告最大库存量")

    private BigDecimal perAdvertiseMaxAmount;

    @ApiModelProperty("单笔订单最小交易量")

    private BigDecimal perOrderMinAmount;

    @ApiModelProperty("单笔订单最大交易量")

    private BigDecimal perOrderMaxAmount;

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;

    @ApiModelProperty("操作时间")
    @Column(name = "create_time")
    private Date createTime;
}
