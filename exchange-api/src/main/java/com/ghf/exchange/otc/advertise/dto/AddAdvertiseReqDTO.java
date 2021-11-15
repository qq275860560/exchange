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
public class AddAdvertiseReqDTO {

    @ApiModelProperty("广告编码，唯一")
    private String advertiseCode;

    @ApiModelProperty("买卖类型,1:买币,2:卖币")
    private int advertiseBuySellType;

    @ApiModelProperty("币种编码")
    private String advertiseCoinCode;

    @ApiModelProperty("库存量")
    private BigDecimal advertiseAmount;

    @ApiModelProperty("单笔最大交易量")
    private BigDecimal advertisePerMaxAmount;

    @ApiModelProperty("单笔最小交易量")
    private BigDecimal advertisePerMinAmount;

    @ApiModelProperty("法币所在国家编码")
    private String advertiseLegalCurrencyCountryCode;

    @ApiModelProperty("法币符号")
    private String advertiseLegalCurrencySymbol;

    @ApiModelProperty("法币单位")
    private String advertiseLegalCurrencyUnit;

    @ApiModelProperty("价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))")
    private int advertisePriceType;

    @ApiModelProperty("固定价格，价格类型固定时才有效")
    private BigDecimal advertiseFixedPrice;

    @ApiModelProperty("溢价率，价格类型变化时才有效")
    private BigDecimal advertisePremiumRate;

    @ApiModelProperty("自动回复内容")
    private String advertiseAutoReplyContent;

    @ApiModelProperty("付款时间条件，从下单到点击确认付款的时间，单位分钟")
    private int advertiseBusinessPaymentTermTime;

    @ApiModelProperty("广告商家支持的收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分")
    private String advertiseBusinessPaymentTermTypeArray;

    @ApiModelProperty("付款条件:支付宝方式,支付宝账号")
    private String advertiseBusinessPaymentTermTypeAlipayAccount;
    @ApiModelProperty("付款条件:支付宝方式,支付宝二维码")
    private String advertiseBusinessPaymentTermTypeAlipayQrcode;
    @ApiModelProperty("付款条件:微信方式,微信账号")
    private String advertiseBusinessPaymentTermTypeWechatAccount;
    @ApiModelProperty("付款条件:微信方式,微信二维码")
    private String advertiseBusinessPaymentTermTypeWechatQrcode;
    @ApiModelProperty("付款条件:银行方式,银行名称")
    private String advertiseBusinessPaymentTermTypeBankName;
    @ApiModelProperty("付款条件:银行方式,支行账号")
    private String advertiseBusinessPaymentTermTypeBankBranchName;
    @ApiModelProperty("付款条件:银行方式,银行卡号")
    private String advertiseBusinessPaymentTermTypeBankAccount;
    @ApiModelProperty("付款条件:银行方式,户主真实姓名")
    private String advertiseBusinessPaymentTermTypeBankRealname;

    @ApiModelProperty("备注")
    private String remark;

}