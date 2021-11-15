package com.ghf.exchange.boss.authorization.permission.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum PermissionStatusEnum {

    /**
     *
     */
    DISABLE(0, "权限状态禁用"),
    ENABLE(1, "权限状态启用"),

    ;

    PermissionStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
