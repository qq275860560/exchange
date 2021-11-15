package com.ghf.exchange.boss.authorication.client.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ClientGrantTypeEnum {

    /**
     *
     */
    AUTHORIZATION_CODE("authorization_code", "认证码模式"),
    IMPLICIT("implicit", "隐藏模式/简化模式"),
    PASSWORD("password", "密码模式"),
    CLIENT_CREDENTIALS("client_credentials", "客户端模式"),
    REFRESH_TOKEN("refresh_token", "刷新Token模式"),

    ;

    ClientGrantTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;

}
