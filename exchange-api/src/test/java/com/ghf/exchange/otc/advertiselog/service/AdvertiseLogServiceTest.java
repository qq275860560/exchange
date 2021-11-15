
package com.ghf.exchange.otc.advertiselog.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.otc.account.dto.AccountRespDTO;
import com.ghf.exchange.otc.account.dto.GetAccountByUsernameAndCoinCodeReqDTO;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.entity.QAdvertise;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBusinessPaymentTermTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.advertiselog.dto.AdvertiseLogRespDTO;
import com.ghf.exchange.otc.advertiselog.dto.PageAdvertiseLogReqDTO;
import com.ghf.exchange.util.AutoMapUtils;
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

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class AdvertiseLogServiceTest {
    @Lazy
    @Resource
    private AdvertiseService advertisService;
    @Lazy
    @Resource
    private AdvertiseLogService advertisLogService;

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
    public static final String SERVER_CLIENTID = "otc-api";

    /**
     * 内部服务器密码（测试）
     */
    public static final String SERVER_SECRET = "123456";

    @SneakyThrows
    @Test
    public void addAdvertiseLog() {

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
        addAdvertiseReqDTO.setRemark("test-addAdvertise-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        advertisService.addAdvertise(addAdvertiseReqDTO);

        //清理

        PageAdvertiseReqDTO pageAdvertiseReqDTO = new PageAdvertiseReqDTO();

        PageRespDTO<AdvertiseRespDTO> page = advertisService.pageAdvertise(pageAdvertiseReqDTO).getData();
        int total = page.getTotal();
        Assert.assertTrue(total > 0);

        Predicate predicate = QAdvertise.advertise.advertiseCode.eq(addAdvertiseReqDTO.getAdvertiseCode());

        PutOnShelvesReqDTO putOnShelvesReqDTO = AutoMapUtils.map(addAdvertiseReqDTO, PutOnShelvesReqDTO.class);

        advertisService.putOnShelves(putOnShelvesReqDTO);

        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(BUSINESS_USER_NAME);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(putOnShelvesReqDTO.getAdvertiseCoinCode());
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Assert.assertTrue(accountRespDTO.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0);

        PutOffShelvesReqDTO putOffShelvesReqDTO = new PutOffShelvesReqDTO();
        putOffShelvesReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertisService.putOffShelves(putOffShelvesReqDTO);

        DeleteAdvertiseReqDTO deleteAdvertiseReqDTO = new DeleteAdvertiseReqDTO();
        deleteAdvertiseReqDTO.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        advertisService.deleteAdvertise(deleteAdvertiseReqDTO);

        PageAdvertiseLogReqDTO pageAdvertiseLogReqDTO = new PageAdvertiseLogReqDTO();
        PageRespDTO<AdvertiseLogRespDTO> pageRespDTO = advertisLogService.pageAdvertiseLog(pageAdvertiseLogReqDTO).getData();
        total = pageRespDTO.getTotal();
        Assert.assertTrue(total > 0);
    }

}
