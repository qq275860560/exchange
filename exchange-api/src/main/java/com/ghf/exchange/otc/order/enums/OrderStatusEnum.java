package com.ghf.exchange.otc.order.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderStatusEnum {

    /**
     *
     */
    ADD(1, "已下单"),
    PAY(2, "已付款"),
    RELEASE(3, "已放行"),
    CANCEL(4, "已取消"),

    ;

    OrderStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
