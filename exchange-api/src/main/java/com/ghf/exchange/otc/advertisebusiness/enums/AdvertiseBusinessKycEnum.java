package com.ghf.exchange.otc.advertisebusiness.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertiseBusinessKycEnum {

    /**
     *
     */
    KYC_1(1, "kyc1"),
    KYC_2(2, "kyc1"),
    KYC_3(3, "kyc1"),

    ;

    AdvertiseBusinessKycEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
