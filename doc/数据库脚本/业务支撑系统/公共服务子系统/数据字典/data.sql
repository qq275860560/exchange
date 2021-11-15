/**
数据字典
 */
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (101001,
        'ROLE_NAME_DICT',
        '角色名称字典',
        'ROLE_ADMIN',
        '平台管理员');

INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (101002,
        'ROLE_NAME_DICT',
        '角色名称字典',
        'ROLE_USER',
        '普通注册用户');

INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102001,
        'PERMISSION_DICT',
        '权限类型字典',
        '1',
        '系统');

INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102002,
        'PERMISSION_DICT',
        '权限类型字典',
        '2',
        '子系统');

INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102003,
        'PERMISSION_DICT',
        '权限类型字典',
        '3',
        '模块');

INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102004,
        'PERMISSION_DICT',
        '权限类型字典',
        '4',
        '子模块');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102005,
        'PERMISSION_DICT',
        '权限类型字典',
        '5',
        '一级菜单');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102006,
        'PERMISSION_DICT',
        '权限类型字典',
        '6',
        '二级菜单');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102007,
        'PERMISSION_DICT',
        '权限类型字典',
        '7',
        '三级菜单');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102008,
        'PERMISSION_DICT',
        '权限类型字典',
        '8',
        '页面(组件)');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102009,
        'PERMISSION_DICT',
        '权限类型字典',
        '9',
        '子页面(子组件)');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (102010,
        'PERMISSION_DICT',
        '权限类型字典',
        '10',
        '按钮');





INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (103001,
        'VALIDATE_CODE_TYPE_DICT',
        '验证码类型字典',
        'PICTURE',
        '图片');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (103002,
        'VALIDATE_CODE_TYPE_DICT',
        '验证码类型字典',
        'MESSAGE',
        '短信');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (103003,
        'VALIDATE_CODE_TYPE_DICT',
        '验证码类型字典',
        'EMAIL',
        '邮箱');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (105003,
        'VALIDATE_CODE_TYPE_DICT',
        '验证码类型字典',
        'SLIDER',
        '滑块');


INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (104001,
        'VALIDATE_CODE_USAGE_DICT',
        '验证码用途字典',
        'REGISTER',
        '注册');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (104002,
        'VALIDATE_CODE_USAGE_DICT',
        '验证码用途字典',
        'LOGIN',
        '登录');
INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (104003,
        'VALIDATE_CODE_USAGE_DICT',
        '验证码用途字典',
        'FORGET_PASSWORD',
        '忘记密码');

INSERT INTO t_dict(id,
                   dict_type,
                   dict_type_desc,
                   dict_key,
                   dict_value)
VALUES (104004,
        'VALIDATE_CODE_USAGE_DICT',
        '验证码用途字典',
        'SUBMIT_FORM',
        '提交表单');

