package com.ghf.exchange.boss.common.task.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum TaskTypeEnum {

    /**
     *
     */

    SIMPLY(0, "简单任务(直接设置重复执行次数和执行间隔)"),
    CRON(1, "CRON任务(根据cron表达式确定执行间隔)"),

    ;

    TaskTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
