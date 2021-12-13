package com.ghf.exchange.otc.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghf.exchange.dto.PageReqDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
public class PageOrderForAdminReqDTO extends PageReqDTO {

    @ApiModelProperty("用户名")

    private String username;

    @ApiModelProperty("订单编码，唯一")
    private String orderCode;

    @ApiModelProperty("广告编码")

    private String advertiseCode;

    @ApiModelProperty("订单顾客选择的收付款类型:1:支付宝，2：微信，3：银行卡")

    private int orderCustomerPaymentType;

    @ApiModelProperty("订单来源,1:广告区固定价格下单,2:快捷区市场价格下单")

    private int orderSource;

    @ApiModelProperty("订单买卖类型,1:买币,2:卖币，跟广告买卖类型相反")

    private int orderBuySellType;

    @ApiModelProperty("广告币种编码")

    private String advertiseCoinCode;

    @ApiModelProperty("订单状态,1:已下单,2:已付款,3:已放行,4:申诉中,5:已取消")

    private int status;

}
