package com.ghf.exchange.otc.advertisebusiness.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

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
public class AdvertiseBusinessRespDTO {

    @ApiModelProperty("id")

    private long id;

    @ApiModelProperty("广告商家编码，默认和用户名相同")

    private String advertiseBusinessCode;

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")

    private String username;

    @ApiModelProperty("密码，BCryptPasswordEncoder加密")

    private String password;

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")

    private String nickname;

    @ApiModelProperty("用户真实姓名")

    private String realname;

    @ApiModelProperty("手机")

    private String mobile;

    @ApiModelProperty("邮箱")

    private String email;

    @ApiModelProperty("国家编码")

    private String countryCode;

    @ApiModelProperty("国家名称")

    private String countryName;

    @ApiModelProperty("法币编码")

    private String legalCurrencyCode;

    @ApiModelProperty("法币名称")

    private String legalCurrencyName;

    @ApiModelProperty("法币符号")

    private String legalCurrencySymbol;

    @ApiModelProperty("法币单位")

    private String legalCurrencyUnit;

    @ApiModelProperty("保证金")

    private String deposit;

    @ApiModelProperty("广告权限,0:加V,1:不加V")

    private int advertisePermission;

    @ApiModelProperty("作为广告商家时上架中广告数量")

    private int advertisePutOnShelvesCount;
    @ApiModelProperty("发布广告后，订单顾客下单次数")

    private int orderBuySellCount;
    @ApiModelProperty("发布卖币广告后，买币订单次数,也就是卖币广告被下单的次数")

    private int orderBuyCount;
    @ApiModelProperty("发布买币广告后，卖币订单次数,也就是买币广告被下单的次数")

    private int orderSellCount;
    @ApiModelProperty("发布卖币广告后，买币订单放行次数,,也就是订单最终成功的次数")

    private int orderBuyReleaseCount;

    @ApiModelProperty("发布买币广告后，卖币订单放行次数,,也就是订单最终成功的次数")

    private int orderSellReleaseCount;
    @ApiModelProperty("买总完成率=买币订单放行次数/订单顾客买币订单下单次数")

    private BigDecimal orderBuyReleaseRate;
    @ApiModelProperty("总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数")

    private BigDecimal orderBuySellReleaseRate;
    @ApiModelProperty("买币订单累计放行时间，买币订单从下单到放行的时间")

    private long orderBuyTotalReleaseTime;
    @ApiModelProperty("卖币订单累计放行时间，卖币订单从下单到放行的时间")

    private long orderSellTotalReleaseTime;

    @ApiModelProperty("订单平均放行时间，订单从下单到放行的平均时间")

    private long orderBuySellAvgReleaseTime;
    @ApiModelProperty("订单当日申诉次数")

    private int orderTodayAppealCount;
    @ApiModelProperty("订单最后申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderLastAppealTime;


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
    private Date orderLastAddTime;
    @ApiModelProperty("订单最后放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderLastReleaseTime;

    @ApiModelProperty("状态,0:禁用,1:启用")

    private int status;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;
}
