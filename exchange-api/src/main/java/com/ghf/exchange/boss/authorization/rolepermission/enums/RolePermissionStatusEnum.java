package com.ghf.exchange.boss.authorization.rolepermission.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum RolePermissionStatusEnum {

    /**
     *
     */
    DISABLE(0, "角色权限状态禁用"),
    ENABLE(1, "角色权限状态启用"),

    ;

    RolePermissionStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
