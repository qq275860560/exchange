
package com.ghf.exchange.otc.orderappeal.service;

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
import com.ghf.exchange.otc.order.dto.GetOrderByOrderCodeReqDTO;
import com.ghf.exchange.otc.order.dto.OrderRespDTO;
import com.ghf.exchange.otc.order.dto.PayOrderReqDTO;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderappeal.dto.*;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealStatusEnum;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealTypeEnum;
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
public class OrderAppealServiceTest {
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
    private OrderAppealService appealService;
    @Lazy
    @Resource
    private CoinService coinService;
    @Lazy
    @Resource
    private PaymentService paymentService;
    @Value("${maxPayTime}")
    private long maxPayTime;
    @Value("${maxReleaseTime}")
    private long maxReleaseTime;

    @SneakyThrows
    @Test
    public void addOrderAppeal() {

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
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(BigDecimal.valueOf(50.55));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(BigDecimal.valueOf(450000));

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
        addOrderReqDTO.setOrderAmount(BigDecimal.valueOf(8));
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
        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        GetOrderAppealByOrderAppealCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        OrderAppealRespDTO appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //?????????????????????????????????????????????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        AuditOrderAppealForAdminReqDTO auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("????????????");
        appealService.auditOrderAppealForAdmin(auditAppealForAdminReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.FAIL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(3);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //?????????????????????????????????????????????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("????????????");
        appealService.auditOrderAppealForAdmin(auditAppealForAdminReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.FAIL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //????????????????????????
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //??????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        CancelOrderAppealReqDTO cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("???????????????");
        appealService.cancelOrderAppeal(cancelOrderAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.CANCEL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //??????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("???????????????");
        appealService.cancelOrderAppeal(cancelOrderAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.CANCEL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

    }

    @SneakyThrows
    @Test
    public void addOrderAppeal2() {

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
        addAdvertiseReqDTO.setAdvertiseAvailableAmount(BigDecimal.valueOf(50.55));

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(BigDecimal.valueOf(450000));

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
        addOrderReqDTO.setOrderAmount(BigDecimal.valueOf(8));
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
        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        GetOrderAppealByOrderAppealCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        OrderAppealRespDTO appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //?????????????????????????????????????????????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        AuditOrderAppealForAdminReqDTO auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("????????????");
        appealService.auditOrderAppealForAdmin(auditAppealForAdminReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.FAIL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(3);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //?????????????????????????????????????????????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("????????????");
        appealService.auditOrderAppealForAdmin(auditAppealForAdminReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.FAIL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //????????????????????????
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //??????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        CancelOrderAppealReqDTO cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("???????????????");
        appealService.cancelOrderAppeal(cancelOrderAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.CANCEL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-??????-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("?????????");
        appealService.addOrderAppeal(addAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.APPEAL.getCode());

        //??????????????????????????????
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("???????????????");
        appealService.cancelOrderAppeal(cancelOrderAppealReqDTO);

        TimeUnit.SECONDS.sleep(1);
        getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.CANCEL.getCode());
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(appealRespDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

    }

}
