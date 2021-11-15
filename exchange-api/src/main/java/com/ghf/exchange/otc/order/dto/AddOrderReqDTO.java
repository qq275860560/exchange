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

    @ApiModelProperty("订单币种编码，广告区选中下单不需要填写，快捷区一键匹配下单必填")
    private String orderCoinCode;
    @ApiModelProperty("订单交易量，必填")
    private BigDecimal orderAmount;

    @ApiModelProperty("订单顾客选择的收付款类型:1:支付宝，2：微信，3：银行卡")

    private int orderCustomerPaymentTermType;

/////////////////

    @ApiModelProperty("订单顾客作为卖币方时付款时间条件，从下单到点击确认付款的时间，单位分钟")

    private int orderCustomerPaymentTermTime;

    @ApiModelProperty("订单顾客支持的收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分")

    private String orderCustomerPaymentTermTypeArray;

    @ApiModelProperty("订单顾客支持的付款条件:支付宝方式,支付宝账号")

    private String orderCustomerPaymentTermTypeAlipayAccount;
    @ApiModelProperty("订单顾客支持的付款条件:支付宝方式,支付宝二维码")

    private String orderCustomerPaymentTermTypeAlipayQrcode;
    @ApiModelProperty("订单顾客支持的付款条件:微信方式,微信账号")

    private String orderCustomerPaymentTermTypeWechatAccount;
    @ApiModelProperty("订单顾客支持的付款条件:微信方式,微信二维码")

    private String orderCustomerPaymentTermTypeWechatQrcode;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,银行名称")

    private String orderCustomerPaymentTermTypeBankName;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,支行账号")

    private String orderCustomerPaymentTermTypeBankBranchName;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,银行卡号")

    private String orderCustomerPaymentTermTypeBankAccount;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,户主真实姓名")

    private String orderCustomerPaymentTermTypeBankRealname;

////////////////////

    @ApiModelProperty("广告编码，广告区选中下单必填，快捷区一键匹配下单不需要填写")
    private String advertiseCode;

    @ApiModelProperty("备注")

    private String remark;

}
