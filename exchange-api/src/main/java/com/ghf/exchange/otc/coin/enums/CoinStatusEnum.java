package com.ghf.exchange.otc.coin.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum CoinStatusEnum {

    /**
     *
     */
    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),

    ;

    CoinStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
