
package com.ghf.exchange.otc.order.service;

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
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseStatusEnum;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.order.dto.*;
import com.ghf.exchange.otc.order.enums.OrderBuySellTypeEnum;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.orderappeal.dto.AddOrderAppealReqDTO;
import com.ghf.exchange.otc.orderappeal.dto.AuditOrderAppealForAdminReqDTO;
import com.ghf.exchange.otc.orderappeal.dto.GetOrderAppealByOrderAppealCodeReqDTO;
import com.ghf.exchange.otc.orderappeal.dto.OrderAppealRespDTO;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealStatusEnum;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealTypeEnum;
import com.ghf.exchange.otc.orderappeal.service.OrderAppealService;
import com.ghf.exchange.otc.payment.enums.PaymentTypeEnum;
import com.ghf.exchange.otc.payment.service.PaymentService;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
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
public class OrderServiceTest {
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
    private CoinService coinService;

    @Lazy
    @Resource
    private AccountService accountService;

    @Lazy
    @Resource
    private PaymentService paymentService;

    @Lazy
    @Resource
    private OrderAppealService appealService;

    @Lazy
    @Resource
    private ApplicationContext applicationContext;

    @Value("${maxPayTime}")
    private long maxPayTime;
    @Value("${maxReleaseTime}")
    private long maxReleaseTime;

    @SneakyThrows
    @Test
    public void addOrder() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(putOnShelvesReqDTO.getAdvertiseCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());

        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //??????????????????
        PageOrderReqDTO pageOrderReqDTO = new PageOrderReqDTO();
        PageRespDTO<OrderRespDTO> orderRespDTOPageResult = orderService.pageOrder(pageOrderReqDTO).getData();
        total = page.getTotal();
        Assert.assertTrue(total > 0);

