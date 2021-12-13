package com.ghf.exchange.otc.payment.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum PaymentTypeEnum {

    /**
     *
     */
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信"),
    BANK(3, "银行"),

    ;

    PaymentTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
