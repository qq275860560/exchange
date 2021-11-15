DROP TABLE  IF EXISTS 	t_order_message ;
CREATE TABLE t_order_message  (
id bigint(16) NOT NULL,
order_message_code varchar(128) COMMENT '消息编号，唯一',
order_code         VARCHAR(64)   DEFAULT NULL comment '订单编码',
order_message_type tinyint(1) not null DEFAULT 0 COMMENT '消息类型,0:文本,2:图片',
order_message_content LONGTEXT NULL COMMENT '订单消息',
order_message_sender_username varchar(128) COMMENT '消息发送者的登陆用户名,用户英文名称，用户编码',
order_message_receiver_username varchar(128) COMMENT '消息接收者的登陆用户名,用户英文名称，用户编码',
status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:未读,1:已读',
create_time datetime DEFAULT NULL,
read_time datetime null comment '消息读取时间',
PRIMARY KEY ( id ),
UNIQUE KEY(order_message_code)
) DEFAULT CHARSET=utf8mb4  comment '消息管理';
