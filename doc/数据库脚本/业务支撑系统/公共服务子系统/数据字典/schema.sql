DROP TABLE  IF EXISTS 	t_dict ;
CREATE TABLE t_dict  (
id bigint(16) NOT NULL,
dict_type varchar(128) NOT NULL COMMENT '字典类型，字典名称，字典英文名称，说明是哪一种业务的字典',
dict_type_desc varchar(128) NOT NULL COMMENT '字典类型描述，字典中文名称，说明是哪一种业务的字典',
dict_key varchar(128) NOT NULL COMMENT '字典key，前端下拉，单选，多选的隐藏值,或者后端枚举类的code，或者后端常量名',
dict_value varchar(1024) NOT NULL COMMENT '字典value,前端下拉，单选，多选的显示值,或者后端枚举类的msg，或者后端常量值',
order_num int DEFAULT 0 COMMENT '排序,指的是同级顺序，拥有同一字典类型时，兄弟间的顺序,0>1>2>3>4,数字小的优先',
status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 128 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id ),
UNIQUE KEY(dict_type,dict_key)
) DEFAULT CHARSET=utf8mb4  comment '数据字典';







