DROP TABLE IF EXISTS t_order_customer;
CREATE TABLE t_order_customer
(
    id               bigint(16) NOT NULL,
    order_customer_code           VARCHAR(32)         not null comment '订单顾客编码，默认和用户名相同',
    username         VARCHAR(64)         DEFAULT NULL comment '登陆用户名,用户英文名称，用户编码，唯一',
    password         VARCHAR(512)        DEFAULT NULL comment '安全密码，此密码跟登陆操作密码不同，下单和付款等操作可能需要认证',
    nickname         VARCHAR(16)         DEFAULT NULL comment '用户昵称，用户中文名称，可以随时修改',
    realname         VARCHAR(16)         DEFAULT NULL comment '用户真实姓名',
    mobile           VARCHAR(16)         DEFAULT NULL comment '手机，唯一',
    email           VARCHAR(16)         DEFAULT NULL comment '邮箱，唯一',
    country_code           VARCHAR(16)         DEFAULT NULL comment '国家编码',
    country_name           VARCHAR(16)         DEFAULT NULL comment '国家名称',
    legal_currency_code   VARCHAR(16)         DEFAULT NULL comment '法币编码',
    legal_currency_name   VARCHAR(16)         DEFAULT NULL comment '法币名称',
    legal_currency_symbol        VARCHAR(16)   DEFAULT NULL comment '法币符号',
    legal_currency_unit        VARCHAR(16)   DEFAULT NULL comment '法币单位',
    order_buy_sell_count int DEFAULT 0 comment '发布广告后，订单顾客下单次数',
    order_buy_count int DEFAULT 0 comment '发布卖币广告后，买币订单次数,也就是卖币广告被下单的次数',
    order_sell_count int DEFAULT 0 comment '发布买币广告后，卖币订单次数,也就是买币广告被下单的次数',
    order_buy_release_count int DEFAULT 0 comment '发布卖币广告后，买币订单放行次数,,也就是订单最终成功的次数',
    order_sell_release_count int DEFAULT 0 comment '发布买币广告后，卖币订单放行次数,,也就是订单最终成功的次数',

    order_buy_release_rate DECIMAL(18,2) DEFAULT 0 comment '买总完成率=买币订单放行次数/订单顾客买币订单下单次数',
    order_buy_sell_release_rate DECIMAL(18,2) DEFAULT 0 comment '总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数',
    order_buy_total_release_time bigint(16)  DEFAULT 0 comment '买币订单累计放行时间，买币订单从下单到放行的时间',
    order_sell_total_release_time bigint(16)  DEFAULT 0 comment '卖币订单累计放行时间，卖币订单从下单到放行的时间',
    order_buy_sell_avg_release_time bigint(16)  DEFAULT 0 comment '订单平均放行时间，订单从下单到放行的平均时间',
    order_today_appeal_count int DEFAULT 0 comment '订单当日申诉次数',
    order_last_appeal_time     datetime            DEFAULT NULL  comment '订单最后一次申诉时间',
    order_today_cancel_count int DEFAULT 0 comment '订单当日取消次数，如果大于等于系统规定的取消次数就禁止当天继续下单',
    order_last_cancel_time     datetime            DEFAULT NULL  comment '订单最后一次取消时间',

    order_month_buy_sell_count int DEFAULT 0 comment '订单当月下单总数',
    order_month_buy_sell_release_count int DEFAULT 0 comment '订单当月放行总数',
    order_month_buy_sell_release_rate DECIMAL(18,2) DEFAULT 0 comment '订单当月放行比例',
    order_sell_total_price DECIMAL(18,2) DEFAULT 0 comment '卖币订单总额',
    order_buy_total_price DECIMAL(18,2) DEFAULT 0 comment '买币订单总额',
    order_last_add_time     datetime            DEFAULT NULL  comment '订单最后下单时间',
    order_last_release_time     datetime            DEFAULT NULL  comment '订单最后放行时间',

    status           tinyint(1) unsigned not null DEFAULT 1 comment '状态,0:禁用,1:启用',
    create_time      datetime            DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (username)
) DEFAULT CHARSET = utf8mb4 comment '订单顾客';


