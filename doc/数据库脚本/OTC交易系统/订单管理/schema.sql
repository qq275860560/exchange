DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order
(
    id               bigint(16) NOT NULL,
    order_code         VARCHAR(64)   DEFAULT NULL comment '订单编码， 唯一',
    order_source           tinyint(1) unsigned not null DEFAULT 1 comment '订单来源,1:广告区选中下单,2:快捷区一键匹配下单',
    order_buy_sell_type           tinyint(1) unsigned not null DEFAULT 1 comment '订单买卖类型,1:买币,2:卖币，跟广告买卖类型相反',
    order_coin_code VARCHAR(16)         DEFAULT NULL comment '订单币种编码',
    order_coin_name  VARCHAR(16)         DEFAULT NULL comment '订单币种名称',
    order_coin_unit  VARCHAR(16)         DEFAULT NULL comment '订单币种单位',
    order_coin_rate DECIMAL(18,8) DEFAULT NULL comment '订单币种交易手续费比例',
    order_amount  DECIMAL(18,8) DEFAULT NULL comment '订单交易量',
    order_price  DECIMAL(18,8) DEFAULT NULL comment '订单法币成交单价，后台计算，如果是广告区选中下单，使用广告对应的单价，如果使用快捷区下单，使用系统一键匹配的单价',
    order_total_price  DECIMAL(18,8) DEFAULT NULL comment '订单法币成交总价,后台计算',
    order_customer_payment_type   tinyint(4) unsigned not null DEFAULT 1 comment '订单顾客选择的收付款类型:1:支付宝，2：微信，3：银行卡',

    order_customer_username         VARCHAR(64)         DEFAULT NULL comment '订单顾客的登陆用户名,用户英文名称，用户编码',
    order_customer_nickname         VARCHAR(64)         DEFAULT NULL comment '订单顾客的用户昵称，用户中文名称，可以随时修改',
    order_customer_realname         VARCHAR(64)         DEFAULT NULL comment '订单顾客的真实姓名',

    order_customer_payment_code    varchar(256)   not null DEFAULT 1 comment '订单顾客支持的付款方式编码',
    order_customer_payment_type_alipay_account VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:支付宝方式,支付宝账号',
    order_customer_payment_type_alipay_qrcode VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:支付宝方式,支付宝二维码',
    order_customer_payment_type_wechat_account VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:微信方式,微信账号',
    order_customer_payment_type_wechat_qrcode VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:微信方式,微信二维码',
    order_customer_payment_type_bank_name VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:银行方式,银行名称',
    order_customer_payment_type_bank_branch_name VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:银行方式,支行账号',
    order_customer_payment_type_bank_account VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:银行方式,银行卡号',
    order_customer_payment_type_bank_realname VARCHAR(64)   DEFAULT NULL comment '订单顾客支持的付款条件:银行方式,户主真实姓名',

    advertise_code         VARCHAR(64)   DEFAULT NULL comment '广告编码',
    advertise_buy_sell_type           tinyint(1) unsigned not null DEFAULT 1 comment '广告买卖类型,1:买币,2:卖币',
    advertise_coin_code  VARCHAR(16)         DEFAULT NULL comment '广告币种编码',
    advertise_coin_name  VARCHAR(16)         DEFAULT NULL comment '广告币种名称',
    advertise_coin_unit  VARCHAR(16)         DEFAULT NULL comment '广告币种单位',
    advertise_coin_rate DECIMAL(18,8) DEFAULT NULL comment '广告币种交易手续费比例',
    advertise_available_amount DECIMAL(18,8) DEFAULT NULL comment '广告可用库存数量',

    advertise_legal_currency_country_code VARCHAR(16)         DEFAULT NULL comment '广告法币所在国家编码',
    advertise_legal_currency_country_name VARCHAR(16)         DEFAULT NULL comment '广告法币所在国家名称',
    advertise_legal_currency_code VARCHAR(16)         DEFAULT NULL comment '广告法币编码',
    advertise_legal_currency_name VARCHAR(16)         DEFAULT NULL comment '广告法币名称',
    advertise_legal_currency_symbol VARCHAR(16)         DEFAULT NULL comment '广告法币符号',
    advertise_legal_currency_unit VARCHAR(16)         DEFAULT NULL comment '广告法币单位',

    advertise_price_type tinyint(1) unsigned not null DEFAULT 1 comment '广告价格类型,1:固定价格,2:变化价格,价格=市场价格*(1+溢价比例))',
    advertise_fixed_price DECIMAL(18,8) DEFAULT NULL comment '广告固定价格，价格类型固定时才有效',
    advertise_premium_rate DECIMAL(18,8) DEFAULT NULL comment '广告溢价率，价格类型变化时才有效',

    advertise_business_username         VARCHAR(64)         DEFAULT NULL comment '广告商家的登陆用户名,用户英文名称，用户编码',
    advertise_business_nickname         VARCHAR(64)         DEFAULT NULL comment '广告商家的用户昵称，用户中文名称，可以随时修改',
    advertise_business_realname         VARCHAR(64)         DEFAULT NULL comment '广告商家的真实姓名',

    advertise_business_payment_code     varchar(256)   not null DEFAULT 1 comment '广告商家支持的付款方式编码',
    advertise_business_payment_type_alipay_account VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:支付宝方式,支付宝账号',
    advertise_business_payment_type_alipay_qrcode VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:支付宝方式,支付宝二维码',
    advertise_business_payment_type_wechat_account VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:微信方式,微信账号',
    advertise_business_payment_type_wechat_qrcode VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:微信方式,微信二维码',
    advertise_business_payment_type_bank_name VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:银行方式,银行名称',
    advertise_business_payment_type_bank_branch_name VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:银行方式,支行账号',
    advertise_business_payment_type_bank_account VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:银行方式,银行卡号',
    advertise_business_payment_type_bank_realname VARCHAR(64)   DEFAULT NULL comment '广告商家支持的付款条件:银行方式,户主真实姓名',



    status           tinyint(1) unsigned not null DEFAULT 1 comment '订单状态,1:已下单,2:已付款,3:已放行,4:已申诉,5:已取消',
    add_time      datetime            DEFAULT NULL,
    pay_time      datetime            DEFAULT NULL,
    release_time      datetime            DEFAULT NULL,
    appeal_time      datetime            DEFAULT NULL,
    cancel_time      datetime            DEFAULT NULL,
    agree_un_pay_time      datetime            DEFAULT NULL,
    agree_un_release_time      datetime            DEFAULT NULL,
    remark           text                DEFAULT NULL comment '备注',
    PRIMARY KEY (id),
    UNIQUE KEY (order_code)
) DEFAULT CHARSET = utf8mb4 comment '订单管理';


