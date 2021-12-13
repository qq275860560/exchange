package com.ghf.exchange.otc.advertiseamountlog.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.advertise.event.DecAdvertiseFrozenAmountEvent;
import com.ghf.exchange.otc.advertise.event.FreezeAdvertiseAmountEvent;
import com.ghf.exchange.otc.advertise.event.UnFreezeAdvertiseAmountEvent;
import com.ghf.exchange.otc.advertiseamountlog.dto.AddAdvertiseAmountLogForClientReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.service.AdvertiseAmountLogService;
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
public class AdvertiseAmountLogListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private AdvertiseAmountLogService advertiseAmountLogService;

    @Async
    @EventListener
    public void onFreezeAdvertiseAmountEvent(FreezeAdvertiseAmountEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseAmountLogForClientReqDTO addAdvertiseAmountLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseAmountLogForClientReqDTO.class);

        advertiseAmountLogService.addAdvertiseAmountLogForClient(addAdvertiseAmountLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onUnFreezeAdvertiseAmountEvent(UnFreezeAdvertiseAmountEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseAmountLogForClientReqDTO addAdvertiseAmountLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseAmountLogForClientReqDTO.class);

        advertiseAmountLogService.addAdvertiseAmountLogForClient(addAdvertiseAmountLogForClientReqDTO);

    }

    @Async
    @EventListener
    public void onDecAdvertiseFrozenAmountEvent(DecAdvertiseFrozenAmountEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseAmountLogForClientReqDTO addAdvertiseAmountLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseAmountLogForClientReqDTO.class);

        advertiseAmountLogService.addAdvertiseAmountLogForClient(addAdvertiseAmountLogForClientReqDTO);

    }
}