package com.ghf.exchange.boss.common.resourcefile.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ResourceFileUsageEnum {

    /**
     *
     */
    GENERAL_FILE(0, "普通文件"),
    SLIDER_VERIFICATION_CODE_BACKGROUND_PICTURE(1, "滑块验证码背景图片"),

    ;

    ResourceFileUsageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
