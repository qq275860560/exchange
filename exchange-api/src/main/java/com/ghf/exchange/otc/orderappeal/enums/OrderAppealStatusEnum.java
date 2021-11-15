package com.ghf.exchange.otc.orderappeal.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderAppealStatusEnum {

    /**
     *
     */
    APPEAL(1, "已申诉"),
    AUDIT(2, "已审核"),

    ;

    OrderAppealStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
