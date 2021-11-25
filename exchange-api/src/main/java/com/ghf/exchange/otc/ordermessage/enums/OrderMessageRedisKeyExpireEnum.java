package com.ghf.exchange.otc.ordermessage.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderMessageRedisKeyExpireEnum {

    /**
     *
     */

    SESSION_EXPIRE(25, "会话过期时间"),

    ;

    OrderMessageRedisKeyExpireEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
