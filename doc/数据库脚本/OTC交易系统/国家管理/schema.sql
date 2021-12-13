DROP TABLE IF EXISTS t_country;
CREATE TABLE t_country
(
    id               bigint(16) NOT NULL,
    country_code         VARCHAR(16)   DEFAULT NULL comment '国家编码',
    country_name         VARCHAR(16)   DEFAULT NULL comment '国家名称',
   status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (country_code)
) DEFAULT CHARSET = utf8mb4 comment '国家管理';


