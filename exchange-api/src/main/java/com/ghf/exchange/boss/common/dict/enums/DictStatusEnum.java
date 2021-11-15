package com.ghf.exchange.boss.common.dict.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum DictStatusEnum {

    /**
     *
     */
    DISABLE(0, "数据字典状态禁用"),
    ENABLE(1, "数据字典状态启用"),

    ;

    DictStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
