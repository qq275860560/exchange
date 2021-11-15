package com.ghf.exchange.boss.common.validatecode.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ValidateCodeCauseEnum {

    /**
     *
     */
    NOT_EXISTS(1, "验证码不存在"),
    ERROR(1, "不准确"),
    OVER_TIME(2, "超时"),

    ;

    ValidateCodeCauseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
