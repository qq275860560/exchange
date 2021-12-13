package com.ghf.exchange.boss.common.task.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum TaskInvokeTypeEnum {

    /**
     *
     */

    REMOTE(0, "远程调用"),
    LOCAL(1, "本地调用"),

    ;

    TaskInvokeTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
