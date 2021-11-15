package com.ghf.exchange.boss.authorication.client.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ClientScopeEnum {

    /**
     *
     */
    APP("APP", "手机APP"),
    BROWSER("BROWSER", "浏览器"),
    SERVER("SERVER", "内部后端服务器"),

    ;

    ClientScopeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;

}
