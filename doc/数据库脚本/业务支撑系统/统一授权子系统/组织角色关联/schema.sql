
DROP TABLE  IF EXISTS 	t_org_role ;
CREATE TABLE t_org_role  (
                             id bigint(16) NOT NULL,
                             orgname VARCHAR ( 128 )  NOT NULL comment '组织名称,组织英文名称，组织编码，唯一，必须在组织表存在',
                             rolename VARCHAR ( 128 )  NOT NULL comment '角色名称，角色英文名称，角色编码，必须在角色表存在',
                             status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
                             create_user_id bigint(16) DEFAULT 0,
                             create_user_name VARCHAR ( 128 ) DEFAULT NULL,
                             create_time datetime DEFAULT NULL,
                             remark text DEFAULT NULL comment '备注',
                             PRIMARY KEY ( id ),
                             UNIQUE KEY(orgname,rolename)
) DEFAULT CHARSET=utf8mb4  comment '组织角色关联';