
DROP TABLE  IF EXISTS 	t_area ;
CREATE TABLE t_area  (
                        id bigint(16) NOT NULL,
                        areaname VARCHAR ( 128 ) DEFAULT NULL comment '地区名称,地区英文名称，地区编码，唯一',
                        areadesc VARCHAR ( 128 ) DEFAULT NULL comment '地区描述,地区中文名称，页面显示名称',
                        area_type  tinyint(4) unsigned DEFAULT 0 comment '地区类型,地区级别，默认为0，其中1:一级地区，2：二级地区,3:三级地区，4:四级地区，5:五级地区',
                        order_num  tinyint(4) unsigned DEFAULT 0 comment '排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先',
                        parent_area_id VARCHAR ( 16 ) DEFAULT NULL comment '父级地区id',
                        parent_areaname VARCHAR ( 128 ) DEFAULT NULL comment '父级地区名称,地区英文名称，父地区编码',
                        parent_areadesc VARCHAR ( 128 ) DEFAULT NULL comment '父级地区描述,地区中文名称，页面显示名称',
                        full_area_id VARCHAR ( 512 ) DEFAULT NULL comment '完整地区id，逗号隔开,前后都有逗号',
                        full_areaname VARCHAR ( 512 ) DEFAULT NULL comment '完整地区名称,完整地区英文名称，完整地区编码，逗号隔开,前后都有逗号',
                        full_areadesc VARCHAR ( 512 ) DEFAULT NULL comment '完整地区描述,完整地区中文名称，页面显示的完整名称，逗号隔开,前后都有逗号',
                        deep tinyint(4) unsigned DEFAULT null  comment '当前节点深度,冗余，根节点为1，第二层为为2，第三层为3，以此类推',
                        status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
                        create_user_id bigint(16) DEFAULT 0,
                        create_user_name VARCHAR ( 128 ) DEFAULT NULL,
                        create_time datetime DEFAULT NULL,
                        remark text DEFAULT NULL comment '备注',
                        area_fullname varchar(512)  DEFAULT NULL COMMENT '地区完整全称',
                        area_fullcode varchar(512)  DEFAULT NULL COMMENT '地区完整code',
                        PRIMARY KEY ( id ),
                        UNIQUE KEY(areaname),
                        INDEX index_areadesc(areadesc),
                        INDEX index_parent_areanname(parent_areaname)
) DEFAULT CHARSET=utf8mb4  comment '地区';
