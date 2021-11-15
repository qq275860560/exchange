DROP TABLE IF EXISTS t_client;
CREATE TABLE t_client
(
    id                            bigint(16) NOT NULL,
    client_id                     VARCHAR(128)         DEFAULT NULL comment '客户端id，唯一，相当于oauth2认证码模式的client_id',
    client_secret                      VARCHAR(512)        DEFAULT NULL comment '密码，相当于oauth2认证码模式的client_secret，数据库存储以BCryptPasswordEncoder加密,但认证请求时的密码为明文',
    registered_redirect_uris      text                DEFAULT NULL comment 'url,用于接收code或者access_token，多个请逗号分隔',
    authorized_grant_types        VARCHAR(128)        DEFAULT NULL comment '认证模式,多个请逗号分隔,可选项为authorization_code,refresh_token,implicit,password,client_credentials',
    scopes                        VARCHAR(512)        DEFAULT NULL comment '授权范围，不需要SCOPE_开头,多个请逗号分隔,如果一个url的授权范围不为空，那么登录客户端的授权范围必须大于或等于它，才有权限访问',
    access_token_validity_seconds int                 DEFAULT NULL comment 'token的有效时间,单位秒',
    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_user_id                bigint(16)          DEFAULT 0,
    create_user_name              VARCHAR(128)         DEFAULT NULL,
    create_time                   datetime            DEFAULT NULL,
    remark                        text                DEFAULT NULL comment '备注',
    PRIMARY KEY (id),
    UNIQUE KEY (client_id)
) DEFAULT CHARSET = utf8mb4 comment '客户端';