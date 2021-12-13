DROP TABLE  IF EXISTS 	t_order_message ;
CREATE TABLE t_order_message  (
id bigint(16) NOT NULL,
order_message_code varchar(128) COMMENT '消息编号，唯一',
order_code         VARCHAR(64)   DEFAULT NULL comment '订单编码',
order_message_type tinyint(1) not null DEFAULT 0 COMMENT '订单消息类型,1:文本,2:图片',
order_message_content LONGTEXT NULL COMMENT '订单消息内容',
order_message_sender_username varchar(128) COMMENT '消息发送者的登陆用户名,用户英文名称，用户编码',
order_message_receiver_username varchar(128) COMMENT '消息接收者的登陆用户名,用户英文名称，用户编码',
create_time datetime DEFAULT NULL,
PRIMARY KEY ( id ),
UNIQUE KEY(order_message_code)
) DEFAULT CHARSET=utf8mb4  comment '订单消息';
