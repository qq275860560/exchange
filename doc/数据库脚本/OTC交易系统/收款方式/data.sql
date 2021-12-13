/*
收款方式
 */

INSERT INTO t_payment (id, payment_code, username, payment_type, payment_type_alipay_account, payment_type_alipay_qrcode)
VALUES
       (1, 'advertise_business_alipay', 'advertise_business', '1', 'advertise_business_alipay_account', 'advertise_business_alipay_qrcode');
INSERT INTO t_payment (id, payment_code, username, payment_type, payment_type_alipay_account, payment_type_alipay_qrcode)
 VALUES
        (2, 'order_customer_alipay', 'order_customer', '1', 'order_customer_alipay_yaccount', 'order_customer_alipay_qrcode');