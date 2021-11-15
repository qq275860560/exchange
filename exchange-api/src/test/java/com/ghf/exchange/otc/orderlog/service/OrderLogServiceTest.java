
package com.ghf.exchange.otc.orderlog.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.otc.account.dto.AccountRespDTO;
import com.ghf.exchange.otc.account.dto.GetAccountByUsernameAndCoinCodeReqDTO;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBusinessPaymentTermTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.order.dto.*;
import com.ghf.exchange.otc.order.enums.OrderCustomerPaymentTermTypeEnum;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderlog.dto.OrderLogRespDTO;
import com.ghf.exchange.otc.orderlog.dto.PageOrderLogReqDTO;
import com.ghf.exchange.util.AutoMapUtils;
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

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class OrderLogServiceTest {
    @Lazy
    @Resource
    private AdvertiseService advertisService;

    @Lazy
    @Resource
    private OrderService orderService;

    @Lazy
    @Resource
    private OrderLogService orderLogService;

    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private AccountService accountService;

    /**
     * 商家账号（测试）
     */
    public static final String BUSINESS_USER_NAME = "advertise_business";

    /**
     * 商家账号（测试）
     */
    public static final String BUSINESS_PASSWORD = "123456";

    /**
     * 顾客账号（测试）
     */
    public static final String CUSTOMER_USER_NAME = "order_customer";

    /**
     * 顾客密码（测试）
     */
    public static final String CUSTOMER_PASSWORD = "123456";

    /**
     * 平台管理员账号（测试）
     */
    public static final String ADMIN_USER_NAME = "admin";

    /**
     * 平台管理员密码（测试）
     */
    public static final String ADMIN_PASSWORD = "123456";

    /**
     * 内部服务器账号（测试）
     */
    public static final String SERVER_CLIENTID = "exchange-api";

    /**
     * 内部服务器密码（测试）
     */
    public static final String SERVER_SECRET = "123456";

    @SneakyThrows
    @Test
    public void addOrder() {

        userService.login(LoginReqDTO.builder().username(BUSINESS_USER_NAME).password(BUSINESS_PASSWORD).build());

        AddAdvertiseReqDTO addAdvertiseReqDTO = new AddAdvertiseReqDTO();
        addAdvertiseReqDTO.setAdvertiseCode("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        addAdvertiseReqDTO.setAdvertiseBuySellType(AdvertiseBuySellTypeEnum.SELL.getCode());
        addAdvertiseReqDTO.setAdvertiseCoinCode("BTC");
        addAdvertiseReqDTO.setAdvertiseAmount(BigDecimal.valueOf(50.55));

        addAdvertiseReqDTO.setAdvertiseLegalCurrencyCountryCode("CN");
        addAdvertiseReqDTO.setAdvertiseLegalCurrencySymbol("￥");
        addAdvertiseReqDTO.setAdvertiseLegalCurrencyUnit("元");

        addAdvertiseReqDTO.setAdvertisePriceType(AdvertisePriceTypeEnum.FIXED.getCode());
        addAdvertiseReqDTO.setAdvertiseFixedPrice(BigDecimal.valueOf(450000));

        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTime(120);
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeArray(AdvertiseBusinessPaymentTermTypeEnum.ALIPAY.getCode() + "");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeAlipayAccount("alipay-account");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeAlipayQrcode("alipay-qrcode");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeWechatAccount("wechat-account");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeWechatQrcode("wechat-qrcode");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeBankName("bank-name");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeBankBranchName("bank-branch-name");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeBankAccount("bank-account");
        addAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeBankRealname("bank-realname");
        addAdvertiseReqDTO.setAdvertiseAutoReplyContent("你好，欢迎光临");
        addAdvertiseReqDTO.setRemark("test-addOrdere-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        PutOnShelvesReqDTO putOnShelvesReqDTO = AutoMapUtils.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(putOnShelvesReqDTO.getAdvertiseCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        //新建订单(顾客在广告区买币)

        userService.login(LoginReqDTO.builder().username(CUSTOMER_USER_NAME).password(CUSTOMER_PASSWORD).build());

        AddOrderReqDTO addOrderReqDTO = new AddOrderReqDTO();
        addOrderReqDTO.setOrderCode("test-顾客在广告区买币-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        addOrderReqDTO.setOrderSource(OrderSourceEnum.ADVERTISE_SELECT.getCode());
        addOrderReqDTO.setOrderAmount(BigDecimal.valueOf(8));
        addOrderReqDTO.setOrderCustomerPaymentTermType(OrderCustomerPaymentTermTypeEnum.ALIPAY.getCode());
        addOrderReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());

        addOrderReqDTO.setOrderCustomerPaymentTermTime(120);
        addOrderReqDTO.setOrderCustomerPaymentTermTypeArray(AdvertiseBusinessPaymentTermTypeEnum.ALIPAY.getCode() + "");
        addOrderReqDTO.setOrderCustomerPaymentTermTypeAlipayAccount("order-customer-alipay-account");
        addOrderReqDTO.setOrderCustomerPaymentTermTypeAlipayQrcode("order-customer-alipay-qrcode");

        addOrderReqDTO.setRemark("顾客在广告区买币");
        orderService.addOrder(addOrderReqDTO);

        //分页搜索订单
        PageOrderReqDTO pageOrderReqDTO = new PageOrderReqDTO();
        PageRespDTO<OrderRespDTO> orderRespDTOPageResult = orderService.pageOrder(pageOrderReqDTO).getData();
        total = page.getTotal();
        Assert.assertTrue(total > 0);

        //判断广告商家作为卖币方时，其账户需要冻结金额，广告也需要冻结金额
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(orderRespDTO.getOrderCoinCode());
        accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(orderRespDTO.getAdvertiseCode());
        AdvertiseRespDTO advertiseRespDTO = advertisService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Assert.assertTrue(advertiseRespDTO.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) > 0);

        //付款

        userService.login(LoginReqDTO.builder().username(CUSTOMER_USER_NAME).password(CUSTOMER_PASSWORD).build());

        PayOrderReqDTO payOrderReqDTO = new PayOrderReqDTO();
        payOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.payOrder(payOrderReqDTO);

        //判断付款
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode());

        //付款后对账
        Assert.assertTrue(accountService.checkAccount().getData());

        //放行
        userService.login(LoginReqDTO.builder().username(BUSINESS_USER_NAME).password(BUSINESS_PASSWORD).build());

        ReleaseOrderReqDTO releaseOrderReqDTO = new ReleaseOrderReqDTO();
        releaseOrderReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderService.releaseOrder(releaseOrderReqDTO);

        //判断放行
        getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Assert.assertTrue(orderRespDTO.getStatus() == OrderStatusEnum.RELEASE.getCode());

        //放行后对账
        Assert.assertTrue(accountService.checkAccount().getData());

        //订单日志
        PageOrderLogReqDTO pageOrderLogReqDTO = new PageOrderLogReqDTO();
        PageRespDTO<OrderLogRespDTO> pageRespDTO = orderLogService.pageOrderLog(pageOrderLogReqDTO).getData();
        total = pageRespDTO.getTotal();
        Assert.assertTrue(total > 0);

    }

}
