package com.ghf.exchange.boss.authorication.user.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum UsersStatusEnum {

    /**
     *
     */
    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),

    ;

    UsersStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
