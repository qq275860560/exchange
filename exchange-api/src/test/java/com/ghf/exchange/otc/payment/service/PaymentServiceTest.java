
package com.ghf.exchange.otc.payment.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.Constants;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.ordermessage.service.OrderMessageService;
import com.ghf.exchange.otc.payment.dto.*;
import com.ghf.exchange.otc.payment.enums.PaymentStatusEnum;
import com.ghf.exchange.otc.payment.enums.PaymentTypeEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
public class PaymentServiceTest {
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

    @SneakyThrows
    @Test
    public void addPayment() {

        //新建付款方式
        userService.login(LoginReqDTO.builder().username(Constants.ADVERTISE_BUSINESS_USER_NAME).password(Constants.ADVERTISE_BUSINESS_PASSWORD).build());

        AddPaymentReqDTO addPaymentReqDTO = new AddPaymentReqDTO();
        addPaymentReqDTO.setPaymentCode("test-addPayment-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPaymentReqDTO.setPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        addPaymentReqDTO.setPaymentTypeAlipayAccount("alipayaccount");
        addPaymentReqDTO.setPaymentTypeAlipayQrcode("alipayqrcode");
        addPaymentReqDTO.setRemark("支付宝");
        paymentService.addPayment(addPaymentReqDTO);

        GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO = new GetPaymentByPaymentTypeForClientReqDTO();
        getPaymentByPaymentTypeForClientReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        getPaymentByPaymentTypeForClientReqDTO.setPaymentType(PaymentTypeEnum.ALIPAY.getCode());
        PaymentRespDTO paymentRespDTO = paymentService.getPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO).getData();

        GetPaymentByPaymentCodeReqDTO getPaymentByCodeReqDTO = new GetPaymentByPaymentCodeReqDTO();
        getPaymentByCodeReqDTO.setPaymentCode(paymentRespDTO.getPaymentCode());
        PaymentRespDTO paymentRespDTO2 = paymentService.getPaymentByPaymentCode(getPaymentByCodeReqDTO).getData();
        Assert.assertTrue(paymentRespDTO.getPaymentTypeAlipayAccount().equals(paymentRespDTO2.getPaymentTypeAlipayAccount()));

        //分页搜索付款方式
        PagePaymentReqDTO pagePaymentReqDTO = new PagePaymentReqDTO();
        int total = paymentService.pagePayment(pagePaymentReqDTO).getData().getTotal();
        Assert.assertTrue(total > 0);

        paymentService.disablePayment(getPaymentByCodeReqDTO);

        Assert.assertTrue(paymentService.getPaymentByPaymentCode(getPaymentByCodeReqDTO).getData().getStatus() == PaymentStatusEnum.DISABLE.getCode());
        paymentService.enablePayment(getPaymentByCodeReqDTO);
        Assert.assertTrue(paymentService.getPaymentByPaymentCode(getPaymentByCodeReqDTO).getData().getStatus() == PaymentStatusEnum.ENABLE.getCode());

        //列出搜索付款方式
        ListPaymentReqDTO listPaymentReqDTO = new ListPaymentReqDTO();
        total = paymentService.listPayment(listPaymentReqDTO).getData().size();
        Assert.assertTrue(total > 0);

        UpdatePaymentByPaymentCodeReqDTO updatePaymentByCodeReqDTO = new UpdatePaymentByPaymentCodeReqDTO();
        updatePaymentByCodeReqDTO.setPaymentCode(addPaymentReqDTO.getPaymentCode());
        updatePaymentByCodeReqDTO.setPaymentType(PaymentTypeEnum.WECHAT.getCode());
        updatePaymentByCodeReqDTO.setPaymentTypeWechatAccount("wechataccount");
        updatePaymentByCodeReqDTO.setPaymentTypeWechatQrcode("wechatqrcode");
        updatePaymentByCodeReqDTO.setRemark("微信");
        paymentService.updatePaymentByPaymentCode(updatePaymentByCodeReqDTO);

        //管理员分页搜索付款方式
        userService.login(LoginReqDTO.builder().username(Constants.ADMIN_USER_NAME).password(Constants.ADMIN_PASSWORD).build());

        PagePaymentForAdminReqDTO pagePaymentForAdminReqDTO = new PagePaymentForAdminReqDTO();
        pagePaymentForAdminReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        total = paymentService.pagePaymentForAdmin(pagePaymentForAdminReqDTO).getData().getTotal();
        Assert.assertTrue(total > 0);

        //管理员列出付款方式
        ListPaymentForAdminReqDTO listPaymentForAdminReqDTO = new ListPaymentForAdminReqDTO();
        listPaymentForAdminReqDTO.setUsername(Constants.ADVERTISE_BUSINESS_USER_NAME);
        total = paymentService.listPaymentForAdmin(listPaymentForAdminReqDTO).getData().size();
        Assert.assertTrue(total > 0);

    }

}
