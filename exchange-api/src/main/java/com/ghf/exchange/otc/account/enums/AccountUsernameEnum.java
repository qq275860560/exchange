package com.ghf.exchange.otc.account.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AccountUsernameEnum {

    /**
     *
     */
    ADMIN_USER_NAME("admin", "平台管理员用户名"),

    ;

    AccountUsernameEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;

}
