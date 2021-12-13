package com.ghf.exchange.otc.advertise.dto;

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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

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
public class AdvertiseRespDTO {

    @ApiModelProperty("id")
    private long id;

    @ApiModelProperty("广告编码，唯一")

    private String advertiseCode;

    @ApiModelProperty("买卖类型,1:买币,2:卖币")

    private int advertiseBuySellType;

    @ApiModelProperty("数字货币编码")

    private String advertiseCoinCode;

    @ApiModelProperty("数字货币名称")

    private String advertiseCoinName;

    @ApiModelProperty("数字货币单位")

    private String advertiseCoinUnit;

    @ApiModelProperty("数字货币交易手续费比例")

    private BigDecimal advertiseCoinRate;

    @ApiModelProperty("总库存数量")
    private BigDecimal advertiseTotalAmount;

    @ApiModelProperty("广告可用库存数量")
    private BigDecimal advertiseAvailableAmount;

    @ApiModelProperty("冻结库存数量")
    private BigDecimal advertiseFrozenAmount;

    @ApiModelProperty("法币所在国家编码")

    private String advertiseLegalCurrencyCountryCode;

    @ApiModelProperty("法币所在国家编码")

    private String advertiseLegalCurrencyCountryName;

    @ApiModelProperty("法币编码")

    private String advertiseLegalCurrencyCode;

    @ApiModelProperty("法币名称")

    private String advertiseLegalCurrencyName;

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

    @ApiModelProperty("广告商家支持的付款方式列表")
    private Set<Integer> advertiseBusinessPaymentTypeSet = Collections.emptySet();

    @ApiModelProperty("广告商家支持的付款方式编码，逗号区分")
    private String advertiseBusinessPaymentCodes;

    @ApiModelProperty("广告商家支持的收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分")
    private String advertiseBusinessPaymentTypes;

    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")

    private String advertiseBusinessUsername;
    @ApiModelProperty("广告商家的用户昵称，用户中文名称，可以随时修改")

    private String advertiseBusinessNickname;

    @ApiModelProperty("广告商家的真实姓名")

    private String advertiseBusinessRealname;

    @ApiModelProperty("状态,1:上架,2:下架,3:删除")

    private int status;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    @ApiModelProperty("备注")

    private String remark;

}
