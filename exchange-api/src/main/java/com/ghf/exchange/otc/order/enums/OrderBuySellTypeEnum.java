package com.ghf.exchange.otc.order.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderBuySellTypeEnum {

    /**
     *
     */
    BUY(1, "买币"),
    SELL(2, "卖币"),

    ;

    OrderBuySellTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
