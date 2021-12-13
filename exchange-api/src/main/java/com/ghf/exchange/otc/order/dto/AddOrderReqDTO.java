package com.ghf.exchange.otc.order.dto;

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
public class AddOrderReqDTO {

    @ApiModelProperty("订单编码，唯一")
    private String orderCode;

    @ApiModelProperty("订单来源,1:广告区选中下单,2:快捷区一键匹配下单，必填")
    private int orderSource;
    @ApiModelProperty("订单买卖类型,1:买币,2:卖币，跟广告买卖类型相反，广告区选中下单不需要填写，快捷区一键匹配下单必填")
    private int orderBuySellType;

    @ApiModelProperty("订单数字货币编码，广告区选中下单不需要填写，快捷区一键匹配下单必填")
    private String orderCoinCode;
    @ApiModelProperty("订单交易量，必填")
    private BigDecimal orderAmount;

    @ApiModelProperty("订单顾客选择的收付款类型:1:支付宝，2：微信，3：银行卡，4：现金")

    private int orderCustomerPaymentType;

////////////////////

    @ApiModelProperty("广告编码，广告区选中下单必填，快捷区一键匹配下单不需要填写")
    private String advertiseCode;

    @ApiModelProperty("备注")

    private String remark;

}
