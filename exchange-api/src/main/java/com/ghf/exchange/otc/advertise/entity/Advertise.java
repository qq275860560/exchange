package com.ghf.exchange.otc.advertise.entity;

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
@Table(name = "t_advertise")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Advertise {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("广告编码，唯一")
    @Column(name = "advertise_code")
    private String advertiseCode;

    @ApiModelProperty("买卖类型,1:买币,2:卖币")
    @Column(name = "advertise_buy_sell_type")
    private int advertiseBuySellType;

    @ApiModelProperty("币种编码")
    @Column(name = "advertise_coin_code")
    private String advertiseCoinCode;

    @ApiModelProperty("币种名称")
    @Column(name = "advertise_coin_name")
    private String advertiseCoinName;

    @ApiModelProperty("币种单位")
    @Column(name = "advertise_coin_unit")
    private String advertiseCoinUnit;

    @ApiModelProperty("币种交易手续费比例")
    @Column(name = "advertise_coin_rate")
    private BigDecimal advertiseCoinRate;

    @ApiModelProperty("库存量")
    @Column(name = "advertise_amount")
    private BigDecimal advertiseAmount;

    @ApiModelProperty("冻结库存量")
    @Column(name = "advertise_frozen_amount")
    private BigDecimal advertiseFrozenAmount;

    @ApiModelProperty("单笔最大交易量")
    @Column(name = "advertise_per_max_amount")
    private BigDecimal advertisePerMaxAmount;

    @ApiModelProperty("单笔最小交易量")
    @Column(name = "advertise_per_min_amount")
    private BigDecimal advertisePerMinAmount;

    @ApiModelProperty("法币所在国家编码")
    @Column(name = "advertise_legal_currency_country_code")
    private String advertiseLegalCurrencyCountryCode;

    @ApiModelProperty("法币符号")
    @Column(name = "advertise_legal_currency_symbol")
    private String advertiseLegalCurrencySymbol;

    @ApiModelProperty("法币单位")
    @Column(name = "advertise_legal_currency_unit")
    private String advertiseLegalCurrencyUnit;

    @ApiModelProperty("价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))")
    @Column(name = "advertise_price_type")
    private int advertisePriceType;

    @ApiModelProperty("固定价格，价格类型固定时才有效")
    @Column(name = "advertise_fixed_price")
    private BigDecimal advertiseFixedPrice;

    @ApiModelProperty("溢价率，价格类型变化时才有效")
    @Column(name = "advertise_premium_rate")
    private BigDecimal advertisePremiumRate;

    @ApiModelProperty("自动回复内容")
    @Column(name = "advertise_auto_reply_content")
    private String advertiseAutoReplyContent;

    @ApiModelProperty("付款时间条件，从下单到点击确认付款的时间，单位分钟")
    @Column(name = "advertise_business_payment_term_time")
    private int advertiseBusinessPaymentTermTime;

    @ApiModelProperty("广告商家支持的收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分")
    @Column(name = "advertise_business_payment_term_type_array")
    private String advertiseBusinessPaymentTermTypeArray;

    @ApiModelProperty("付款条件:支付宝方式,支付宝账号")
    @Column(name = "advertise_business_payment_term_type_alipay_account")
    private String advertiseBusinessPaymentTermTypeAlipayAccount;
    @ApiModelProperty("付款条件:支付宝方式,支付宝二维码")
    @Column(name = "advertise_business_payment_term_type_alipay_qrcode")
    private String advertiseBusinessPaymentTermTypeAlipayQrcode;
    @ApiModelProperty("付款条件:微信方式,微信账号")
    @Column(name = "advertise_business_payment_term_type_wechat_account")
    private String advertiseBusinessPaymentTermTypeWechatAccount;
    @ApiModelProperty("付款条件:微信方式,微信二维码")
    @Column(name = "advertise_business_payment_term_type_wechat_qrcode")
    private String advertiseBusinessPaymentTermTypeWechatQrcode;
    @ApiModelProperty("付款条件:银行方式,银行名称")
    @Column(name = "advertise_business_payment_term_type_bank_name")
    private String advertiseBusinessPaymentTermTypeBankName;
    @ApiModelProperty("付款条件:银行方式,支行账号")
    @Column(name = "advertise_business_payment_term_type_bank_branch_name")
    private String advertiseBusinessPaymentTermTypeBankBranchName;
    @ApiModelProperty("付款条件:银行方式,银行卡号")
    @Column(name = "advertise_business_payment_term_type_bank_account")
    private String advertiseBusinessPaymentTermTypeBankAccount;
    @ApiModelProperty("付款条件:银行方式,户主真实姓名")
    @Column(name = "advertise_business_payment_term_type_bank_realname")
    private String advertiseBusinessPaymentTermTypeBankRealname;

    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")
    @Column(name = "advertise_business_username")
    private String advertiseBusinessUsername;
    @ApiModelProperty("广告商家的用户昵称，用户中文名称，可以随时修改")
    @Column(name = "advertise_business_nickname")
    private String advertiseBusinessNickname;

    @ApiModelProperty("广告商家的真实姓名")
    @Column(name = "advertise_business_realname")
    private String advertiseBusinessRealname;

    @ApiModelProperty("状态,1:上架,2:下架,3:删除")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark;

}
