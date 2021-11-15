DROP TABLE  IF EXISTS 	t_role ;
CREATE TABLE t_role  (
id bigint(16) NOT NULL,
rolename VARCHAR ( 128 )  NOT NULL comment '角色名称，角色英文名称，角色编码，唯一,ROLE_开头',
roledesc VARCHAR ( 128 ) DEFAULT NULL comment '角色描述，角色中文名称,比如平台管理员,普通注册用户',
permissionnames        text       DEFAULT NULL comment '权限名称列表，冗余,逗号隔开，前后都有逗号',
permissiondescs        text       DEFAULT NULL comment '权限描述列表，冗余,逗号隔开，前后都有逗号',
status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 128 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY(rolename)
) DEFAULT CHARSET=utf8mb4  comment '角色';