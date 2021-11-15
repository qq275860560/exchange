package com.ghf.exchange.otc.order.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderSourceEnum {

    /**
     *
     */
    ADVERTISE_SELECT(1, "广告区选中下单"),
    SHORTCUT_ONE_KEY_MATCH(2, "快捷区一键匹配下单"),

    ;

    OrderSourceEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
