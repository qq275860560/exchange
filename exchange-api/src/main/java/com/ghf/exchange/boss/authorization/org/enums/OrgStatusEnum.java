package com.ghf.exchange.boss.authorization.org.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrgStatusEnum {

    /**
     *
     */
    DISABLE(0, "组织状态禁用"),
    ENABLE(1, "组织状态启用"),

    ;

    OrgStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
