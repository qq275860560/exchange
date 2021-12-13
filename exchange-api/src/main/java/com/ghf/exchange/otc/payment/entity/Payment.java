package com.ghf.exchange.otc.payment.entity;

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
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_payment")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Payment {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("收款方式编码， 唯一")
    @Column(name = "payment_code")
    private String paymentCode;

    @ApiModelProperty("收付款的登陆用户名,用户英文名称，用户编码")
    @Column(name = "username")
    private String username;

    @ApiModelProperty("收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分")
    @Column(name = "payment_type")
    private int paymentType;

    @ApiModelProperty("支付宝方式,支付宝账号")
    @Column(name = "payment_type_alipay_account")
    private String paymentTypeAlipayAccount;

    @ApiModelProperty("支付宝方式,支付宝二维码")
    @Column(name = "payment_type_alipay_qrcode")
    private String paymentTypeAlipayQrcode;

    @ApiModelProperty("微信方式,微信账号")
    @Column(name = "payment_type_wechat_account")
    private String paymentTypeWechatAccount;

    @ApiModelProperty("微信方式,微信二维码")
    @Column(name = "payment_type_wechat_qrcode")
    private String paymentTypeWechatQrcode;

    @ApiModelProperty("银行方式,银行名称")
    @Column(name = "payment_type_bank_name")
    private String paymentTypeBankName;
    @ApiModelProperty("银行方式,支行名称")
    @Column(name = "payment_type_bank_branch_name")
    private String paymentTypeBankBranchName;
    @ApiModelProperty("银行方式,银行卡号")
    @Column(name = "payment_type_bank_account")
    private String paymentTypeBankAccount;
    @ApiModelProperty("银行方式,户主真实姓名")
    @Column(name = "payment_type_bank_realname")
    private String paymentTypeBankRealname;

    @ApiModelProperty("状态,1:启用，2:停用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark = "";

}
