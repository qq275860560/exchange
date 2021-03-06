DROP TABLE IF EXISTS t_order_appeal;
CREATE TABLE t_order_appeal
(
    id               bigint(16) NOT NULL,
    order_appeal_code  VARCHAR(64)   DEFAULT NULL comment '申诉编码，唯一',
    order_appeal_type           tinyint(1) unsigned not null DEFAULT 1 comment '申诉类型,1:对方未付款 2：对方未放行 3:其他',
    order_code         VARCHAR(64)   DEFAULT NULL comment '订单编码',
    order_old_status           tinyint(1) unsigned not null DEFAULT 1 comment '订单申诉前状态,1:已下单,2:已付款,3:已放行,4:已取消',
    order_customer_username         VARCHAR(64)         DEFAULT NULL comment '订单顾客的登陆用户名,用户英文名称，用户编码',
    advertise_code         VARCHAR(64)   DEFAULT NULL comment '广告编码',
    advertise_business_username         VARCHAR(64)         DEFAULT NULL comment '广告商家的登陆用户名,用户英文名称，用户编码',
    order_appeal_content  text  DEFAULT NULL comment '申诉内容',
    status           tinyint(1) unsigned not null DEFAULT 1 comment '申诉状态,1:等待处理,2:取消申诉,3:审核失败,4:审核成功',
    order_appeal_username  VARCHAR(64)         DEFAULT NULL comment '申诉人的登陆用户名,用户英文名称，用户编码',
    create_time      datetime            DEFAULT NULL,
    order_appeal_audit_username  VARCHAR(64)         DEFAULT NULL comment '审核人的登陆用户名,用户英文名称，用户编码',
    order_appeal_audit_time      datetime            DEFAULT NULL,
    order_appeal_audit_result           text                DEFAULT NULL comment '审核结果',
    order_appeal_cancel_username  VARCHAR(64)         DEFAULT NULL comment '取消人的登陆用户名,用户英文名称，用户编码',
    order_appeal_cancel_time      datetime            DEFAULT NULL,
    order_appeal_cancel_result           text                DEFAULT NULL comment '取消结果',

    PRIMARY KEY (id),
    UNIQUE KEY (order_appeal_code)
) DEFAULT CHARSET = utf8mb4 comment '订单申诉';


