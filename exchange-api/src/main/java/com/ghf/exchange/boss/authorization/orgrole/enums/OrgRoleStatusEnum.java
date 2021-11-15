package com.ghf.exchange.boss.authorization.orgrole.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrgRoleStatusEnum {

    /**
     *
     */
    DISABLE(0, "组织角色状态禁用"),
    ENABLE(1, "组织角色状态启用"),

    ;

    OrgRoleStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
