
DROP TABLE  IF EXISTS 	t_advertise_log ;
CREATE TABLE t_advertise_log  (
id bigint(16) NOT NULL,
advertise_log_code         VARCHAR(64)   DEFAULT NULL comment '广告日志编码， 唯一',
advertise_code         VARCHAR(64)   DEFAULT NULL comment '广告编码',
advertise_log_type tinyint(1) unsigned   comment '操作类型，1:发布,2:上架:3:下架,4:删除',

advertise_log_client_id varchar(128) COMMENT '登录客户端',
advertise_log_username varchar(128) COMMENT '操作人，如果是后端服务器进行操作，此字段为空',
create_time datetime  COMMENT '操作时间',
advertise_log_ip_addr varchar(64) COMMENT 'IP地址',
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY (advertise_log_code)
) DEFAULT CHARSET=utf8mb4  comment '广告日志';









