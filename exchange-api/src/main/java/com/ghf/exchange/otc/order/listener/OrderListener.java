package com.ghf.exchange.otc.order.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.order.dto.AgreeUnPayOrderAppealForClientReqDTO;
import com.ghf.exchange.otc.order.dto.AgreeUnReleaseOrderAppealForClientReqDTO;
import com.ghf.exchange.otc.order.dto.UpdateOrderStatusForClientReqDTO;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealStatusEnum;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealTypeEnum;
import com.ghf.exchange.otc.orderappeal.event.AddOrderAppealEvent;
import com.ghf.exchange.otc.orderappeal.event.AuditOrderAppealForAdminEvent;
import com.ghf.exchange.otc.orderappeal.event.CancelOrderAppealEvent;
import com.ghf.exchange.util.JsonUtil;
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
public class OrderListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private OrderService orderService;

    @Async
    @EventListener
    public void onAddOrderAppealEvent(AddOrderAppealEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        //未付款申诉或未放行申诉，需要修改订单状态为申诉
        if (event.getOrderAppealType() == OrderAppealTypeEnum.UN_PAY.getCode() || event.getOrderAppealType() == OrderAppealTypeEnum.UN_RELEASE.getCode()) {
            UpdateOrderStatusForClientReqDTO updateOrderStatusForClientReqDTO = new UpdateOrderStatusForClientReqDTO();
            updateOrderStatusForClientReqDTO.setOrderCode(event.getOrderCode());
            updateOrderStatusForClientReqDTO.setStatus(OrderStatusEnum.APPEAL.getCode());
            orderService.updateOrderStatusForClient(updateOrderStatusForClientReqDTO);
        } else if (event.getOrderAppealType() == OrderAppealTypeEnum.OTHER.getCode()) {
            //TODO 什么都不做

        }

    }

    @Async
    @EventListener
    public void onAuditOrderAppealForAdminEvent(AuditOrderAppealForAdminEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        //审核订单申诉，把订单状态修改成管理员设定的
        UpdateOrderStatusForClientReqDTO updateOrderStatusForClientReqDTO = new UpdateOrderStatusForClientReqDTO();
        updateOrderStatusForClientReqDTO.setOrderCode(event.getOrderCode());

        //
        if (event.getOrderAppealType() == OrderAppealTypeEnum.UN_PAY.getCode()) {
            if (event.getStatus() == OrderAppealStatusEnum.SUCCESS.getCode()) {
                //未支付申诉，并且审核成功时,订单状态改为已下单
                AgreeUnPayOrderAppealForClientReqDTO agreeUnPayOrderAppealForClientReqDTO = new AgreeUnPayOrderAppealForClientReqDTO();
                agreeUnPayOrderAppealForClientReqDTO.setOrderCode(event.getOrderCode());
                orderService.agreeUnPayOrderAppealForClient(agreeUnPayOrderAppealForClientReqDTO);

            } else if (event.getStatus() == OrderAppealStatusEnum.FAIL.getCode()) {
                //未支付申诉，并且审核失败时,订单状态改为已付款(恢复原来的)
                updateOrderStatusForClientReqDTO.setStatus(event.getOrderOldStatus());
                orderService.updateOrderStatusForClient(updateOrderStatusForClientReqDTO);
            }

        } else if (event.getOrderAppealType() == OrderAppealTypeEnum.UN_RELEASE.getCode()) {
            if (event.getStatus() == OrderAppealStatusEnum.SUCCESS.getCode()) {
// 未放行申诉，并且审核成功时,订单状态改为已放行，并且直接放行
                AgreeUnReleaseOrderAppealForClientReqDTO agreeUnReleaseOrderAppealForClientReqDTO = new AgreeUnReleaseOrderAppealForClientReqDTO();
                agreeUnReleaseOrderAppealForClientReqDTO.setOrderCode(event.getOrderCode());
                orderService.agreeUnReleaseOrderAppealForClient(agreeUnReleaseOrderAppealForClientReqDTO);
            } else if (event.getStatus() == OrderAppealStatusEnum.FAIL.getCode()) {
//未放行申诉，并且审核失败时，订单状态改成已付款(恢复原来的)
                updateOrderStatusForClientReqDTO.setStatus(event.getOrderOldStatus());
                orderService.updateOrderStatusForClient(updateOrderStatusForClientReqDTO);
            }
        } else if (event.getOrderAppealType() == OrderAppealTypeEnum.OTHER.getCode()) {
            //TODO 什么都不做

        }

    }

    @Async
    @EventListener
    public void onCancelOrderAppealEvent(CancelOrderAppealEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        //取消订单申诉，把订单状态修改成原来的
        UpdateOrderStatusForClientReqDTO updateOrderStatusForClientReqDTO = new UpdateOrderStatusForClientReqDTO();
        updateOrderStatusForClientReqDTO.setOrderCode(event.getOrderCode());
        updateOrderStatusForClientReqDTO.setStatus(event.getOrderOldStatus());
        orderService.updateOrderStatusForClient(updateOrderStatusForClientReqDTO);

    }

}