
DROP TABLE  IF EXISTS 	t_role_permission ;
CREATE TABLE t_role_permission  (
 id bigint(16) NOT NULL,
 rolename VARCHAR ( 128 )  NOT NULL comment '角色名称，角色英文名称，角色编码，必须在角色表存在',
 permissionname VARCHAR ( 128 )  NOT NULL comment '权限名称，权限英文名称，权限编码，必须在权限表存在',
 status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
 create_user_id bigint(16) DEFAULT 0,
 create_user_name VARCHAR ( 128 ) DEFAULT NULL,
 create_time datetime DEFAULT NULL,
 remark text DEFAULT NULL comment '备注',
 PRIMARY KEY ( id ),
 UNIQUE KEY(rolename,permissionname)
) DEFAULT CHARSET=utf8mb4  comment '角色权限关联';
