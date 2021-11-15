package com.ghf.exchange.boss.authorization.role.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum RolenameEnum {

    /**
     * 平台管理员
     */
    ROLE_ADMIN("ROLE_ADMIN", "平台管理员"),

    /**
     * '普通注册用户'
     */
    ROLE_USER("ROLE_USER", "普通注册用户"),

    /**
     * 广告商家
     */
    ROLE_ADVERTISE_BUSINESS("ROLE_ADVERTISE_BUSINESS", "广告商家"),

    /**
     * 订单顾客
     */
    ROLE_ORDER_CUSTOMER("ROLE_ORDER_CUSTOMER", "订单顾客"),

    ;

    RolenameEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;

}
