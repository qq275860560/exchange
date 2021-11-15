package com.ghf.exchange.otc.orderlog.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderLogTypeEnum {

    /**
     *
     */
    ADD_ORDER(1, "下单"),
    PAY_ORDER(2, "付款"),

    RELEASE_ORDER(3, "放行"),
    CANCEL_ORDER(4, "取消"),
    RECOVER_ORDER(4, "恢复"),

    ;

    OrderLogTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