        //????????????????????????????????????????????????????????????????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(orderRespDTO.getOrderCoinCode());
        accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(orderRespDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) > 0);

        //??????

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        //??????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        ReleaseOrderReqDTO releaseOrderReqDTO = new ReleaseOrderReqDTO();
        releaseOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.releaseOrder(releaseOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

    }

    @SneakyThrows
    @Test
    public void addOrder2() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //???????????????????????????????????????
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(putOnShelvesReqDTO.getAdvertiseCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode());
        addOrderReqDTO.setOrderBuySellType(OrderBuySellTypeEnum.BUY.getCode());
        addOrderReqDTO.setOrderCoinCode("BTC");

        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());

        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //????????????????????????????????????????????????????????????????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(orderRespDTO.getOrderCoinCode());
        accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(orderRespDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) > 0);

        //??????

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        //??????

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        ReleaseOrderReqDTO releaseOrderReqDTO = new ReleaseOrderReqDTO();
        releaseOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.releaseOrder(releaseOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

    }

    @SneakyThrows
    @Test
    public void addOrder3() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());

        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //??????????????????
        PageOrderReqDTO pageOrderReqDTO = new PageOrderReqDTO();
        PageRespDTO<OrderRespDTO> orderRespDTOPageResult = orderService.pageOrder(pageOrderReqDTO).getData();
        total = page.getTotal();
        Assert.assertTrue(total > 0);

        //??????????????????????????????????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ORDER_CUSTOMER_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(orderRespDTO.getOrderCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        //??????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        //??????

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        ReleaseOrderReqDTO releaseOrderReqDTO = new ReleaseOrderReqDTO();
        releaseOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.releaseOrder(releaseOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

    }

    @SneakyThrows
    @Test
    public void addOrder4() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        //??????????????????
        PageOrderReqDTO pageOrderReqDTO = new PageOrderReqDTO();
        PageRespDTO<OrderRespDTO> orderRespDTOPageResult = orderService.pageOrder(pageOrderReqDTO).getData();
        total = page.getTotal();
        Assert.assertTrue(total > 0);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode());
        addOrderReqDTO.setOrderBuySellType(OrderBuySellTypeEnum.SELL.getCode());
        addOrderReqDTO.setOrderCoinCode("BTC");

        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());

        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //??????????????????????????????????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ORDER_CUSTOMER_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(orderRespDTO.getOrderCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        //??????

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        //??????

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        ReleaseOrderReqDTO releaseOrderReqDTO = new ReleaseOrderReqDTO();
        releaseOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.releaseOrder(releaseOrderReqDTO);

        //????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

    }

    @SneakyThrows
    @Test
    public void cancelOrder() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(putOnShelvesReqDTO.getAdvertiseCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());

        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //??????????????????
        PageOrderReqDTO pageOrderReqDTO = new PageOrderReqDTO();
        PageRespDTO<OrderRespDTO> orderRespDTOPageResult = orderService.pageOrder(pageOrderReqDTO).getData();
        total = page.getTotal();
        Assert.assertTrue(total > 0);

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) > 0);

        //?????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());

        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        cancelOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.cancelOrder(cancelOrderReqDTO);

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

    }

    @SneakyThrows
    @Test
    public void cancelOrderForClient() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //??????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //????????????(????????????????????????)
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //????????????????????????
        getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) > 0);

        //??????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());

        //?????????????????????????????????????????????????????????????????????
        TimeUnit.MILLISECONDS.sleep(maxPayTime + 20000);

        //??????????????????
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.CANCEL.getCode());

        //???????????????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());

    }

    @SneakyThrows
    @Test
    public void agreeUnPayOrderAppealForClientTest() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //??????????????????
        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(3);
        GetOrderAppealByOrderAppealCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        OrderAppealRespDTO appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //??????????????????????????????????????????????????????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        AuditOrderAppealForAdminReqDTO auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.SUCCESS.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("????????????");
        appealService.auditOrderAppealForAdmin(auditAppealForAdminReqDTO);

        TimeUnit.SECONDS.sleep(3);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.SUCCESS.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());

    }

    @SneakyThrows
    @Test
    public void agreeUnReleaseOrderAppealForClientTest() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????(????????????????????????)

        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("????????????????????????");
        orderService.addOrder(addOrderReqDTO);

        //??????????????????
        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //????????????????????????
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //???????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(3);
        GetOrderAppealByOrderAppealCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        OrderAppealRespDTO appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //???????????????????????????????????????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        AuditOrderAppealForAdminReqDTO auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.SUCCESS.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("????????????");
        appealService.auditOrderAppealForAdmin(auditAppealForAdminReqDTO);

        TimeUnit.SECONDS.sleep(3);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.SUCCESS.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

    }

    //????????????????????????,????????????????????????
    @SneakyThrows
    @Test
    public void fixedAdvertise_advertiseOrder_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("????????????????????????,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

    }

    //????????????????????????,????????????????????????
    @SneakyThrows
    @Test
    public void fixedAdvertise_oneKeyOrder_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().add(BigDecimal.valueOf(888)));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode());
        addOrderReqDTO.setOrderCoinCode(coinRespDTO.getCoinCode());
        addOrderReqDTO.setOrderBuySellType(OrderBuySellTypeEnum.BUY.getCode());
        addOrderReqDTO.setRemark("????????????????????????,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

    }

    //????????????????????????,????????????????????????
    @SneakyThrows
    @Test
    public void premiumAdvertise_advertiseOrder_Test() {
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO.setAdvertisePremiumRate(BigDecimal.valueOf(0.01));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));

        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //????????????(????????????????????????)
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("????????????????????????,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).subtract(advertiseRespDTO.getAdvertisePremiumRate()));
        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

    }

    //????????????????????????,????????????????????????
    @SneakyThrows
    @Test
    public void premiumAdvertise_oneKeyOrder_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO.setAdvertisePremiumRate(BigDecimal.valueOf(0.01));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode());
        addOrderReqDTO.setOrderCoinCode(coinRespDTO.getCoinCode());
        addOrderReqDTO.setOrderBuySellType(OrderBuySellTypeEnum.BUY.getCode());
        addOrderReqDTO.setRemark("????????????????????????,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).subtract(advertiseRespDTO.getAdvertisePremiumRate()));
        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

    }

    //??????????????????,??????????????????????????????
    @SneakyThrows
    @Test
    public void fixedpreminuAdvertise_advertiseOrder_buy_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //////////////////////////////////////////////////////////////////
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //////////////////////////////////////////////////////////////////

        //????????????2
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO2 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO2.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO2.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO2.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO2.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO2.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO2.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO2.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO2.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO2.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO2);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO2 = ModelMapperUtil.map(addAdvertiseReqDTO2, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO2);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        ///////////////////////////////////////////////////////////////////
        //????????????3
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO3 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO3.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO3.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO3.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO3.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO3.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO3.setAdvertisePremiumRate(BigDecimal.valueOf(0.01));
        addAdvertiseReqDTO3.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO3.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO3.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO3);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO3 = ModelMapperUtil.map(addAdvertiseReqDTO3, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO3);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////

        //????????????4
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO4 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO4.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO4.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO4.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO4.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO4.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO4.setAdvertisePremiumRate(BigDecimal.valueOf(0.09));
        addAdvertiseReqDTO4.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO4.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO4.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO4);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO4 = ModelMapperUtil.map(addAdvertiseReqDTO4, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO4);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????1
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("??????????????????1,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice = null;

        if (orderRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO.getAdvertisePremiumRate()));
            } else {
                orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????2
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO2 = new AddOrderReqDTO();
        addOrderReqDTO2.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO2.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO2.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO2.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        addOrderReqDTO2.setRemark("??????????????????2,????????????????????????");

        orderService.addOrder(addOrderReqDTO2);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO2 = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO2.setOrderCode(addOrderReqDTO2.getOrderCode());
        OrderRespDTO orderRespDTO2 = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO2).getData();
        Assert.assertTrue(orderRespDTO2.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice2 = null;

        if (orderRespDTO2.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice2 = advertiseRespDTO2.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO2.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice2 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO2.getAdvertisePremiumRate()));
            } else {
                orderPrice2 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO2.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice2 = orderPrice2.multiply(orderRespDTO2.getOrderAmount());
        Assert.assertTrue(orderRespDTO2.getOrderTotalPrice().compareTo(orderTotalPrice2) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO2.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO2.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO2.getAdvertiseTotalAmount().subtract(advertiseRespDTO2.getAdvertiseFrozenAmount())) == 0);

        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????3
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO3 = new AddOrderReqDTO();
        addOrderReqDTO3.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO3.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO3.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO3.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        addOrderReqDTO3.setRemark("??????????????????3,????????????????????????");

        orderService.addOrder(addOrderReqDTO3);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO3 = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO3.setOrderCode(addOrderReqDTO3.getOrderCode());
        OrderRespDTO orderRespDTO3 = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO3).getData();
        Assert.assertTrue(orderRespDTO3.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice3 = null;

        if (orderRespDTO3.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice3 = advertiseRespDTO3.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO3.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice3 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO3.getAdvertisePremiumRate()));
            } else {
                orderPrice3 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO3.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice3 = orderPrice3.multiply(orderRespDTO3.getOrderAmount());
        Assert.assertTrue(orderRespDTO3.getOrderTotalPrice().compareTo(orderTotalPrice3) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO3.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO3.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO3.getAdvertiseTotalAmount().subtract(advertiseRespDTO3.getAdvertiseFrozenAmount())) == 0);
