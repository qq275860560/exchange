package com.ghf.exchange.otc.ordercustomer.entity;

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
@Table(name = "t_order_customer")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class OrderCustomer {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("订单顾客编码，默认和用户名相同")
    @Column(name = "order_customer_code")
    private String orderCustomerCode;

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")
    @Column(name = "username")
    private String username;

    @ApiModelProperty("密码，BCryptPasswordEncoder加密")
    @Column(name = "password")
    private String password;

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")
    @Column(name = "nickname")
    private String nickname;

    @ApiModelProperty("用户真实姓名")
    @Column(name = "realname")
    private String realname;

    @ApiModelProperty("手机")
    @Column(name = "mobile")
    private String mobile;

    @ApiModelProperty("邮箱")
    @Column(name = "email")
    private String email;

    @ApiModelProperty("国家编码")
    @Column(name = "country_code")
    private String countryCode;

    @ApiModelProperty("国家名称")
    @Column(name = "country_name")
    private String countryName;

    @ApiModelProperty("法币编码")
    @Column(name = "legal_currency_code")
    private String legalCurrencyCode;

    @ApiModelProperty("法币名称")
    @Column(name = "legal_currency_name")
    private String legalCurrencyName;

    @ApiModelProperty("法币符号")
    @Column(name = "legal_currency_symbol")
    private String legalCurrencySymbol;

    @ApiModelProperty("法币单位")
    @Column(name = "legal_currency_unit")
    private String legalCurrencyUnit;

    @ApiModelProperty("发布广告后，订单顾客下单次数")
    @Column(name = "order_buy_sell_count")
    private int orderBuySellCount;
    @ApiModelProperty("发布卖币广告后，买币订单次数,也就是卖币广告被下单的次数")
    @Column(name = "order_buy_count")
    private int orderBuyCount;
    @ApiModelProperty("发布买币广告后，卖币订单次数,也就是买币广告被下单的次数")
    @Column(name = "order_sell_count")
    private int orderSellCount;

    @ApiModelProperty("发布卖币广告后，买币订单放行次数,,也就是订单最终成功的次数")
    @Column(name = "order_buy_release_count")
    private int orderBuyReleaseCount;
    @ApiModelProperty("发布买币广告后，卖币订单放行次数,,也就是订单最终成功的次数")
    @Column(name = "order_sell_release_count")
    private int orderSellReleaseCount;

    @ApiModelProperty("买总完成率=买币订单放行次数/订单顾客买币订单下单次数")
    @Column(name = "order_buy_release_rate")
    private BigDecimal orderBuyReleaseRate;
    @ApiModelProperty("总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数")
    @Column(name = "order_buy_sell_release_rate")
    private BigDecimal orderBuySellReleaseRate;

    @ApiModelProperty("买币订单累计放行时间，买币订单从下单到放行的时间")
    @Column(name = "order_buy_total_release_time")
    private long orderBuyTotalReleaseTime;
    @ApiModelProperty("卖币订单累计放行时间，卖币订单从下单到放行的时间")
    @Column(name = "order_sell_total_release_time")
    private long orderSellTotalReleaseTime;
    @ApiModelProperty("订单平均放行时间，订单从下单到放行的平均时间")
    @Column(name = "order_buy_sell_avg_release_time")
    private long orderBuySellAvgReleaseTime;

    @ApiModelProperty("订单当日申诉次数")
    @Column(name = "order_today_appeal_count")
    private int orderTodayAppealCount;
    @ApiModelProperty("订单最后申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "order_last_appeal_time")
    private Date orderLastAppealTime;

    @ApiModelProperty("订单当日取消次数，如果大于等于系统规定的取消次数就禁止当天继续下单")
    @Column(name = "order_today_cancel_count")
    private int orderTodayCancelCount;
    @ApiModelProperty("订单最后一次取消时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "order_last_cancel_time")
    private Date orderLastCancelTime;

    @ApiModelProperty("订单当月下单总数")
    @Column(name = "order_month_buy_sell_count")
    private int orderMonthBuySellCount;
    @ApiModelProperty("订单当月放行总数")
    @Column(name = "order_month_buy_sell_release_count")
    private int orderMonthBuySellReleaseCount;
    @ApiModelProperty("订单当月放行比例")
    @Column(name = "order_month_buy_sell_release_rate")
    private BigDecimal orderMonthBuySellReleaseRate;
    @ApiModelProperty("卖币订单总额")
    @Column(name = "order_sell_total_price")
    private BigDecimal orderSellTotalPrice;
    @ApiModelProperty("买币订单总额")
    @Column(name = "order_buy_total_price")
    private BigDecimal orderBuyTotalPrice;
    @ApiModelProperty("订单最后下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "order_last_add_time")
    private Date orderLastAddTime;
    @ApiModelProperty("订单最后放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "order_last_release_time")
    private Date orderLastReleaseTime;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
