package com.ghf.exchange.otc.advertisebusiness.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum AdvertiseBusinessAdevrtisePermissionEnum {

    /**
     *
     */
    ADD_V(0, "加V"),
    NOT_ADD_V(1, "不加V"),

    ;

    AdvertiseBusinessAdevrtisePermissionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
