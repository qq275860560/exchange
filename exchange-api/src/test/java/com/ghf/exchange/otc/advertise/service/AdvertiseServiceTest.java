
package com.ghf.exchange.otc.advertise.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.Constants;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.otc.account.dto.AccountRespDTO;
import com.ghf.exchange.otc.account.dto.GetAccountByUsernameAndCoinCodeReqDTO;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.entity.QAdvertise;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseStatusEnum;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.order.dto.*;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.payment.dto.GetPaymentByPaymentTypeForClientReqDTO;
import com.ghf.exchange.otc.payment.dto.PaymentRespDTO;
import com.ghf.exchange.otc.payment.enums.PaymentTypeEnum;
import com.ghf.exchange.otc.payment.service.PaymentService;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.types.Predicate;
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
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class AdvertiseServiceTest {
    @Lazy
    @Resource
    private AdvertiseService advertisService;

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
    public void addAdvertise() {

        //清空环境
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //获取币种信息
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO = new GetPaymentByPaymentTypeForClientReqDTO();
        getPaymentByPaymentTypeForClientReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getPaymentByPaymentTypeForClientReqDTO.setPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        PaymentRespDTO paymentRespDTO = paymentService.getPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO).getData();

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(BigDecimal.valueOf(50.55));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(BigDecimal.valueOf(450000));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<Integer>(Arrays.asList(paymentRespDTO.getPaymentType())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("你好，欢迎光临");
        addAdvertiseReqDTO.setRemark("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //清理

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        Predicate predicate = QAdvertise.advertise.advertiseCode.eq(addAdvertiseReqDTO.getAdvertiseCode());

        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(putOnShelvesReqDTO.getAdvertiseCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        PutOffShelvesReqDTO putOffShelvesReqDTO = new PutOffShelvesReqDTO();
        putOffShelvesReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertisService.putOffShelves(putOffShelvesReqDTO);

    }

    @SneakyThrows
    @Test
    public void putOffShelvesForClientTest() {

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
        //故意设置成只允许交易一次,然后因为数量不足被自动下架
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(BigDecimal.valueOf(450000));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("你好，欢迎光临");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //判断是否发布成功(状态为下架)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //上架广告
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //判断是否上架成功
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //新建订单(顾客在广告区买币)
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-顾客在广告区买币-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        //故意以币种最小限制购买
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("顾客在广告区买币");
        orderService.addOrder(addOrderReqDTO);

        //判断订单状态是否为下单状态
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());

        //判断广告的冻结的库存是否跟订单交易额相同
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

        //付款
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //判断订单状态是否为付款状态
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //放行
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        ReleaseOrderReqDTO releaseOrderReqDTO = new ReleaseOrderReqDTO();
        releaseOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.releaseOrder(releaseOrderReqDTO);

        //判断订单状态是否为判断放行
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

        //判断广告的冻结的库存是否为0
        getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(BigDecimal.valueOf(0)) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

        //延时
        TimeUnit.MILLISECONDS.sleep(60000);

        //判断广告状态是否为下架（因为可交易数量不满足币种最小限制）
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

    }

}
