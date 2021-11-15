package com.ghf.exchange.otc.ordermessage.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderMessageStatusEnum {

    /**
     *
     */
    UN_READ(1, "未读"),
    READ(2, "已读"),

    ;

    OrderMessageStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
