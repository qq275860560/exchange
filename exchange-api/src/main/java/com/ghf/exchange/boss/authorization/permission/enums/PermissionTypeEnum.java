package com.ghf.exchange.boss.authorization.permission.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum PermissionTypeEnum {

    /**
     *
     */
    平台(1, "平台"),
    系统(2, "系统"),
    子系统(3, "子系统"),
    模块(4, "模块"),
    子模块(5, "子模块"),
    一级菜单(6, "一级菜单"),
    二级菜单(7, "二级菜单"),
    三级菜单(8, "三级菜单"),
    页面(9, "页面(组件)"),
    子页面(10, "子页面(子组件)"),
    按钮(11, "按钮"),
    ;

    PermissionTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
