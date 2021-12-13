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

    advertise_total_amount DECIMAL(18,8) DEFAULT NULL comment '总库存数量',
    advertise_available_amount DECIMAL(18,8) DEFAULT NULL comment '可用库存数量',
    advertise_frozen_amount DECIMAL(18,8) DEFAULT NULL comment '冻结库存数量',


    advertise_legal_currency_country_code VARCHAR(16)         DEFAULT NULL comment '法币所在国家编码',
    advertise_legal_currency_country_name VARCHAR(16)         DEFAULT NULL comment '法币所在国家名称',
    advertise_legal_currency_code VARCHAR(16)         DEFAULT NULL comment '法币编码',
    advertise_legal_currency_name VARCHAR(16)         DEFAULT NULL comment '法币名称',
    advertise_legal_currency_symbol VARCHAR(16)         DEFAULT NULL comment '法币符号',
    advertise_legal_currency_unit VARCHAR(16)         DEFAULT NULL comment '法币单位',

    advertise_price_type tinyint(1) unsigned not null DEFAULT 1 comment '价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))',
    advertise_fixed_price DECIMAL(18,8) DEFAULT NULL comment '固定价格，价格类型固定时才有效',
    advertise_premium_rate DECIMAL(18,8) DEFAULT NULL comment '溢价率，价格类型变化时才有效',
    advertise_auto_reply_content          text default null  comment '自动回复内容',

    advertise_business_payment_codes     varchar(256)   not null DEFAULT 1 comment '广告商家支持的付款方式编码，逗号区分',
    advertise_business_payment_types     varchar(16)   not null DEFAULT 1 comment '广告商家支持的收付款类型:1:支付宝，2：微信，3：银行卡,逗号区分',



    advertise_business_username         VARCHAR(64)         DEFAULT NULL comment '广告商家的登陆用户名,用户英文名称，用户编码',
    advertise_business_nickname         VARCHAR(64)         DEFAULT NULL comment '广告商家的用户昵称，用户中文名称，可以随时修改',
    advertise_business_realname         VARCHAR(64)         DEFAULT NULL comment '广告商家的真实姓名',

    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,1:上架,2:下架,3:删除',
    create_time      datetime            DEFAULT NULL,
    remark           text                DEFAULT NULL comment '备注',
    PRIMARY KEY (id),
    UNIQUE KEY (advertise_code)
) DEFAULT CHARSET = utf8mb4 comment '广告管理';


