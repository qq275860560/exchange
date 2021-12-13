package com.ghf.exchange.otc.advertise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

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
public class GetMatchAdvertiseReqDTO {

    @ApiModelProperty("买卖类型,1:买币,2:卖币")
    private int advertiseBuySellType;

    @ApiModelProperty("数字货币编码")
    private String advertiseCoinCode;

    @ApiModelProperty("库存数量")
    private BigDecimal advertiseAmount;

    @ApiModelProperty("价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))")
    private int advertisePriceType;

    @ApiModelProperty("固定价格，价格类型固定时才有效")
    private BigDecimal advertiseFixedPrice;

    @ApiModelProperty("溢价率，价格类型变化时才有效")
    private BigDecimal advertisePremiumRate;

    @ApiModelProperty("广告商家支持的收付款类型:1:支付宝，2：微信，3：银行卡")
    private int advertiseBusinessPaymentType;

    @ApiModelProperty("状态,1:上架,2:下架,3:删除")
    private int status;

}
