DROP TABLE IF EXISTS t_config;
CREATE TABLE t_config
(
    id               bigint(16) NOT NULL,
    config_key         VARCHAR(16)   DEFAULT NULL comment '键',
    config_value         VARCHAR(16)   DEFAULT NULL comment '值',
   status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (country_code)
) DEFAULT CHARSET = utf8mb4 comment '全局配置，直接使用springboot配置文件的配置参数';