/////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????4
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO4 = new AddOrderReqDTO();
        addOrderReqDTO4.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO4.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO4.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO4.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        addOrderReqDTO4.setRemark("??????????????????4,????????????????????????");

        orderService.addOrder(addOrderReqDTO4);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO4 = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO4.setOrderCode(addOrderReqDTO4.getOrderCode());
        OrderRespDTO orderRespDTO4 = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO4).getData();
        Assert.assertTrue(orderRespDTO4.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice4 = null;

        if (orderRespDTO4.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice4 = advertiseRespDTO4.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO4.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice4 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO4.getAdvertisePremiumRate()));
            } else {
                orderPrice4 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO4.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice4 = orderPrice4.multiply(orderRespDTO4.getOrderAmount());
        Assert.assertTrue(orderRespDTO4.getOrderTotalPrice().compareTo(orderTotalPrice4) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO4.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO4.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO4.getAdvertiseTotalAmount().subtract(advertiseRespDTO4.getAdvertiseFrozenAmount())) == 0);

    }

    //??????????????????,??????????????????????????????
    @SneakyThrows
    @Test
    public void fixedpreminuAdvertise_onekeyOrder_buy_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //////////////////////////////////////////////////////////////////
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //////////////////////////////////////////////////////////////////

        //????????????2
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO2 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO2.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO2.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO2.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO2.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO2.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO2.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO2.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO2.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO2.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO2);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO2 = ModelMapperUtil.map(addAdvertiseReqDTO2, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO2);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        ///////////////////////////////////////////////////////////////////
        //????????????3
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO3 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO3.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO3.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO3.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO3.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO3.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO3.setAdvertisePremiumRate(BigDecimal.valueOf(0.01));
        addAdvertiseReqDTO3.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO3.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO3.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO3);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO3 = ModelMapperUtil.map(addAdvertiseReqDTO3, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO3);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////

        //????????????4
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO4 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO4.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO4.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO4.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO4.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO4.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO4.setAdvertisePremiumRate(BigDecimal.valueOf(0.09));
        addAdvertiseReqDTO4.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO4.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO4.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO4);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO4 = ModelMapperUtil.map(addAdvertiseReqDTO4, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO4);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????1
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode());
        addOrderReqDTO.setOrderCoinCode(coinRespDTO.getCoinCode());
        addOrderReqDTO.setOrderBuySellType(OrderBuySellTypeEnum.BUY.getCode());

        addOrderReqDTO.setRemark("??????????????????,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());

        AdvertiseRespDTO result = advertiseRespDTO;
        BigDecimal orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
        if (addAdvertiseReqDTO2.getAdvertiseFixedPrice().compareTo(orderPrice) < 0) {
            result = advertiseRespDTO2;
            orderPrice = addAdvertiseReqDTO2.getAdvertiseFixedPrice();
        }
        if (coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(addAdvertiseReqDTO3.getAdvertisePremiumRate())).compareTo(orderPrice) < 0) {
            result = advertiseRespDTO3;
            orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(addAdvertiseReqDTO3.getAdvertisePremiumRate()));
        }
        if (coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(addAdvertiseReqDTO4.getAdvertisePremiumRate())).compareTo(orderPrice) < 0) {
            result = advertiseRespDTO4;
            orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(addAdvertiseReqDTO4.getAdvertisePremiumRate()));
        }

        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(result.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

    }

    //??????????????????,??????????????????????????????
    @SneakyThrows
    @Test
    public void fixedpreminuAdvertise_advertiseOrder_sell_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //////////////////////////////////////////////////////////////////
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //////////////////////////////////////////////////////////////////

        //????????????2
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO2 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO2.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO2.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO2.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO2.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO2.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO2.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO2.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO2.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO2.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO2);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO2 = ModelMapperUtil.map(addAdvertiseReqDTO2, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO2);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        ///////////////////////////////////////////////////////////////////
        //????????????3
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO3 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO3.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO3.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO3.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO3.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO3.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO3.setAdvertisePremiumRate(BigDecimal.valueOf(0.01));
        addAdvertiseReqDTO3.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO3.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO3.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO3);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO3 = ModelMapperUtil.map(addAdvertiseReqDTO3, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO3);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////

        //????????????4
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO4 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO4.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO4.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO4.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO4.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO4.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO4.setAdvertisePremiumRate(BigDecimal.valueOf(0.09));
        addAdvertiseReqDTO4.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO4.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO4.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO4);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO4 = ModelMapperUtil.map(addAdvertiseReqDTO4, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO4);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????1
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        addOrderReqDTO.setRemark("??????????????????1,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice = null;

        if (orderRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO.getAdvertisePremiumRate()));
            } else {
                orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());
        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????2
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO2 = new AddOrderReqDTO();
        addOrderReqDTO2.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO2.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO2.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO2.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        addOrderReqDTO2.setRemark("??????????????????2,????????????????????????");

        orderService.addOrder(addOrderReqDTO2);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO2 = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO2.setOrderCode(addOrderReqDTO2.getOrderCode());
        OrderRespDTO orderRespDTO2 = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO2).getData();
        Assert.assertTrue(orderRespDTO2.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice2 = null;

        if (orderRespDTO2.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice2 = advertiseRespDTO2.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO2.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice2 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO2.getAdvertisePremiumRate()));
            } else {
                orderPrice2 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO2.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice2 = orderPrice2.multiply(orderRespDTO2.getOrderAmount());
        Assert.assertTrue(orderRespDTO2.getOrderTotalPrice().compareTo(orderTotalPrice2) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO2.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO2.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO2.getAdvertiseTotalAmount().subtract(advertiseRespDTO2.getAdvertiseFrozenAmount())) == 0);

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());
        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????3
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO3 = new AddOrderReqDTO();
        addOrderReqDTO3.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO3.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO3.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO3.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        addOrderReqDTO3.setRemark("??????????????????3,????????????????????????");

        orderService.addOrder(addOrderReqDTO3);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO3 = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO3.setOrderCode(addOrderReqDTO3.getOrderCode());
        OrderRespDTO orderRespDTO3 = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO3).getData();
        Assert.assertTrue(orderRespDTO3.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice3 = null;

        if (orderRespDTO3.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice3 = advertiseRespDTO3.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO3.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice3 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO3.getAdvertisePremiumRate()));
            } else {
                orderPrice3 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO3.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice3 = orderPrice3.multiply(orderRespDTO3.getOrderAmount());
        Assert.assertTrue(orderRespDTO3.getOrderTotalPrice().compareTo(orderTotalPrice3) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO3.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO3.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO3.getAdvertiseTotalAmount().subtract(advertiseRespDTO3.getAdvertiseFrozenAmount())) == 0);

        //??????
        Assert.assertTrue(accountService.checkAccountForClientForClient().getData());
        /////////////////////////////////////////////////////////////////
        orderService.deleteAllOrderForAdmin();
        //????????????4
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO4 = new AddOrderReqDTO();
        addOrderReqDTO4.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO4.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO4.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO4.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        addOrderReqDTO4.setRemark("??????????????????4,????????????????????????");

        orderService.addOrder(addOrderReqDTO4);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO4 = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO4.setOrderCode(addOrderReqDTO4.getOrderCode());
        OrderRespDTO orderRespDTO4 = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO4).getData();
        Assert.assertTrue(orderRespDTO4.getStatus() == OrderStatusEnum.ADD.getCode());
        BigDecimal orderPrice4 = null;

        if (orderRespDTO4.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
            orderPrice4 = advertiseRespDTO4.getAdvertiseFixedPrice();
        } else {
            if (orderRespDTO4.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                orderPrice4 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.subtract(advertiseRespDTO4.getAdvertisePremiumRate()));
            } else {
                orderPrice4 = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(advertiseRespDTO4.getAdvertisePremiumRate()));
            }
        }
        BigDecimal orderTotalPrice4 = orderPrice4.multiply(orderRespDTO4.getOrderAmount());
        Assert.assertTrue(orderRespDTO4.getOrderTotalPrice().compareTo(orderTotalPrice4) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO4.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO4.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO4.getAdvertiseTotalAmount().subtract(advertiseRespDTO4.getAdvertiseFrozenAmount())) == 0);

    }

    //??????????????????,??????????????????????????????
    @SneakyThrows
    @Test
    public void fixedpreminuAdvertise_onekeyOrder_sell_Test() {

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        orderService.deleteAllOrderForAdmin();
        advertisService.deleteAllAdvertiseForAdmin();
        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode("BTC");
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //////////////////////////////////////////////////////////////////
        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO = ModelMapperUtil.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        //////////////////////////////////////////////////////////////////

        //????????????2
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO2 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO2.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO2.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO2.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO2.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO2.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO2.setAdvertiseFixedPrice(coinRespDTO.getMarketPrice().multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(0.05))));
        addAdvertiseReqDTO2.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO2.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO2.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO2);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO2 = ModelMapperUtil.map(addAdvertiseReqDTO2, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO2);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO2 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO2.setAdvertiseCode(addAdvertiseReqDTO2.getAdvertiseCode());
        advertiseRespDTO2 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO2).getData();
        Assert.assertTrue(advertiseRespDTO2.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());

        ///////////////////////////////////////////////////////////////////
        //????????????3
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO3 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO3.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO3.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO3.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO3.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO3.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO3.setAdvertisePremiumRate(BigDecimal.valueOf(0.01));
        addAdvertiseReqDTO3.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO3.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO3.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO3);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO3 = ModelMapperUtil.map(addAdvertiseReqDTO3, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO3);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO3 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO3.setAdvertiseCode(addAdvertiseReqDTO3.getAdvertiseCode());
        advertiseRespDTO3 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO3).getData();
        Assert.assertTrue(advertiseRespDTO3.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////

        //????????????4
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        AddAdvertiseReqDTO addAdvertiseReqDTO4 = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO4.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAdvertiseReqDTO4.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.BUY.getCode());
        addAdvertiseReqDTO4.setAdvertiseCoinCode(coinRespDTO.getCoinCode());
        addAdvertiseReqDTO4.setAdvertiseAvailableAmount(coinRespDTO.getPerOrderMinAmount().multiply(BigDecimal.valueOf(1.5)));

        addAdvertiseReqDTO4.setAdvertisePriceType(AdvertisePriceTypeEnum.PREMIUM.getCode());
        addAdvertiseReqDTO4.setAdvertisePremiumRate(BigDecimal.valueOf(0.09));
        addAdvertiseReqDTO4.setAdvertiseBusinessPaymentTypeSet(new HashSet<>(Arrays.asList(PaymentTypeEnum.ALIPAY.getCode())));
        addAdvertiseReqDTO4.setAdvertiseAutoReplyContent("?????????????????????");
        addAdvertiseReqDTO4.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        advertisService.addAdvertise(addAdvertiseReqDTO4);

        //????????????????????????(???????????????)
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        PutOnShelvesReqDTO putOnShelvesReqDTO4 = ModelMapperUtil.map(addAdvertiseReqDTO4, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO4);

        //????????????????????????
        getAdvertiseByAdvertiseCodeReqDTO4 = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO4.setAdvertiseCode(addAdvertiseReqDTO4.getAdvertiseCode());
        advertiseRespDTO4 = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO4).getData();
        Assert.assertTrue(advertiseRespDTO4.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        /////////////////////////////////////////////////////////////////

        orderService.deleteAllOrderForAdmin();
        //????????????1
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-????????????????????????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrderReqDTO.setOrderAmount(coinRespDTO.getPerOrderMinAmount());
        addOrderReqDTO.setOrderCustomerPaymentType(PaymentTypeEnum.ALIPAY.getCode());

        addOrderReqDTO.setOrderSource(OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode());
        addOrderReqDTO.setOrderCoinCode(coinRespDTO.getCoinCode());
        addOrderReqDTO.setOrderBuySellType(OrderBuySellTypeEnum.SELL.getCode());

        addOrderReqDTO.setRemark("??????????????????,????????????????????????");

        orderService.addOrder(addOrderReqDTO);

        //???????????????????????????????????????
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode());

        AdvertiseRespDTO result = advertiseRespDTO;
        BigDecimal orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
        if (addAdvertiseReqDTO2.getAdvertiseFixedPrice().compareTo(orderPrice) > 0) {
            result = advertiseRespDTO2;
            orderPrice = addAdvertiseReqDTO2.getAdvertiseFixedPrice();
        }
        if (coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(addAdvertiseReqDTO3.getAdvertisePremiumRate())).compareTo(orderPrice) > 0) {
            result = advertiseRespDTO3;
            orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(addAdvertiseReqDTO3.getAdvertisePremiumRate()));
        }
        if (coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(addAdvertiseReqDTO4.getAdvertisePremiumRate())).compareTo(orderPrice) > 0) {
            result = advertiseRespDTO4;
            orderPrice = coinRespDTO.getMarketPrice().multiply(BigDecimal.ONE.add(addAdvertiseReqDTO4.getAdvertisePremiumRate()));
        }

        BigDecimal orderTotalPrice = orderPrice.multiply(orderRespDTO.getOrderAmount());
        Assert.assertTrue(orderRespDTO.getOrderTotalPrice().compareTo(orderTotalPrice) == 0);

        //????????????????????????????????????????????????????????????
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(result.getAdvertiseCode());
        advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(addOrderReqDTO.getOrderAmount()) == 0);
        Assert.assertTrue(advertiseRespDTO.getAdvertiseAvailableAmount().compareTo(advertiseRespDTO.getAdvertiseTotalAmount().subtract(advertiseRespDTO.getAdvertiseFrozenAmount())) == 0);

    }

}
