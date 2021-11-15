/*
 用户（）
 用户的角色英文数组，角色中文数组，组织英文数组，组织中文数组，为冗余字段
 有值的情况下必须在角色表，组织表存在，以逗号分隔，前后都有逗号
 用户角色关联表保存用户和角色的关联关系
 用户组织关联表保存用户和组织的关联关系
 组织角色关联表保存组织和角色的关联关系
 */
INSERT INTO t_user(id,
                   username,
                   password,
                   nickname,
                   realname,
                   rolenames,
                   roledescs,
                   orgnames,
                   orgdescs)
VALUES (1,
        'admin',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        '平台管理员',
        '平台管理员',
        ',ROLE_ADMIN,',
        ',平台管理员,',
        null,
        null);


INSERT INTO t_user(id,
                   username,
                   password,
                   nickname,
                   realname,
                   rolenames,
                   roledescs,
                   orgnames,
                   orgdescs)
VALUES (2,
        'user',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        '普通注册用户',
        '普通注册用户',
        ',ROLE_USER,',
        ',普通注册用户,',
        null,
        null);


INSERT INTO t_user(id,
                   username,
                   password,
                   nickname,
                   realname,
                   rolenames,
                   roledescs,
                   orgnames,
                   orgdescs)
VALUES (3,
        'advertise_business',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        '普通注册商家',
        '普通注册商家',
        ',ROLE_USER,',
        ',普通注册商家,',
        null,
        null);

INSERT INTO t_user(id,
                   username,
                   password,
                   nickname,
                   realname,
                   rolenames,
                   roledescs,
                   orgnames,
                   orgdescs)
VALUES (4,
        'order_customer',
        '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
        '普通注册顾客',
        '普通注册顾客',
        ',ROLE_USER,',
        ',普通注册顾客,',
        null,
        null);

