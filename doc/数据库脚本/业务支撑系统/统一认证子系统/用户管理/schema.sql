DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user
(
    id               bigint(16) NOT NULL,
    username         VARCHAR(64)         DEFAULT NULL comment '登陆用户名,用户英文名称，用户编码，唯一',
    password         VARCHAR(512)        DEFAULT NULL comment '密码，数据库存储以BCryptPasswordEncoder加密,但认证请求时的密码为明文',
    nickname         VARCHAR(16)         DEFAULT NULL comment '用户昵称，用户中文名称，可以随时修改',
    realname         VARCHAR(16)         DEFAULT NULL comment '用户真实姓名',
    mobile           VARCHAR(16)         DEFAULT NULL comment '手机，唯一',
    email            VARCHAR(64)         DEFAULT NULL comment '邮箱，唯一',
    rolenames        VARCHAR(128)        DEFAULT NULL comment '角色名称列表，冗余,逗号隔开，前后都有逗号',
    roledescs        VARCHAR(128)        DEFAULT NULL comment '角色描述列表，冗余,逗号隔开，前后都有逗号',
    orgnames         VARCHAR(128)        DEFAULT NULL comment '组织名称列表，冗余,逗号隔开，前后都有逗号',
    orgdescs         VARCHAR(128)        DEFAULT NULL comment '组织描述列表，冗余,逗号隔开，前后都有逗号',
    last_login_time  datetime            DEFAULT NULL,
    last_login_ip    VARCHAR(32)         DEFAULT NULL,
    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_user_id   bigint(16)          DEFAULT 0,
    create_user_name VARCHAR(64)         DEFAULT NULL,
    create_time      datetime            DEFAULT NULL,
    remark           text                DEFAULT NULL comment '备注',
    PRIMARY KEY (id),
    UNIQUE KEY (username),
    UNIQUE KEY (mobile),
    UNIQUE KEY (email)
) DEFAULT CHARSET = utf8mb4 comment '用户';


