package com.ghf.exchange.otc.orderlog.listener;

import com.ghf.exchange.boss.authorication.client.dto.LoginClientReqDTO;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.order.event.AddOrderEvent;
import com.ghf.exchange.otc.order.event.CancelOrderEvent;
import com.ghf.exchange.otc.order.event.PayOrderEvent;
import com.ghf.exchange.otc.order.event.ReleaseOrderEvent;
import com.ghf.exchange.otc.orderlog.dto.AddOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.service.OrderLogService;
import com.ghf.exchange.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderLogListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private OrderLogService orderLogService;

    @Value("${security.oauth2.client.client-id}")
    public String clientId;

    @Value("${security.oauth2.client.client-secret}")
    public String secret;

    @Async
    @EventListener
    public void onAddOrderEvent(AddOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddOrderLogReqDTO addOrderLogReqDTO = (AddOrderLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        orderLogService.addOrderLog(addOrderLogReqDTO);

    }

    @Async
    @EventListener
    public void onPayOrderEvent(PayOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddOrderLogReqDTO addOrderLogReqDTO = (AddOrderLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        orderLogService.addOrderLog(addOrderLogReqDTO);

    }

    @Async
    @EventListener
    public void onReleaseOrderEvent(ReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddOrderLogReqDTO addOrderLogReqDTO = (AddOrderLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        orderLogService.addOrderLog(addOrderLogReqDTO);

    }

    @Async
    @EventListener
    public void onCancelOrderEvent(CancelOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddOrderLogReqDTO addOrderLogReqDTO = (AddOrderLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        orderLogService.addOrderLog(addOrderLogReqDTO);

    }

    @Async
    @EventListener
    public void onRecoverOrderEvent(ReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddOrderLogReqDTO addOrderLogReqDTO = (AddOrderLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        orderLogService.addOrderLog(addOrderLogReqDTO);

    }

}