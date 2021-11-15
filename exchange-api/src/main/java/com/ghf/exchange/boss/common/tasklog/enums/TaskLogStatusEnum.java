package com.ghf.exchange.boss.common.tasklog.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum TaskLogStatusEnum {

    /**
     *
     */
    FAIL(0, "失败"),
    SUCCESS(1, "成功"),

    ;

    TaskLogStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
