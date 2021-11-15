DROP TABLE  IF EXISTS 	t_user_org ;
CREATE TABLE t_user_org  (
                              id bigint(16) NOT NULL,
                              username VARCHAR ( 128 ) NOT NULL comment '登陆用户名,用户英文名称，用户编码，必须在用户表存在',
                              orgname VARCHAR ( 128 )  NOT NULL comment '组织名称,组织英文名称，必须在组织表存在',
                              status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
                              create_user_id bigint(16) DEFAULT 0,
                              create_user_name VARCHAR ( 128 ) DEFAULT NULL,
                              create_time datetime DEFAULT NULL,
                              remark text DEFAULT NULL comment '备注',
                              PRIMARY KEY ( id ),
                              UNIQUE KEY(username,orgname)
) DEFAULT CHARSET=utf8mb4  comment '用户组织关联';


