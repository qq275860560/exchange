
package com.ghf.exchange.otc.advertisebusiness.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.Constants;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertisebusiness.dto.AddAdvertiseBusinessForClientReqDTO;
import com.ghf.exchange.otc.advertisebusiness.dto.GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.payment.service.PaymentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class AdvertiseBusinessServiceTest {
    @Lazy
    @Resource
    private AdvertiseBusinessService advertiseBusinessService;

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
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(Constants.ADVERTISE_BUSINESS_USER_NAME);
        advertiseBusinessService.updateAdvertiseBusinessOnPutOnShelvesForClient(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    }

    @SneakyThrows
    @Test
    public void addAdvertiseBusinessForClient() {
        AddAdvertiseBusinessForClientReqDTO addAdvertiseBusinessForClientReqDTO = new AddAdvertiseBusinessForClientReqDTO();
        addAdvertiseBusinessForClientReqDTO.setAdvertiseBusinessCode("test-addAdvertiseBusinessForClient-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseBusinessForClientReqDTO.setCountryCode("CN");
        advertiseBusinessService.addAdvertiseBusinessForClient(addAdvertiseBusinessForClientReqDTO);
    }

}
