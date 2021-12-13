DROP TABLE IF EXISTS t_coin;
CREATE TABLE t_coin
(
    id               bigint(16) NOT NULL,
    coin_code  VARCHAR(16)         DEFAULT NULL comment '数字货币编码',
    coin_name        VARCHAR(16)   DEFAULT NULL comment '数字货币名称',
    coin_unit        VARCHAR(16)   DEFAULT NULL comment '数字货币单位',
    market_price DECIMAL(18,2) DEFAULT NULL comment '市场价(美元)',
    coin_rate DECIMAL(18,8) DEFAULT NULL comment '数字货币交易手续费比例',
    per_advertise_min_amount DECIMAL(18,8) DEFAULT NULL comment '单个广告最小库存量',
    per_advertise_max_amount DECIMAL(18,8) DEFAULT NULL comment '单个广告最大库存量',
    per_order_min_amount DECIMAL(18,8) DEFAULT NULL comment '单笔订单最小交易量',
    per_order_max_amount DECIMAL(18,8) DEFAULT NULL comment '单笔订单最大交易量',
    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (coin_code)
) DEFAULT CHARSET = utf8mb4 comment '数字货币';


