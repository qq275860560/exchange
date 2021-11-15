/*
账户
 */
INSERT INTO exchange.t_account (id, username, coin_code, balance, frozen_balance)
VALUES (1, 'admin', 'BTC', -30000, 0);

INSERT INTO exchange.t_account (id, username, coin_code, balance, frozen_balance)
VALUES (2, 'user', 'BTC', 10000, 0);

INSERT INTO exchange.t_account (id, username, coin_code, balance, frozen_balance)
VALUES (3, 'advertise_business', 'BTC', 10000, 0);

INSERT INTO exchange.t_account (id, username, coin_code, balance, frozen_balance)
VALUES (4, 'order_customer', 'BTC', 10000, 0);