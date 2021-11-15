DROP TABLE IF EXISTS t_advertise;
CREATE TABLE t_advertise
(
    id               bigint(16) NOT NULL,
    advertise_code         VARCHAR(64)   DEFAULT NULL comment '广告编码， 唯一',
    advertise_buy_sell_type           tinyint(1) unsigned not null DEFAULT 1 comment '买卖类型,1:买币,2:卖币',
    advertise_coin_code  VARCHAR(16)         DEFAULT NULL comment '币种编码',
    advertise_coin_name  VARCHAR(16)         DEFAULT NULL comment '币种名称',
    advertise_coin_unit  VARCHAR(16)         DEFAULT NULL comment '币种单位',
    advertise_coin_rate DECIMAL(18,8) DEFAULT NULL comment '币种交易手续费比例',


    advertise_amount DECIMAL(18,8) DEFAULT NULL comment '库存量',
    advertise_frozen_amount DECIMAL(18,8) DEFAULT NULL comment '冻结库存量',
    advertise_per_max_amount DECIMAL(18,8) DEFAULT NULL comment '单笔最大交易量',
    advertise_per_min_amount DECIMAL(18,8) DEFAULT NULL comment '单笔最小交易量',

    advertise_legal_currency_country_code VARCHAR(16)         DEFAULT NULL comment '法币所在国家编码',
    advertise_legal_currency_symbol VARCHAR(16)         DEFAULT NULL comment '法币符号',
    advertise_legal_currency_unit VARCHAR(16)         DEFAULT NULL comment '法币单位',

    advertise_price_type tinyint(1) unsigned not null DEFAULT 1 comment '价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))',
    advertise_fixed_price DECIMAL(18,8) DEFAULT NULL comment '固定价格，价格类型固定时才有效',
    advertise_premium_rate DECIMAL(18,8) DEFAULT NULL comment '溢价率，价格类型变化时才有效',
    advertise_auto_reply_content          text default null  comment '自动回复内容',

    advertise_business_payment_term_time      tinyint(4) unsigned not null DEFAULT 1 comment '付款时间条件，从下单到点击确认付款的时间，单位分钟',
    advertise_business_payment_term_type_array     varchar(16)   not null DEFAULT 1 comment '广告商家支持的收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分',

    advertise_business_payment_term_type_alipay_account VARCHAR(64)   DEFAULT NULL comment '付款条件:支付宝方式,支付宝账号',
    advertise_business_payment_term_type_alipay_qrcode VARCHAR(64)   DEFAULT NULL comment '付款条件:支付宝方式,支付宝二维码',

    advertise_business_payment_term_type_wechat_account VARCHAR(64)   DEFAULT NULL comment '付款条件:微信方式,微信账号',
    advertise_business_payment_term_type_wechat_qrcode VARCHAR(64)   DEFAULT NULL comment '付款条件:微信方式,微信二维码',

    advertise_business_payment_term_type_bank_name VARCHAR(64)   DEFAULT NULL comment '付款条件:银行方式,银行名称',
    advertise_business_payment_term_type_bank_branch_name VARCHAR(64)   DEFAULT NULL comment '付款条件:银行方式,支行账号',
    advertise_business_payment_term_type_bank_account VARCHAR(64)   DEFAULT NULL comment '付款条件:银行方式,银行卡号',
    advertise_business_payment_term_type_bank_realname VARCHAR(64)   DEFAULT NULL comment '付款条件:银行方式,户主真实姓名',



    advertise_business_username         VARCHAR(64)         DEFAULT NULL comment '广告商家的登陆用户名,用户英文名称，用户编码',
    advertise_business_nickname         VARCHAR(64)         DEFAULT NULL comment '广告商家的用户昵称，用户中文名称，可以随时修改',
    advertise_business_realname         VARCHAR(64)         DEFAULT NULL comment '广告商家的真实姓名',

    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,1:上架,2:下架,3:删除',
    create_time      datetime            DEFAULT NULL,
    remark           text                DEFAULT NULL comment '备注',
    PRIMARY KEY (id),
    UNIQUE KEY (advertise_code)
) DEFAULT CHARSET = utf8mb4 comment '广告';


