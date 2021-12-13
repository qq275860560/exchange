DROP TABLE IF EXISTS t_payment;
CREATE TABLE t_payment
(
    id               bigint(16) NOT NULL,
    payment_code         VARCHAR(64)   DEFAULT NULL comment '收款方式编码， 唯一',
    username         VARCHAR(64)         DEFAULT NULL comment '收付款的登陆用户名,用户英文名称，用户编码',
    payment_type    varchar(16)   not null DEFAULT 1 comment '收付款类型:1:支付宝，2：微信，3：银行卡，4：现金,逗号区分',
    payment_type_alipay_account VARCHAR(64)   DEFAULT NULL comment '支付宝方式,支付宝账号',
    payment_type_alipay_qrcode VARCHAR(64)   DEFAULT NULL comment '支付宝方式,支付宝二维码',
    payment_type_wechat_account VARCHAR(64)   DEFAULT NULL comment '微信方式,微信账号',
    payment_type_wechat_qrcode VARCHAR(64)   DEFAULT NULL comment '微信方式,微信二维码',
    payment_type_bank_name VARCHAR(64)   DEFAULT NULL comment '银行方式,银行名称',
    payment_type_bank_branch_name VARCHAR(64)   DEFAULT NULL comment '银行方式,支行名称',
    payment_type_bank_account VARCHAR(64)   DEFAULT NULL comment '银行方式,银行卡号',
    payment_type_bank_realname VARCHAR(64)   DEFAULT NULL comment '银行方式,户主真实姓名',
    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    remark           text                DEFAULT NULL comment '备注',
    PRIMARY KEY (id),
    UNIQUE KEY (payment_code)
) DEFAULT CHARSET = utf8mb4 comment '收款方式';


