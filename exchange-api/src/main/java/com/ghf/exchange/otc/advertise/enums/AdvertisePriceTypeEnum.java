package com.ghf.exchange.otc.advertise.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertisePriceTypeEnum {

    /**
     *
     */
    FIXED(1, "固定价格"),
    PREMIUM(2, "变化价格"),

    ;

    AdvertisePriceTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
