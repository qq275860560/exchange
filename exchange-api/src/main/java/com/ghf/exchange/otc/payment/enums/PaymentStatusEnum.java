package com.ghf.exchange.otc.payment.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum PaymentStatusEnum {

    /**
     *
     */
    DISABLE(0, "状态禁用"),
    ENABLE(1, "状态启用"),
    ;

    PaymentStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
