DROP TABLE  IF EXISTS 	t_validate_code ;
CREATE TABLE t_validate_code  (
id bigint(16) NOT NULL,
validate_code_key VARCHAR ( 128 ) DEFAULT NULL comment '验证码的键,唯一',
validate_code_value VARCHAR ( 128 ) DEFAULT NULL comment '验证码的值，对应图片/短信/邮箱验证码的值',
validate_code_type tinyint(1) unsigned DEFAULT 1 comment '验证方式{1:图片,2:短信,3:邮箱,4:滑块}',
validate_code_usage tinyint(4) unsigned DEFAULT 1 comment '用途,1:注册,2:登录,3:忘记密码,4:提交表单',
seconds  tinyint(2) unsigned DEFAULT 60 comment '有效时间，单位秒',
retry_count  tinyint(2) unsigned DEFAULT 60 comment '可重新验证多少次',
execute_count  tinyint(2) unsigned DEFAULT 60 comment '已经验证多少次',
status tinyint(1) unsigned DEFAULT 1 comment '验证码状态{0:未验证,1:验证通过，2:验证不通过}',
cause tinyint(1) unsigned DEFAULT 1 comment '验证不通过原因,0:无，1:不准确，2:超时',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 128 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY(validate_code_key)
) DEFAULT CHARSET=utf8mb4  comment '资源文件';


