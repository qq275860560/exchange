package com.ghf.exchange.otc.advertise.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertiseStatusEnum {

    /**
     *
     */
    PUT_ON_SHELVES(1, "上架"),
    PUT_OFF_SHELVES(2, "下架"),
    DELETED(3, "删除"),

    ;

    AdvertiseStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
