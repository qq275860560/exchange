
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

        //订单顾客付款
        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //超过最大放行时间
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //订单顾客申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("未放行");
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

        //管理员审核申诉，不同意，也就是恢复申诉前的状态
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        AuditOrderAppealForAdminReqDTO auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("处理完毕");
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

        //广告商家申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("未付款");
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

        //管理员审核申诉，不同意，也就是恢复申诉前的状态
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("处理完毕");
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

        //超过最大放行时间
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //订单顾客申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("未放行");
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

        //订单顾客自己取消申诉
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        CancelOrderAppealReqDTO cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("已处理完毕");
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

        //广告商家申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("未付款");
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

        //广告商家自己取消申诉
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("已处理完毕");
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

        //订单顾客付款
        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //超过最大放行时间
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //订单顾客申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("未放行");
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

        //管理员审核申诉，不同意，也就是恢复申诉前的状态
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        AuditOrderAppealForAdminReqDTO auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("处理完毕");
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

        //广告商家申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("未付款");
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

        //管理员审核申诉，不同意，也就是恢复申诉前的状态
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());
        auditAppealForAdminReqDTO = new AuditOrderAppealForAdminReqDTO();
        auditAppealForAdminReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealForAdminReqDTO.setStatus(OrderAppealStatusEnum.FAIL.getCode());
        auditAppealForAdminReqDTO.setOrderAppealAuditResult("处理完毕");
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

        //超过最大放行时间
        TimeUnit.MILLISECONDS.sleep(maxReleaseTime + 3000);
        //订单顾客申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_RELEASE.getCode());
        addAppealReqDTO.setOrderAppealContent("未放行");
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

        //订单顾客自己取消申诉
        userService.login(LoginReqDTO.builder().username(Constants.ORDER_CUSTOMER_USER_NAME).password(Constants.ORDER_CUSTOMER_PASSWORD).build());
        CancelOrderAppealReqDTO cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("已处理完毕");
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

        //广告商家申诉订单
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealType(OrderAppealTypeEnum.UN_PAY.getCode());
        addAppealReqDTO.setOrderAppealContent("未付款");
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

        //广告商家自己取消申诉
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());
        cancelOrderAppealReqDTO = new CancelOrderAppealReqDTO();
        cancelOrderAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        cancelOrderAppealReqDTO.setOrderAppealCancelResult("已处理完毕");
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
