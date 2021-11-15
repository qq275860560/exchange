package com.ghf.exchange.boss.common.task.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum TaskStatusEnum {

    /**
     *
     */

    PAUSE(0, "任务状态暂停中"),
    RUNNING(1, "任务状态运行中"),
    COMPLETE(2, "任务状态已完成"),
    DELETE(3, "任务状态已删除"),
    ;

    TaskStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
