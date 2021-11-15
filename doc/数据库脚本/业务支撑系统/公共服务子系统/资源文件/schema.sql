DROP TABLE  IF EXISTS 	t_resource_file ;
CREATE TABLE t_resource_file  (
id bigint(16) NOT NULL,
md5 VARCHAR ( 32 ) DEFAULT NULL comment '文件md5',
name VARCHAR ( 256 ) DEFAULT NULL comment '文件名称',
content_type VARCHAR ( 256 ) DEFAULT NULL comment '文件内容类型',
size bigint DEFAULT NULL comment '文件大小，单位字节',
path varchar(256) not null comment '文件路径,前端能直接访问的路径',
slice_count int DEFAULT 1 comment '分片总数',
max_slice_size bigint DEFAULT NULL comment '最大分片大小，单位字节',
resource_usage tinyint(4) unsigned DEFAULT 0 comment '用途,0:普通文件,1:滑块验证码背景图片，默认为0，也就是默认为普通文件',
status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:未上传完毕,1:上传完毕',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 256 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
PRIMARY KEY ( id )
) DEFAULT CHARSET=utf8mb4  comment '资源文件';