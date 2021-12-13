package com.ghf.exchange.otc.ordercustomer.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.order.event.AddOrderEvent;
import com.ghf.exchange.otc.order.event.CancelOrderEvent;
import com.ghf.exchange.otc.order.event.ReleaseOrderEvent;
import com.ghf.exchange.otc.orderappeal.event.AddOrderAppealEvent;
import com.ghf.exchange.otc.ordercustomer.dto.UpdateOrderCustomerOnAddOrderEventForClientReqDTO;
import com.ghf.exchange.otc.ordercustomer.dto.UpdateOrderCustomerOnAppealOrderEventForClientReqDTO;
import com.ghf.exchange.otc.ordercustomer.dto.UpdateOrderCustomerOnCancelOrderEventForClientReqDTO;
import com.ghf.exchange.otc.ordercustomer.dto.UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO;
import com.ghf.exchange.otc.ordercustomer.service.OrderCustomerService;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderCustomerListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private OrderCustomerService orderCustomerService;

    @Async
    @EventListener
    public void onAddOrderEvent(AddOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateOrderCustomerOnAddOrderEventForClientReqDTO updateOrderCustomerWhenAddOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateOrderCustomerOnAddOrderEventForClientReqDTO.class);
        updateOrderCustomerWhenAddOrderEventForClientReqDTO.setOrderCustomerCode(event.getOrderCustomerUsername());
        orderCustomerService.updateOrderCustomerOnAddOrderEventForClient(updateOrderCustomerWhenAddOrderEventForClientReqDTO);

    }

    @Async
    @EventListener
    public void onReleaseOrderEvent(ReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO updateOrderCustomerOnReleaseOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO.class);
        updateOrderCustomerOnReleaseOrderEventForClientReqDTO.setOrderCustomerCode(event.getOrderCustomerUsername());
        orderCustomerService.updateOrderCustomerOnReleaseOrderEventForClient(updateOrderCustomerOnReleaseOrderEventForClientReqDTO);
    }

    @Async
    @EventListener
    public void onAddOrderAppealEvent(AddOrderAppealEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateOrderCustomerOnAppealOrderEventForClientReqDTO updateOrderCustomerOnAppealOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateOrderCustomerOnAppealOrderEventForClientReqDTO.class);
        updateOrderCustomerOnAppealOrderEventForClientReqDTO.setOrderCustomerCode(event.getOrderCustomerUsername());
        if (updateOrderCustomerOnAppealOrderEventForClientReqDTO.getAppealTime() == null) {
            updateOrderCustomerOnAppealOrderEventForClientReqDTO.setAppealTime(new Date());
        }
        orderCustomerService.updateOrderCustomerOnAppealOrderEventForClient(updateOrderCustomerOnAppealOrderEventForClientReqDTO);
    }

    @Async
    @EventListener
    public void onCancelOrderEvent(CancelOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateOrderCustomerOnCancelOrderEventForClientReqDTO updateOrderCustomerOnCancelOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateOrderCustomerOnCancelOrderEventForClientReqDTO.class);
        updateOrderCustomerOnCancelOrderEventForClientReqDTO.setOrderCustomerCode(event.getOrderCustomerUsername());
        orderCustomerService.updateOrderCustomerOnCancelOrderEventForClient(updateOrderCustomerOnCancelOrderEventForClientReqDTO);
    }

}