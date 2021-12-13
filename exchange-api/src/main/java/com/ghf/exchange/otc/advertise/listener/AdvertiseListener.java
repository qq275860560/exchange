package com.ghf.exchange.otc.advertise.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.advertise.dto.PutOffShelvesForClientReqDTO;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.order.event.AgreeUnReleaseOrderEvent;
import com.ghf.exchange.otc.order.event.ReleaseOrderEvent;
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
public class AdvertiseListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private AdvertiseService advertiseService;

    @Async
    @EventListener
    public void onReleaseOrderEvent(ReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        PutOffShelvesForClientReqDTO putOffShelvesForClientReqDTO = ModelMapperUtil.map(event, PutOffShelvesForClientReqDTO.class);
        advertiseService.putOffShelvesForClient(putOffShelvesForClientReqDTO);
    }

    @Async
    @EventListener
    public void onAgreeUnReleaseOrderEvent(AgreeUnReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        PutOffShelvesForClientReqDTO putOffShelvesForClientReqDTO = ModelMapperUtil.map(event, PutOffShelvesForClientReqDTO.class);
        advertiseService.putOffShelvesForClient(putOffShelvesForClientReqDTO);
    }
}