package com.ghf.exchange.otc.orderlog.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.order.event.*;
import com.ghf.exchange.otc.orderlog.dto.AddOrderLogForClientReqDTO;
import com.ghf.exchange.otc.orderlog.service.OrderLogService;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
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

    @Async
    @EventListener
    public void onAddOrderEvent(AddOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onPayOrderEvent(PayOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onAgreeUnPayOrderEvent(AgreeUnPayOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onReleaseOrderEvent(ReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onAgreeUnReleaseOrderEvent(AgreeUnReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onCancelOrderEvent(CancelOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onUpdateOrderStatusEvent(UpdateOrderStatusEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddOrderLogForClientReqDTO addOrderLogForClientReqDTO = ModelMapperUtil.map(event, AddOrderLogForClientReqDTO.class);

        orderLogService.addOrderLogForClient(addOrderLogForClientReqDTO);

    }

}