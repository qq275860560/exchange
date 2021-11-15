package com.ghf.exchange.otc.advertiselog.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertiseLogTypeEnum {

    /**
     *
     */
    ADD_ADVERTISE(1, "发布广告"),
    PUT_ON_SHELVES(2, "上架广告"),

    PUT_OFF_SHELVES(3, "下架广告"),
    DELETE_ADVERTISE(4, "删除广告"),

    ;

    AdvertiseLogTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
