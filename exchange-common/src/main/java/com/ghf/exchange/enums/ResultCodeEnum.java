package com.ghf.exchange.enums;

import lombok.Getter;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
public enum ResultCodeEnum {

    /**
     * 请求正常
     */
    OK(200, "请求正常"),
    BAD_REQUEST(400, "请求异常"),
    UNAUTHORIZED(401, "认证失败"),
    FORBIDDEN(403, "授权失败"),

    USER_EXISTS(60001, "用户已经存在"),
    USER_NOT_EXISTS(60002, "用户不存在"),
    USER_OLD_PASSWORD_ERROR(60003, "用户原密码错误"),
    PASSWORD_ERROR(60004, "用户密码错误"),
    USER_STATUS_ENABLE(60005, "用户已经禁用"),
    USER_STATUS_DISABLE(60006, "用户已经禁用"),
    USER_LOGIN_NOT_EXISTS(60007, "获取当前登录用户失败"),
    USER_EMAIL_EXISTS(60008, "邮箱已存在"),
    EMAIL_CODE_ERROR(60009, "验证码错误"),
    EMAIL_CODE_NOT_NULL(60010, "邮箱不能为空"),
    MOBILE_CODE_NOT_NULL(60011, "手机号码不能为空"),
    INSIDE_ERROR(60012, "内部服务异常，请稍后重试"),
    USER_EMAIL_NOT_EXISTS(60013, "邮箱不存在"),
    USER_MOBILE_EXISTS(60014, "手机已存在"),
    USER_REPEAT_PASSWORD_ERROR(60015, "您修改的新密码与旧密码一致，请重新输入"),

    CLIENT_OLD_PASSWORD_ERROR(60101, "客户端原密码错误"),
    CLIENT_EXISTS(60102, "客户端已经存在"),
    CLIENT_NOT_EXISTS(60103, "客户端不存在"),
    CLIENT_STATUS_DISABLE(60104, "客户端已经禁用"),
    CLIENT_STATUS_ENABLE(60105, "客户端已经启用"),

    ROLE_EXISTS(61101, "角色已经存在"),
    ROLE_NOT_EXISTS(61102, "角色不存在"),

    PERMISSION_EXISTS(61201, "权限已经存在"),
    PERMISSION_NOT_EXISTS(61202, "权限不存在"),
    PERMISSION_NOT_ALLOW(61203, "权限不足"),

    ORG_EXISTS(61301, "组织已经存在"),
    ORG_NOT_EXISTS(61302, "组织不存在"),

    USER_ROLE_EXISTS(61401, "用户角色关联已经存在"),
    USER_ROLE_NOT_EXISTS(61402, "用户角色关联不存在"),

    ROLE_PERMISSION_EXISTS(61501, "角色权限关联已经存在"),
    ROLE_PERMISSION_NOT_EXISTS(61502, "角色权限关联不存在"),

    USER_ORG_EXISTS(61601, "用户组织关联已经存在"),
    USER_ORG_NOT_EXISTS(61602, "用户组织关联不存在"),

    ORG_ROLE_EXISTS(61701, "组织角色关联已经存在"),
    ORG_ROLE_NOT_EXISTS(61702, "组织角色关联不存在"),

    RESOURCE_FILE_EXISTS(61801, "资源文件已经存在"),
    RESOURCE_FILE_NOT_EXISTS(61802, "资源文件不存在"),

    VALIDATE_CODE_TYPE_NOT_EXISTS(61901, "验证方式不存在"),
    VALIDATE_CODE_USEAGE_NOT_EXISTS(61902, "验证用途不存在"),

    DICT_EXISTS(61701, "数据字典已经存在"),
    DICT_NOT_EXISTS(61702, "数据字典不存在"),

    AREA_EXISTS(61801, "数据字典已经存在"),
    AREA_NOT_EXISTS(61802, "数据字典不存在"),

    TASK_EXISTS(61901, "任务已经存在"),
    TASK_NOT_EXISTS(61902, "任务不存在"),
    TASK_STATUS_PAUSE(61903, "任务暂停中"),
    TASK_STATUS_RUNNGIN(61904, "任务运行中"),
    TASK_STATUS_COMPLETE(61905, "任务已经完成"),
    TASK_STATUS_DELETE(61905, "任务已经删除"),
    TASK_TYPE_DELETE(61906, "任务类型不存在"),

