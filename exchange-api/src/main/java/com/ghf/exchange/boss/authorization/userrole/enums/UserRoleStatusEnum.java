package com.ghf.exchange.boss.authorization.userrole.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum UserRoleStatusEnum {

    /**
     *
     */
    DISABLE(0, "用户角色状态禁用"),
    ENABLE(1, "用户角色状态启用"),

    ;

    UserRoleStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
