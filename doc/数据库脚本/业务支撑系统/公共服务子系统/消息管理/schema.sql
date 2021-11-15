DROP TABLE  IF EXISTS 	t_message ;
CREATE TABLE t_message  (
id bigint(16) NOT NULL,
message_no varchar(128) COMMENT '消息编号',
type tinyint(1) not null DEFAULT 0 COMMENT '消息类型,0:XXX,1:XXX,2:XXX',
title VARCHAR(256) NULL COMMENT '消息标题',
content LONGTEXT NULL COMMENT '消息正文',
href varchar(256) COMMENT '跳转链接',
username varchar(128) COMMENT '接收人登陆用户名,用户英文名称，用户编码',
status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:未读,1:已读',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 128 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY(message_no)
) DEFAULT CHARSET=utf8mb4  comment '消息管理';
