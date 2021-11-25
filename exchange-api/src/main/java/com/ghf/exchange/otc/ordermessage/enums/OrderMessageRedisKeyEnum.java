package com.ghf.exchange.otc.ordermessage.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderMessageRedisKeyEnum {

    /**
     *
     */
    ORDER_MESSAGE("OrderMessage", "会话前缀"),
    ORDER_MESSAGE_QUEUE("OrderMessageQueue", "queue"),

    ;

    OrderMessageRedisKeyEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;

}
