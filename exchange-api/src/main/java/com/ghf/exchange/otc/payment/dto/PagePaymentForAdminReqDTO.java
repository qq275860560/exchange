package com.ghf.exchange.otc.payment.dto;

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
public class PagePaymentForAdminReqDTO extends PageReqDTO {

    @ApiModelProperty("收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分")

    private int paymentType;

    @ApiModelProperty("收付款的登陆用户名,用户英文名称，用户编码")

    private String username;
}
