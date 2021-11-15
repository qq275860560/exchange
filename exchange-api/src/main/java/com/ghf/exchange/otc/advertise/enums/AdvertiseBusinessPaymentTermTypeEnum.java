package com.ghf.exchange.otc.advertise.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertiseBusinessPaymentTermTypeEnum {

    /**
     *
     */
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信"),
    BANK(2, "银行卡"),

    ;

    AdvertiseBusinessPaymentTermTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
