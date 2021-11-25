DROP TABLE IF EXISTS t_coin;
CREATE TABLE t_coin
(
    id               bigint(16) NOT NULL,
    coin_code  VARCHAR(16)         DEFAULT NULL comment '币种编码',
    coin_name        VARCHAR(16)   DEFAULT NULL comment '币种名称',
    coin_unit        VARCHAR(16)   DEFAULT NULL comment '币种单位',
    market_price DECIMAL(18,2) DEFAULT NULL comment '市场价(美元)',
    coin_rate DECIMAL(18,8) DEFAULT NULL comment '币种交易手续费比例',
    per_min_amount DECIMAL(18,8) DEFAULT NULL comment '单笔最小交易量',
    per_max_amount DECIMAL(18,8) DEFAULT NULL comment '单笔最大交易量',
    min_payment_term_time           int unsigned not null  comment '最小付款期限',
    max_payment_term_time           int unsigned not null  comment '最大付款期限',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (coin_code)
) DEFAULT CHARSET = utf8mb4 comment '币种';


