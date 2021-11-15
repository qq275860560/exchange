package com.ghf.exchange.boss.common.validatecode.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ValidateCodeStatusEnum {

    /**
     *
     */
    UN(0, "未验证"),
    SUCCESS(1, "已验证通过"),
    FAIL(2, "已验证失败"),

    ;

    ValidateCodeStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
