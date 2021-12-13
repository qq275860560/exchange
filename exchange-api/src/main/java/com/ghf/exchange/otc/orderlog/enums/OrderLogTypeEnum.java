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
    RECOVER_ORDER(5, "恢复"),

    AGREE_UN_PAY_ORDER(6, "同意未付款申诉"),
    AGREE_UN_RELEASE_ORDER(7, "同意未放行申诉"),

    ;

    OrderLogTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
