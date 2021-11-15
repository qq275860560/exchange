package com.ghf.exchange.boss.authorication.client.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ClientStatusEnum {

    /**
     *
     */
    DISABLE(0, "客户端状态禁用"),
    ENABLE(1, "客户端状态启用"),

    ;

    ClientStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
