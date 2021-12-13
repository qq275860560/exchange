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
    APPEAL(1, "已申诉,等待处理"),
    CANCEL(2, "取消申诉"),
    FAIL(3, "审核失败"),
    SUCCESS(4, "审核成功"),

    ;

    OrderAppealStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
