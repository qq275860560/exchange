package com.ghf.exchange.otc.advertiselog.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.advertise.event.*;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogForClientReqDTO;
import com.ghf.exchange.otc.advertiselog.service.AdvertiseLogService;
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
public class AdvertiseLogListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private AdvertiseLogService advertiseLogService;

    @Async
    @EventListener
    public void onAddAdvertiseEvent(AddAdvertiseEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseLogForClientReqDTO addAdvertiseLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseLogForClientReqDTO.class);

        advertiseLogService.addAdvertiseLogForClient(addAdvertiseLogForClientReqDTO);
    }

    @Async
    @EventListener
    public void onPutOnShelvesEvent(PutOnShelvesEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseLogForClientReqDTO addAdvertiseLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseLogForClientReqDTO.class);

        advertiseLogService.addAdvertiseLogForClient(addAdvertiseLogForClientReqDTO);
    }

    @Async
    @EventListener
    public void onPutOffShelvesEvent(PutOffShelvesEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseLogForClientReqDTO addAdvertiseLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseLogForClientReqDTO.class);

        advertiseLogService.addAdvertiseLogForClient(addAdvertiseLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onDeleteAdvertiseEvent(DeleteAdvertiseEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseLogForClientReqDTO addAdvertiseLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseLogForClientReqDTO.class);

        advertiseLogService.addAdvertiseLogForClient(addAdvertiseLogForClientReqDTO);
    }

    @Async
    @EventListener
    public void onFreezeAdvertiseAmountEvent(FreezeAdvertiseAmountEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        //TODO 广告日志

    }

    @Async
    @EventListener
    public void onUnFreezeAdvertiseAmountEvent(UnFreezeAdvertiseAmountEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        //TODO 广告日志

    }

    @Async
    @EventListener
    public void onDecAdvertiseFrozenAmountEvent(DecAdvertiseFrozenAmountEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        //TODO 广告日志

    }
}