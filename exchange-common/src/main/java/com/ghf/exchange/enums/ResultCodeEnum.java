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
    TASK_TYPE_NOT_EXISTS(61906, "任务类型不存在"),
    TASK_INVOKE_TYPE_DELETE(61907, "任务类型不存在"),
    TASK_LOG_EXISTS(61908, "任务日志已经存在"),
    TASK_LOG_NOT_EXISTS(61909, "任务日志不存在"),
    TASK_CLASS_NAME_NOT_EMPTY(61910, "类全名不能为空"),
    TASK_METHOD_NAME_NOT_EMPTY(61911, "方法名称不能为空"),
    TASK_REQUEST_URL_NOT_EMPTY(61912, "方法名称不能为空"),
    TASK_REQUEST_HEADER_NOT_EMPTY(61912, "方法名称不能为空"),
    TASK_CRON_EXPRESSION_NOT_EMPTY(61913, "CRON表达式不能为空"),

    /**
     * 广告模块
     */
    ADVERTISE_EXISTS(70001, "广告已经存在"),
    ADVERTISE_NOT_EXISTS(70002, "广告不存在"),

    ADVERTISE_BUY_SELL_TYPE_NOT_EXISTS(70003, "广告买卖类型不存在"),
    ADVERTISE_PRICE_TYPE_NOT_EXISTS(70004, "广告价格类型不存在"),
    ADVERTISE_STATUSE_NOT_EXISTS(70005, "广告状态不存在"),
    ADVERTISE_PAYMENT_TERM_TIME_ERROR(70006, "广告付款期限错误"),
    ADVERTISE_AVAILABLE_AMOUNT_ERROR(70007, "广告库存错误"),

    ADVERTISE_STATUS_IS_NOT_PUT_OFF_SHELVES(70009, "广告非下架状态"),
    ADVERTISE_STATUS_IS_NOT_PUT_ON_SHELVES(70010, "广告非上架状态"),

    ADVERTISE_FROZEN_AMOUNT_IS_NOT_ZERO(70012, "广告冻结库存非零"),
    ADVERTISE_AMOUNT_NOT_ENOUGH(70013, "广告库存库存不足"),
    ADVERTISE_FROZEN_AMOUNT_NOT_ENOUGH(70014, "广告冻结库存不足"),
    ADVERTISE_AVAILABLE_AMOUNT_LEGAL(70015, "广告可用库存正常"),
    ADVERTISE_FIXED_PRICE_ERROR(70016, "广告固定价格错误"),
    ADVERTISE_PREMIUM_RATE_ERROR(70017, "广告溢价率错误"),
    ADVERTISE_CODE_CAN_NOT_EMPTY(70018, "广告编码不能为空"),
    /**
     * 订单模块
     */
    ORDER_EXISTS(80001, "订单已经存在"),
    ORDER_NOT_EXISTS(80002, "订单不存在"),
    ORDER_SOURCE_NOT_EXISTS(80003, "订单来源不存在"),
    ORDER_BUY_SELL_TYPE_NOT_EXISTS(80004, "订单买卖类型不存在"),
    ORDER_AMOUNT_ERROR(80006, "订单交易数量大于单笔订单最大交易量错误"),
    ORDER_ONE_KEY_MATCH_ERROR(80008, "订单一键匹配错误"),
    ORDER_STATUS_IS_NOT_ADD(80009, "订单非下单状态"),
    ORDER_STATUS_IS_NOT_PAY(80010, "订单非付款状态"),
    ORDER_STATUS_IS_NOT_RELEASE(80011, "订单非放行态"),
    ORDER_STATUS_IS_NOT_CANCEL(80012, "订单非取消状态"),
    ORDER_STATUS_IS_NOT_APPEAL(80013, "订单非申诉状态"),
    ORDER_CUSTOMER_PAYMENT_NOT_EMPTY(80014, "订单顾客支付信息不能为空"),
    ORDER_STATUS_CAN_NOT_APPEAL(80015, "订单当前状态无法申诉"),
    ORDER_STATUS_IS_CANCEL(80016, "订单已经为取消状态"),
    ORDER_ADD_STATUS_EXISTS(80017, "下单状态的订单已存在"),
    ORDER_STATUS_IS_NOT_ADD_OR_PAY(80018, "订单非下单状态或已付款状态"),
    ORDER_PAY_TIME_LEGAL(80019, "订单支付时间正常"),
    ORDER_RELEASE_TIME_LEGAL(80020, "订单放行时间正常"),
    ORDER_COIN_CODE_CAN_NOT_EMPTY(80021, "订单币种编码不能为空"),

    /**
     * 币种模块
     */
    COIN_NOT_EXISTS(90001, "币种不存在"),
    COIN_STATUS_NOT_ENABLE(90002, "币种状态不是启用状态"),
    COIN_STATUS_ENABLE(90003, "币种状态已经是启用状态"),
    COIN_STATUS_DISABLE(90003, "币种状态已经是禁用状态"),

    /**
     * 账户模块
     */
    ACCOUNT_BALANCE_NOT_ENOUGH(100001, "余额不足"),
    ACCOUNT_FROZEN_BALANCE_NOT_ENOUGH(100002, "冻结中的余额不足"),
    ACCOUNT_USERNAME_CAN_NOT_EMPTY(100003, "账户用户名不能为空"),
    ACCOUNT_COINCODE_CAN_NOT_EMPTY(100004, "账户币种编码不能为空"),

    /**
     * 订单申诉
     */
    ORDER_APPEAL_STATUS_IS_NOT_APPEAL(110001, "申诉非已申诉状态"),
    ORDER_APPEAL_EXISTS(110002, "申诉已经存在"),
    ORDER_APPEAL_NOT_EXISTS(110003, "申诉不存在"),
    OTHER_APPEAL_TYPE_NOT_EXISTS(110004, "申诉类型不存在"),
    ORDER_APPEAL_STATUS_IS_NOT_FAIL_OR_SUCCESS(110005, "申诉非审核失败或审核成功状态"),
    ORDER_APPEAL_TYPE_IS_NOT_UN_PAY(110006, "申诉类型非未付款"),
    ORDER_APPEAL_TYPE_IS_NOT_UN_RELEASE(110007, "申诉类型非未放行"),

    /**
     * 订单消息
     */
    ORDER_MESSAGE_STATUS_READ(120001, "订单消息已读"),
    ORDER_MESSAGE_EXISTS(120002, "订单消息已经存在"),
    ORDER_AMESSAGE_NOT_EXISTS(120003, "订单消息不存在"),
    ORDER_AMESSAGE_TYPE_NOT_EXISTS(120004, "订单消息不存在"),

    /**
     * 广告日志
     */

    ADVERTISE_LOG_EXISTS(130002, "广告日志已经存在"),

    /**
     * 广告日志
     */

    ADVERTISE_AMOUNT_LOG_EXISTS(140002, "广告库存量日志已经存在"),

    /**
     * 订单日志
     */

    ORDER_LOG_EXISTS(150002, "订单日志已经存在"),

    /**
     * 付款方式
     */
    PAYMENT_EXISTS(160001, "付款方式已经存在"),
    PAYMENT_NOT_EXISTS(160002, "付款方式不存在"),
    PAYMENT_TYPE_NOT_EXISTS(160003, "付款类型不存在"),
    PAYMENT_TYPE_EXISTS(160004, "付款类型已经存在"),
    PAYMENT_TYPE_ARRAY_NOT_EMPTY(160005, "付款类型数组不能为空"),
    PAYMENT_STATUS_DISABLE(160006, "付款方式状态已经禁用"),
    PAYMENT_STATUS_ENABLE(160007, "付款方式状态已经启用"),

    /**
     * 广告商家
     */
    ADVERTISE_BUSINESS_EXISTS(170001, "广告商家已经存在"),
    ADVERTISE_BUSINESS_NOT_EXISTS(170002, "广告商家不存在"),
    ADVERTISE_BUSINESS_USERNAME_NOT_EMPTY(170003, "广告商家用户名不能为空"),
    ADVERTISE_BUSINESS_USERNAME_EXISTS(170004, "广告商家用户名已经存在"),
    ADVERTISE_BUSINESS_STATUS_DISABLE(170006, "广告商家状态已经禁用"),
    ADVERTISE_BUSINESS_STATUS_ENABLE(170007, "广告商家状态已经启用"),
    ADVERTISE_BUSINESS_KYC_NOT_EXISTS(170008, "广告商家kyc不存在"),

    /**
     * 订单顾客
     */
    ORDER_CUSTOMER_EXISTS(180001, "订单顾客已经存在"),
    ORDER_CUSTOMER_NOT_EXISTS(180002, "订单顾客不存在"),
    ORDER_CUSTOMER_USERNAME_NOT_EMPTY(180003, "订单顾客用户名不能为空"),
    ORDER_CUSTOMER_USERNAME_EXISTS(180004, "订单顾客用户名已经存在"),
    ORDER_CUSTOMER_STATUS_DISABLE(180006, "订单顾客状态已经禁用"),
    ORDER_CUSTOMER_STATUS_ENABLE(180007, "订单顾客状态已经启用"),
    ORDER_CUSTOMER_ORDER_TODAY_CANCEL_COUNT_OVERLOAD(180008, "订单顾客每日订单取消次数超过系统规定"),

    /**
     * 法币模块
     */
    LEGAL_CURRENCY_NOT_EXISTS(90001, "法币不存在"),
    LEGAL_CURRENCY_STATUS_NOT_ENABLE(90002, "法币状态不是启用状态"),
    LEGAL_CURRENCY_STATUS_ENABLE(90003, "法币状态已经是启用状态"),
    LEGAL_CURRENCY_STATUS_DISABLE(90003, "法币状态已经是禁用状态"),

    ;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

}
