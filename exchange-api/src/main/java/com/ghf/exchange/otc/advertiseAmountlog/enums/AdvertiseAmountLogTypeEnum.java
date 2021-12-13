package com.ghf.exchange.otc.advertiseamountlog.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertiseAmountLogTypeEnum {

    /**
     *
     */
    FREE_ADVERTISE_AMOUNT(1, "冻结广告库存数量"),
    UN_FREE_ADVERTISE_AMOUNT(2, "解冻广告库存数量"),

    DEC_ADVERTISE_FROZEN_AMOUNT(3, "扣减广告冻结库存数量"),

    ;

    AdvertiseAmountLogTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
