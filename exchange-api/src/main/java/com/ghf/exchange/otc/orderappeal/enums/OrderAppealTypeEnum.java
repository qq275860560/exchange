package com.ghf.exchange.otc.orderappeal.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderAppealTypeEnum {

    /**
     *
     */
    UN_PAY(1, "对方未付款"),
    UN_RELEASE(2, "对方未放行"),
    OTHER(3, "其他"),

    ;

    OrderAppealTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
