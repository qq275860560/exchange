/*
订单顾客
 */
INSERT INTO t_order_customer(id,
                             order_customer_code,
                                 username,
                                 password,
                                 nickname,
                                 realname,
                                 country_code  ,
                                 country_name   ,
                                 legal_currency_code  ,
                                 legal_currency_name   ,
                                 legal_currency_symbol ,
                                 legal_currency_unit)
    VALUES (1,
            'order_customer',
            'order_customer',
            '$2a$10$0MZ3jfd2/7EsB2issDAffODB3035N4duke3cMb55qIUIOpy8n/8cS',
            '普通订单顾客',
            '普通订单顾客' ,
            'CN',
            '中国',
            '￥',
            '人民币',
            '￥',
            '元');

