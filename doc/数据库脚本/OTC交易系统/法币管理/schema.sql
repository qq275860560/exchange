DROP TABLE IF EXISTS t_legal_currency;
CREATE TABLE t_legal_currency
(
    id               bigint(16) NOT NULL,
    legal_currency_code   VARCHAR(16)         DEFAULT NULL comment '法币编码',
    legal_currency_name   VARCHAR(16)         DEFAULT NULL comment '法币名称',
    legal_currency_symbol        VARCHAR(16)   DEFAULT NULL comment '法币符号',
    legal_currency_unit        VARCHAR(16)   DEFAULT NULL comment '法币单位',
    legal_currency_country_code         VARCHAR(16)   DEFAULT NULL comment '法币所在国家编码',
    legal_currency_country_name         VARCHAR(16)   DEFAULT NULL comment '法币所在国家名称',
    legal_currency_exchange_rate        DECIMAL(18,8) DEFAULT NULL comment '汇率，兑美元汇率',
    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (legal_currency_code),
    UNIQUE KEY (legal_currency_country_code)
) DEFAULT CHARSET = utf8mb4 comment '法币管理';