    TASK_LOG_EXISTS(62001, "任务日志已经存在"),
    TASK_LOG_NOT_EXISTS(62002, "任务日志不存在"),
    /**
     * 广告模块
     */
    ADVERTISE_EXISTS(70001, "广告已经存在"),
    ADVERTISE_NOT_EXISTS(70002, "广告不存在"),

    ADVERTISE_BUY_SELL_TYPE_NOT_EXISTS(70003, "广告买卖类型不存在"),
    ADVERTISE_PRICE_TYPE_NOT_EXISTS(70004, "广告价格类型不存在"),
    ADVERTISE_STATUSE_NOT_EXISTS(70005, "广告状态不存在"),
    ADVERTISE_PAYMENT_TERM_TIME_ERROR(70005, "广告付款期限错误"),
    ADVERTISE_AVAILABLE_AMOUNT_ERROR(70006, "广告库存错误"),
    ADVERTISE_PAYMENT_TERM_TYPE_ARRAY_NOT_EMPTY(70007, "广告收付款类型不能为空"),
    ADVERTISE_STATUS_IS_NOT_PUT_OFF_SHELVES(70008, "广告非下架状态"),
    ADVERTISE_STATUS_IS_NOT_PUT_ON_SHELVES(70009, "广告非上架状态"),
    ADVERTISE_AMOUNT_ERROR(70010, "广告库存数量错误"),
    ADVERTISE_FROZEN_AMOUNT_IS_NOT_ZERO(70011, "广告冻结数量非零"),
    ADVERTISE_AMOUNT_NOT_ENOUGH(70012, "广告库存数量不足"),
    ADVERTISE_FROZEN_AMOUNT_NOT_ENOUGH(70013, "广告冻结数量不足"),
    /**
     * 订单模块
     */
    ORDER_EXISTS(80001, "订单已经存在"),
    ORDER_NOT_EXISTS(80002, "订单不存在"),
    ORDER_SOURCE_NOT_EXISTS(80003, "订单来源不存在"),
    ORDER_BUY_SELL_TYPE_NOT_EXISTS(80004, "订单买卖类型不存在"),
    ORDER_AMOUNT_LESS_THEN_PER_MIN_AMOUNT_ERROR(80005, "订单交易数量小于单笔订单最小交易量错误"),
    ORDER_AMOUNT_GREATER_THEN_PER_MAX_AMOUNT_ERROR(80006, "订单交易数量大于单笔订单最大交易量错误"),
    ORDER_PAYMENT_TERM_TYPE_NOT_EXISTS(80007, "订单付款类型不存在"),
    ORDER_ONE_KEY_MATCH_ERROR(80008, "订单一键匹配错误"),
    ORDER_STATUS_IS_NOT_ADD(80009, "订单非下单状态"),
    ORDER_STATUS_IS_NOT_PAY(80009, "订单非付款状态"),
    ORDER_STATUS_IS_NOT_RELEASE(80009, "订单非放行态"),
    ORDER_STATUS_IS_NOT_CANCEL(80009, "订单非取消状态"),
    ORDER_STATUS_IS_NOT_APPEAL(80009, "订单非申诉状态"),

    /**
     * 币种模块
     */
    COIN_NOT_EXISTS(90001, "币种不存在"),

    /**
     * 账户模块
     */
    ACCOUNT_BALANCE_NOT_ENOUGH(100001, "余额不足"),
    ACCOUNT_FROZEN_BALANCE_NOT_ENOUGH(100002, "冻结中的余额不足"),

    /**
     * 订单申诉
     */
    ORDER_APPEAL_STATUS_IS_NOT_APPEAL(110001, "申诉非已申诉状态"),
    ORDER_APPEAL_EXISTS(110002, "申诉已经存在"),
    ORDER_APPEAL_NOT_EXISTS(110003, "申诉不存在"),

    /**
     * 订单消息
     */
    ORDER_MESSAGE_STATUS_IS_READ(120001, "订单消息已读"),
    ORDER_MESSAGE_EXISTS(120002, "订单消息已经存在"),
    ORDER_AMESSAGE_NOT_EXISTS(120003, "订单消息不存在"),

    /**
     * 广告日志
     */

    ADVERTISE_LOG_EXISTS(130002, "广告日志已经存在"),

    /**
     * 订单日志
     */

    ORDER_LOG_EXISTS(140002, "订单日志已经存在"),

    ;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
