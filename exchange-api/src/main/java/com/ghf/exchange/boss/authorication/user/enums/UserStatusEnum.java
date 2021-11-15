package com.ghf.exchange.boss.authorication.user.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum UserStatusEnum {

    /**
     *
     */
    DISABLE(0, "用户状态禁用"),
    ENABLE(1, "用户状态启用"),

    ;

    UserStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
