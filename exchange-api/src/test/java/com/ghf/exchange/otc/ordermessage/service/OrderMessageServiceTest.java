
package com.ghf.exchange.otc.ordermessage.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.Constants;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.AddAdvertiseReqDTO;
import com.ghf.exchange.otc.advertise.dto.PutOnShelvesReqDTO;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.order.dto.AddOrderReqDTO;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.ordermessage.dto.*;
import com.ghf.exchange.otc.ordermessage.enums.OrderMessageTypeEnum;
import com.ghf.exchange.otc.payment.enums.PaymentTypeEnum;
import com.ghf.exchange.otc.payment.service.PaymentService;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class OrderMessageServiceTest {
    @Lazy
    @Resource
    private AdvertiseService advertisService;

    @Lazy
    @Resource
    private OrderService orderService;

    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private AccountService accountService;

    @Lazy
    @Resource
    private OrderMessageService messageService;

    @Lazy
    @Resource
    private PaymentService paymentService;

    @Lazy
    @Resource
    private CoinService coinService;

    @SneakyThrows
    @Test
    public void addOrderMessage() {

        //清空环境
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //获取币种信息
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //发布广告
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(BigDecimal.valueOf(50.55));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(BigDecimal.valueOf(450000));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("你好，欢迎光临");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //上架广告
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        //新建订单(顾客在广告区买币)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-顾客在广告区买币-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(BigDecimal.valueOf(8));
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());

        addOrderReqDTO.setRemark("顾客在广告区买币");
        orderService.addOrder(addOrderReqDTO);

        //订单消息
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderMessageReqDTO addMessageReqDTO = new AddOrderMessageReqDTO();
        addMessageReqDTO.setOrderMessageCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addMessageReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addMessageReqDTO.setOrderMessageType(OrderMessageTypeEnum.TEXT.getCode());
        addMessageReqDTO.setOrderMessageContent("测试消息");
        messageService.addOrderMessage(addMessageReqDTO);

        PageOrderMessageReqDTO pageMessageReqDTO = new PageOrderMessageReqDTO();
        int total = messageService.pageOrderMessage(pageMessageReqDTO).getData().getTotal();
        Assert.assertTrue(total > 0);

        GetOrderMessageByOrderMessageCodeReqDTO getMessageByMessageCodeReqDTO = new GetOrderMessageByOrderMessageCodeReqDTO();
        getMessageByMessageCodeReqDTO.setOrderMessageCode(addMessageReqDTO.getOrderMessageCode());
        OrderMessageRespDTO messageRespDTO = messageService.getOrderMessageByOrderMessageCode(getMessageByMessageCodeReqDTO).getData();

        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        PageOrderMessageForAdminReqDTO pageMessageForAdminReqDTO = new PageOrderMessageForAdminReqDTO();
        total = messageService.pageOrderMessageForAdmin(pageMessageForAdminReqDTO).getData().getTotal();
        Assert.assertTrue(total > 0);

    }

}
