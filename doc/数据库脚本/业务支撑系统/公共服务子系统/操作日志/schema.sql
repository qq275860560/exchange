
DROP TABLE  IF EXISTS 	t_log ;
CREATE TABLE t_log  (
id bigint(16) NOT NULL,
type varchar(128) COMMENT '日志类型',
username varchar(128) COMMENT '登陆用户名,用户英文名称，用户编码',
request_uri varchar(256) COMMENT '请求URI',
request_param text COMMENT '请求参数',

duration bigint    NOT NULL COMMENT '执行时长(毫秒)',
ip varchar(64) COMMENT 'IP地址',
status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:执行失败,1:执行成功',
output_json  text DEFAULT NULL COMMENT '输出参数json',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 128 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id )
) DEFAULT CHARSET=utf8mb4  comment '操作日志';









