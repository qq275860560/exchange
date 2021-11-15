package com.ghf.exchange.boss.common.validatecode.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ValidateCodeUsageEnum {

    /**
     *
     */
    REGISTER(1, "注册"),
    LOGIN(2, "登录"),
    FORGET_PASSWORD(3, "忘记密码"),
    SUBMIT(4, "提交表单"),

    ;

    ValidateCodeUsageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

    private static Map<Integer, ValidateCodeUsageEnum> enumMap = new HashMap<>();

    static {
        for (ValidateCodeUsageEnum value : values()) {
            enumMap.put(value.getCode(), value);
        }
    }

    public static String getMessageByCode(Integer code) {
        return enumMap.get(code).getMsg();
    }
}
