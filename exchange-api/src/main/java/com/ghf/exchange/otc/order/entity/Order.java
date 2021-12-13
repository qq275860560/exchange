package com.ghf.exchange.otc.order.entity;

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
@Table(name = "t_order")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Order {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("订单编码，唯一")
    @Column(name = "order_code")
    private String orderCode;

    @ApiModelProperty("订单来源,1:广告区固定价格下单,2:快捷区市场价格下单")
    @Column(name = "order_source")
    private int orderSource;

    @ApiModelProperty("订单买卖类型,1:买币,2:卖币，跟广告买卖类型相反")
    @Column(name = "order_buy_sell_type")
    private int orderBuySellType;

    @ApiModelProperty("订单数字货币编码")
    @Column(name = "order_coin_code")
    private String orderCoinCode;

    @ApiModelProperty("订单数字货币名称")
    @Column(name = "order_coin_name")
    private String orderCoinName;

    @ApiModelProperty("'订单数字货币单位")
    @Column(name = "order_coin_unit")
    private String orderCoinUnit;

    @ApiModelProperty("订单数字货币交易手续费比例")
    @Column(name = "order_coin_rate")
    private BigDecimal orderCoinRate;

    @ApiModelProperty("订单交易量")
    @Column(name = "order_amount")
    private BigDecimal orderAmount;
    @ApiModelProperty("订单法币成交单价，后台计算，如果是广告区选中下单，使用广告对应的单价，如果使用快捷区下单，使用系统一键匹配的单价")
    @Column(name = "order_price")
    private BigDecimal orderPrice;
    @ApiModelProperty("订单法币成交总价,后台计算")
    @Column(name = "order_total_price")
    private BigDecimal orderTotalPrice;

    @ApiModelProperty("订单顾客选择的收付款类型:1:支付宝，2：微信，3：银行卡，4：现金")
    @Column(name = "order_customer_payment_type")
    private int orderCustomerPaymentType;

    @ApiModelProperty("订单顾客的登陆用户名,用户英文名称，用户编码")
    @Column(name = "order_customer_username")
    private String orderCustomerUsername;
    @ApiModelProperty("订单顾客的用户昵称，用户中文名称，可以随时修改")
    @Column(name = "order_customer_nickname")
    private String orderCustomerNickname;
    @ApiModelProperty("订单顾客的真实姓名")
    @Column(name = "order_customer_realname")
    private String orderCustomerRealname;

    @ApiModelProperty("订单顾客支持的付款方式编码")
    @Column(name = "order_customer_payment_code")
    private String orderCustomerPaymentCode;

    @ApiModelProperty("订单顾客支持的付款条件:支付宝方式,支付宝账号")
    @Column(name = "order_customer_payment_type_alipay_account")
    private String orderCustomerPaymentTypeAlipayAccount;
    @ApiModelProperty("订单顾客支持的付款条件:支付宝方式,支付宝二维码")
    @Column(name = "order_customer_payment_type_alipay_qrcode")
    private String orderCustomerPaymentTypeAlipayQrcode;
    @ApiModelProperty("订单顾客支持的付款条件:微信方式,微信账号")
    @Column(name = "order_customer_payment_type_wechat_account")
    private String orderCustomerPaymentTypeWechatAccount;
    @ApiModelProperty("订单顾客支持的付款条件:微信方式,微信二维码")
    @Column(name = "order_customer_payment_type_wechat_qrcode")
    private String orderCustomerPaymentTypeWechatQrcode;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,银行名称")
    @Column(name = "order_customer_payment_type_bank_name")
    private String orderCustomerPaymentTypeBankName;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,支行名称")
    @Column(name = "order_customer_payment_type_bank_branch_name")
    private String orderCustomerPaymentTypeBankBranchName;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,银行卡号")
    @Column(name = "order_customer_payment_type_bank_account")
    private String orderCustomerPaymentTypeBankAccount;
    @ApiModelProperty("订单顾客支持的付款条件:银行方式,户主真实姓名")
    @Column(name = "order_customer_payment_type_bank_realname")
    private String orderCustomerPaymentTypeBankRealname;

    @ApiModelProperty("广告编码")
    @Column(name = "advertise_code")
    private String advertiseCode;

    @ApiModelProperty("广告买卖类型,1:买币,2:卖币")
    @Column(name = "advertise_buy_sell_type")
    private int advertiseBuySellType;

    @ApiModelProperty("广告数字货币编码")
    @Column(name = "advertise_coin_code")
    private String advertiseCoinCode;

    @ApiModelProperty("广告数字货币名称")
    @Column(name = "advertise_coin_name")
    private String advertiseCoinName;

    @ApiModelProperty("'广告数字货币单位")
    @Column(name = "advertise_coin_unit")
    private String advertiseCoinUnit;

    @ApiModelProperty("广告数字货币交易手续费比例")
    @Column(name = "advertise_coin_rate")
    private BigDecimal advertiseCoinRate;

    @ApiModelProperty("广告可用库存数量")
    @Column(name = "advertise_available_amount")
    private BigDecimal advertiseAvailableAmount;

    @ApiModelProperty("广告法币所在国家编码")
    @Column(name = "advertise_legal_currency_country_code")
    private String advertiseLegalCurrencyCountryCode;

    @ApiModelProperty("广告法币所在国家名称")
    @Column(name = "advertise_legal_currency_country_name")
    private String advertiseLegalCurrencyCountryName;

    @ApiModelProperty("广告法币编码")
    @Column(name = "advertise_legal_currency_code")
    private String advertiseLegalCurrencyCode;

    @ApiModelProperty("广告法币名称")
    @Column(name = "advertise_legal_currency_name")
    private String advertiseLegalCurrencyName;

    @ApiModelProperty("广告法币符号")
    @Column(name = "advertise_legal_currency_symbol")
    private String advertiseLegalCurrencySymbol;

    @ApiModelProperty("广告法币单位")
    @Column(name = "advertise_legal_currency_unit")
    private String advertiseLegalCurrencyUnit;

    @ApiModelProperty("广告价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))")
    @Column(name = "advertise_price_type")
    private int advertisePriceType;

    @ApiModelProperty("广告固定价格，价格类型固定时才有效")
    @Column(name = "advertise_fixed_price")
    private BigDecimal advertiseFixedPrice;

    @ApiModelProperty("广告溢价率，价格类型变化时才有效")
    @Column(name = "advertise_premium_rate")
    private BigDecimal advertisePremiumRate;

    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")
    @Column(name = "advertise_business_username")
    private String advertiseBusinessUsername;
    @ApiModelProperty("广告商家的用户昵称，用户中文名称，可以随时修改")
    @Column(name = "advertise_business_nickname")
    private String advertiseBusinessNickname;

    @ApiModelProperty("广告商家的真实姓名")
    @Column(name = "advertise_business_realname")
    private String advertiseBusinessRealname;

    @ApiModelProperty("广告商家支持的付款方式编码")
    @Column(name = "advertise_business_payment_code")
    private String advertiseBusinessPaymentCode;

    @ApiModelProperty("广告商家支持的付款条件:支付宝方式,支付宝账号")
    @Column(name = "advertise_business_payment_type_alipay_account")
    private String advertiseBusinessPaymentTypeAlipayAccount;
    @ApiModelProperty("广告商家支持的付款条件:支付宝方式,支付宝二维码")
    @Column(name = "advertise_business_payment_type_alipay_qrcode")
    private String advertiseBusinessPaymentTypeAlipayQrcode;
    @ApiModelProperty("广告商家支持的付款条件:微信方式,微信账号")
    @Column(name = "advertise_business_payment_type_wechat_account")
    private String advertiseBusinessPaymentTypeWechatAccount;
    @ApiModelProperty("广告商家支持的付款条件:微信方式,微信二维码")
    @Column(name = "advertise_business_payment_type_wechat_qrcode")
    private String advertiseBusinessPaymentTypeWechatQrcode;
    @ApiModelProperty("广告商家支持的付款条件:银行方式,银行名称")
    @Column(name = "advertise_business_payment_type_bank_name")
    private String advertiseBusinessPaymentTypeBankName;
    @ApiModelProperty("广告商家支持的付款条件:银行方式,支行名称")
    @Column(name = "advertise_business_payment_type_bank_branch_name")
    private String advertiseBusinessPaymentTypeBankBranchName;
    @ApiModelProperty("广告商家支持的付款条件:银行方式,银行卡号")
    @Column(name = "advertise_business_payment_type_bank_account")
    private String advertiseBusinessPaymentTypeBankAccount;
    @ApiModelProperty("广告商家支持的付款条件:银行方式,户主真实姓名")
    @Column(name = "advertise_business_payment_type_bank_realname")
    private String advertiseBusinessPaymentTypeBankRealname;

    @ApiModelProperty("订单状态,1:已下单,2:已付款,3:已放行,4:申诉中,5:已取消")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "add_time")
    private Date addTime;

    @ApiModelProperty("付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "pay_time")
    private Date payTime;

    @ApiModelProperty("放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "release_time")
    private Date releaseTime;

    @ApiModelProperty("申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "appeal_time")
    private Date appealTime;

    @ApiModelProperty("取消时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "cancel_time")
    private Date cancelTime;

    @ApiModelProperty("确认未付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "agree_un_pay_time")
    private Date agreeUnPayTime;

    @ApiModelProperty("确认未放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "agree_un_release_time")
    private Date agreeUnReleaseTime;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark;

}
