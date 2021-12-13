
DROP TABLE  IF EXISTS 	t_advertise_amount_log ;
CREATE TABLE t_advertise_amount_log  (
id bigint(16) NOT NULL,
advertise_amount_log_code         VARCHAR(64)   DEFAULT NULL comment '广告日志编码， 唯一',
advertise_code         VARCHAR(64)   DEFAULT NULL comment '广告编码',
advertise_amount_log_type tinyint(1) unsigned   comment '操作类型，1:冻结广告库存数量,2:解冻广告库存数量:3:扣减广告冻结库存数量',
amount DECIMAL(18,8) DEFAULT NULL comment '变动库存数量',
before_advertise_total_amount DECIMAL(18,8) DEFAULT NULL comment '变动之前总库存数量',
before_advertise_available_amount DECIMAL(18,8) DEFAULT NULL comment '变动之前可用库存数量',
before_advertise_frozen_amount DECIMAL(18,8) DEFAULT NULL comment '变动之前冻结库存数量',
after_advertise_total_amount DECIMAL(18,8) DEFAULT NULL comment '变动之后总库存数量',
after_advertise_available_amount DECIMAL(18,8) DEFAULT NULL comment '变动之后可用库存数量',
after_advertise_frozen_amount DECIMAL(18,8) DEFAULT NULL comment '变动之后冻结库存数量',
advertise_log_client_id varchar(128) COMMENT '登录客户端',
advertise_log_username varchar(128) COMMENT '操作人，如果是后端服务器进行操作，此字段为空',
create_time datetime  COMMENT '操作时间',
advertise_log_ip_addr varchar(64) COMMENT 'IP地址',
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY (advertise_amount_log_code)
) DEFAULT CHARSET=utf8mb4  comment '广告库存数量日志';









