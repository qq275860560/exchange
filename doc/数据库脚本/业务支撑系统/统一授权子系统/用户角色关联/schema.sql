DROP TABLE  IF EXISTS 	t_user_role ;
CREATE TABLE t_user_role  (
 id bigint(16) NOT NULL,
 username VARCHAR ( 128 ) NOT NULL comment '登陆用户名,用户英文名称，用户编码，必须在用户表存在',
 rolename VARCHAR ( 128 )  NOT NULL comment '角色名称，角色英文名称，必须在角色表存在',
 status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
 create_user_id bigint(16) DEFAULT 0,
 create_user_name VARCHAR ( 128 ) DEFAULT NULL,
 create_time datetime DEFAULT NULL,
 remark text DEFAULT NULL comment '备注',
 PRIMARY KEY ( id ),
 UNIQUE KEY(username,rolename)
) DEFAULT CHARSET=utf8mb4  comment '用户角色关联';

