package com.ghf.exchange.boss.common.validatecode.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ValidateCodeTypeEnum {

    /**
     *
     */
    PICTURE(1, "图片"),
    MESSAGE(2, "短信"),
    EMAIL(3, "邮箱"),
    SLIDER(4, "滑块"),

    ;

    ValidateCodeTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
