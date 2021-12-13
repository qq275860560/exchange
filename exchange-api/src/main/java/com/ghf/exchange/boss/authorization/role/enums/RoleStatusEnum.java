package com.ghf.exchange.boss.authorization.role.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum RoleStatusEnum {

    /**
     *
     */
    DISABLE(0, "状态禁用"),
    ENABLE(1, "状态启用"),

    ;

    RoleStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
