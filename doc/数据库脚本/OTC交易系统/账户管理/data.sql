/*
账户管理
 */
INSERT INTO t_account (id, username, coin_code, total_balance,available_balance, frozen_balance)
VALUES (1, 'admin', 'BTC',-300000000, -300000000, 0);

INSERT INTO t_account (id, username, coin_code, total_balance,available_balance, frozen_balance)
VALUES (2, 'user', 'BTC',100000000, 100000000, 0);

INSERT INTO t_account (id, username, coin_code, total_balance,available_balance, frozen_balance)
VALUES (3, 'advertise_business', 'BTC', 100000000,100000000, 0);

INSERT INTO t_account (id, username, coin_code,total_balance,available_balance, frozen_balance)
VALUES (4, 'order_customer', 'BTC', 100000000,100000000, 0);