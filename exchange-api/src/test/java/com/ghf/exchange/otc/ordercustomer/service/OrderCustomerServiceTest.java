
package com.ghf.exchange.otc.ordercustomer.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.Constants;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.ordercustomer.dto.GetOrderCustomerByOrderCustomerCodeReqDTO;
import com.ghf.exchange.otc.payment.service.PaymentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class OrderCustomerServiceTest {
    @Lazy
    @Resource
    private OrderCustomerService orderCustomerService;

    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private CoinService coinService;

    @Lazy
    @Resource
    private AccountService accountService;

    @Lazy
    @Resource
    private PaymentService paymentService;

    @Lazy
    @Resource
    private OrderService orderService;

    @SneakyThrows
    @Test
    public void test() {
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(Constants.ORDER_CUSTOMER_USER_NAME);
        orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO);
        log.info("");

    }

}
