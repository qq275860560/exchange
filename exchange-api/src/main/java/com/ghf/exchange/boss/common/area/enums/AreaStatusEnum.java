package com.ghf.exchange.boss.common.area.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AreaStatusEnum {

    /**
     *
     */
    DISABLE(0, "地区状态禁用"),
    ENABLE(1, "地区状态启用"),

    ;

    AreaStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
