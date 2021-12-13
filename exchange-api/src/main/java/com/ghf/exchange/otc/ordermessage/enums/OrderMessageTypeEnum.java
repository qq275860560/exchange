package com.ghf.exchange.otc.ordermessage.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum OrderMessageTypeEnum {

    /**
     *
     */
    TEXT(1, "文本"),
    PICTURE(2, "图片"),

    ;

    OrderMessageTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
