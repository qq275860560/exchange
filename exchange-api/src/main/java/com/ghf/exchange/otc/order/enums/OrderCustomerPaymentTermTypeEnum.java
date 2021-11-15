package com.ghf.exchange.otc.order.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderCustomerPaymentTermTypeEnum {

    /**
     *
     */
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信"),
    BANK(2, "银行卡"),

    ;

    OrderCustomerPaymentTermTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
