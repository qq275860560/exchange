package com.ghf.exchange.boss.common.resourcefile.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ResourceFileStatusEnum {

    /**
     *
     */
    NOT_FINISH(0, "未上传完毕"),
    FINISH(1, "已经上传完毕"),

    ;

    ResourceFileStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
