package com.ghf.exchange.boss.authorization.userorg.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum UserOrgStatusEnum {

    /**
     *
     */
    DISABLE(0, "用户组织状态禁用"),
    ENABLE(1, "用户组织状态启用"),

    ;

    UserOrgStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
