DROP TABLE IF EXISTS t_account;
CREATE TABLE t_account
(
    id               bigint(16) NOT NULL,
    username        VARCHAR(64)   DEFAULT NULL comment '用户名称',
    coin_code  VARCHAR(16)         DEFAULT NULL comment '数字货币编码',
    total_balance DECIMAL(18,8) DEFAULT NULL comment '总金额',
    available_balance DECIMAL(18,8) DEFAULT NULL comment '可用金额',
    frozen_balance DECIMAL(18,8) DEFAULT NULL comment '冻结金额',
    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (username,coin_code)
) DEFAULT CHARSET = utf8mb4 comment '账户管理';


