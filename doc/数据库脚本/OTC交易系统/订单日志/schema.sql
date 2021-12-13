
DROP TABLE  IF EXISTS 	t_order_log ;
CREATE TABLE t_order_log  (
id bigint(16) NOT NULL,
order_log_code         VARCHAR(64)   DEFAULT NULL comment '订单日志编码， 唯一',
order_code         VARCHAR(64)   DEFAULT NULL comment '订单编码',
order_log_type tinyint(1) unsigned   comment '操作类型，1:下单,2:付款:3:放行,4:取消,5:恢复,6:同意未付款申诉,7:同意未放行申诉',

order_log_client_id varchar(128) COMMENT '登录客户端',
order_log_username varchar(128) COMMENT '操作人，如果是后端服务器进行操作，此字段为空',
create_time datetime  COMMENT '操作时间',
order_log_ip_addr varchar(64) COMMENT 'IP地址',
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY (order_log_code)
) DEFAULT CHARSET=utf8mb4  comment '订单日志';









