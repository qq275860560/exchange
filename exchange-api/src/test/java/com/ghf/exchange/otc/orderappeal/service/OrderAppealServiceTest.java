
package com.ghf.exchange.otc.orderappeal.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.AddAdvertiseReqDTO;
import com.ghf.exchange.otc.advertise.dto.PutOnShelvesReqDTO;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBusinessPaymentTermTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.order.dto.AddOrderReqDTO;
import com.ghf.exchange.otc.order.enums.OrderCustomerPaymentTermTypeEnum;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderappeal.dto.*;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealStatusEnum;
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
    public void addOrderAppeal() {

        //发布广告
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

        //上架广告
        PutOnShelvesReqDTO putOnShelvesReqDTO = AutoMapUtils.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);
        advertisService.putOnShelves(putOnShelvesReqDTO);

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

        //申诉订单
        userService.login(LoginReqDTO.builder().username(CUSTOMER_USER_NAME).password(CUSTOMER_PASSWORD).build());
        AddOrderAppealReqDTO addAppealReqDTO = new AddOrderAppealReqDTO();
        addAppealReqDTO.setOrderAppealCode("test-申诉-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        addAppealReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addAppealReqDTO.setOrderAppealContent("总价格有疑问");
        appealService.addOrderAppeal(addAppealReqDTO);

        PageOrderAppealReqDTO pageAppealReqDTO = new PageOrderAppealReqDTO();
        int total = appealService.pageOrderAppeal(pageAppealReqDTO).getData().getTotal();
        Assert.assertTrue(total > 0);

        GetOrderAppealByOrderAppealCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getAppealByAppealCodeReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        OrderAppealRespDTO appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode());

        //审核申诉
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());
        AuditOrderAppealReqDTO auditAppealReqDTO = new AuditOrderAppealReqDTO();
        auditAppealReqDTO.setOrderAppealCode(addAppealReqDTO.getOrderAppealCode());
        auditAppealReqDTO.setOrderAppealAuditResult("处理完毕");
        appealService.auditOrderAppeal(auditAppealReqDTO);

        appealRespDTO = appealService.getOrderAppealByOrderAppealCode(getAppealByAppealCodeReqDTO).getData();
        Assert.assertTrue(appealRespDTO.getStatus() == OrderAppealStatusEnum.AUDIT.getCode());

    }

}
