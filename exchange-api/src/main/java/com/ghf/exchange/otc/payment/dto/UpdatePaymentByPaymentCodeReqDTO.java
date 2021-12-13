package com.ghf.exchange.otc.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class UpdatePaymentByPaymentCodeReqDTO {

    @ApiModelProperty("收款方式编码， 唯一")

    private String paymentCode;

    @ApiModelProperty("收付款类型:1:支付宝，2：微信，3：银行卡，4：现金,逗号区分")

    private int paymentType;

    @ApiModelProperty("支付宝方式,支付宝账号")

    private String paymentTypeAlipayAccount;

    @ApiModelProperty("支付宝方式,支付宝二维码")

    private String paymentTypeAlipayQrcode;

    @ApiModelProperty("微信方式,微信账号")

    private String paymentTypeWechatAccount;

    @ApiModelProperty("微信方式,微信二维码")

    private String paymentTypeWechatQrcode;

    @ApiModelProperty("银行方式,银行名称")

    private String paymentTypeBankName;
    @ApiModelProperty("银行方式,支行名称")

    private String paymentTypeBankBranchName;
    @ApiModelProperty("银行方式,银行卡号")

    private String paymentTypeBankAccount;
    @ApiModelProperty("银行方式,户主真实姓名")

    private String paymentTypeBankRealname;

    @ApiModelProperty("备注")

    private String remark = "";

}
