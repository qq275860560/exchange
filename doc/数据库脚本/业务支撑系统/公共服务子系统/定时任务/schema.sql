DROP TABLE IF EXISTS `t_task`;
CREATE TABLE `t_task` (
id bigint(16) NOT NULL,
taskname varchar(128) NOT NULL COMMENT '任务名称，任务英文名称,任务编号，唯一',
taskdesc varchar(128) NOT NULL COMMENT '任务描述，任务中文名称',
tasktype  tinyint(1) unsigned not null DEFAULT 0 comment '任务类型,0:简单任务（直接设置重复重复次数和执行间隔，也就是task_repeat_count和task_interval字段有效）,1:cron任务（根据cron表达式确定执行间隔,也就是cron_expression字段有效）',
task_repeat_count int    DEFAULT 0 COMMENT '重复次数，默认为0,表示任务只执行一次就完成，即使当前时间还未到end_at，任务也完成了',
task_interval int    DEFAULT 60 COMMENT '每次执行间距(单位秒)，必须大于0，默认为60，表示每隔60秒执行一次，该参数在repeat_count>0时并且在还在有效期是有用',
cron_expression varchar(255) DEFAULT NULL COMMENT '时间表达式,tasktype=1时有效',
start_at datetime NOT NULL COMMENT '任务第一次/下一次启动时间,任务有效期的开始时间',
end_at datetime NOT NULL COMMENT '任务最后一次截止时间,任务有效期的截止时间，超过这个时间，即使任务还剩余有执行次数，也会失效',
request_url varchar(256)  NOT NULL COMMENT 'httpUrl链接，局域网内连接，内部服务连接，通常是微服务链接',
request_method varchar(8)  DEFAULT 'POST' COMMENT '请求方法(GET/POST/PUT/DELETE)，默认为POST',
request_header longtext DEFAULT NULL COMMENT '请求头部json格式',
input_json longtext   DEFAULT NULL COMMENT '请求参数json,请求体内容 ',
status           tinyint(4) unsigned not null DEFAULT 0 comment '状态,0:暂停中,1:运行中,2:已完成',
create_user_id bigint(16) DEFAULT 0,
create_user_name VARCHAR ( 128 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
  PRIMARY KEY (`id`),
UNIQUE KEY(taskname)
) DEFAULT CHARSET=utf8mb4  comment '定时任务';


DROP TABLE IF EXISTS `t_task_log`;
CREATE TABLE `t_task_log` (
id bigint(16) NOT NULL,
taskname varchar(100) NOT NULL COMMENT '任务名称，任务英文名称,任务编号,必须在task表存在',
tasklogname varchar(100) NOT NULL COMMENT '任务日志名称,唯一,建议$taskName-yyyyMMddHHmmssSSS',
request_url varchar(256)  NOT NULL COMMENT 'httpUrl链接，局域网内连接，内部服务连接，通常是微服务链接',
request_method varchar(8)  DEFAULT 'POST' COMMENT '请求方法(GET/POST/PUT/DELETE)，默认为POST',
request_header longtext DEFAULT NULL COMMENT '请求头部json格式',
input_json longtext   DEFAULT NULL COMMENT '请求参数json,请求体内容 ',
output_json  longtext DEFAULT NULL COMMENT '输出参数json，响应体内容',
response_status_code int DEFAULT NULL COMMENT '状态码',
response_header longtext DEFAULT NULL COMMENT '响应头部json格式',
start_time bigint DEFAULT NULL comment '开始时间（1970年以来的毫秒数）',
end_time bigint DEFAULT NULL comment '结束时间（1970年以来的毫秒数）',
duration bigint    DEFAULT NULL COMMENT '执行时长(毫秒)',
status bit not null DEFAULT 1 comment '状态;0：失败，1：成功',
create_user_id bigint(16) DEFAULT NULL,
create_user_name VARCHAR ( 64 ) DEFAULT NULL,
create_time datetime DEFAULT NULL,
remark text DEFAULT NULL comment '备注',
is_delete bit not null DEFAULT 0 comment '是否删除;0：正常，1：删除',
gmt_create datetime not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
gmt_modified datetime not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
gmt_delete datetime null comment '删除时间',
  PRIMARY KEY (`id`),
UNIQUE KEY(taskname,tasklogname)
) DEFAULT CHARSET=utf8mb4  comment '定时任务日志';
