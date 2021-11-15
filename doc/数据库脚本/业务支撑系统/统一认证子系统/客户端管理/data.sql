/*
 客户端（比如手机端app，PC端浏览器，手机端浏览器，后台其他tomcat，外部应用）
 */
insert into t_client (id,
                      client_id,
                      client_secret,
                      registered_redirect_uris,
                      authorized_grant_types,
                      scopes,
                      access_token_validity_seconds)
values (1,
        'app',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        'http://www.baidu.com',
        'authorization_code,refresh_token,implicit,password,client_credentials',
        'APP',
        31536000);

insert into t_client (id,
                      client_id,
                      client_secret,
                      registered_redirect_uris,
                      authorized_grant_types,
                      scopes,
                      access_token_validity_seconds)
values (2,
        'browser',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        'http://www.baidu.com',
        'authorization_code,refresh_token,implicit,password,client_credentials',
        'BROWSER',
        31536000);


insert into t_client (id,
                      client_id,
                      client_secret,
                      registered_redirect_uris,
                      authorized_grant_types,
                      scopes,
                      access_token_validity_seconds)
values (8080,
        'exchange-api',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        'http://www.baidu.com',
        'authorization_code,refresh_token,implicit,password,client_credentials',
        'SERVER',
        31536000);