package com.ghf.exchange.otc.advertisebusiness.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.advertise.event.PutOffShelvesEvent;
import com.ghf.exchange.otc.advertise.event.PutOnShelvesEvent;
import com.ghf.exchange.otc.advertisebusiness.dto.GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO;
import com.ghf.exchange.otc.advertisebusiness.dto.UpdateAdvertiseBusinessOnAddOrderEventForClientReqDTO;
import com.ghf.exchange.otc.advertisebusiness.dto.UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO;
import com.ghf.exchange.otc.advertisebusiness.dto.UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO;
import com.ghf.exchange.otc.advertisebusiness.service.AdvertiseBusinessService;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogForClientReqDTO;
import com.ghf.exchange.otc.order.event.AddOrderEvent;
import com.ghf.exchange.otc.order.event.ReleaseOrderEvent;
import com.ghf.exchange.otc.orderappeal.event.AddOrderAppealEvent;
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
public class AdvertiseBusinessListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private AdvertiseBusinessService advertiseBusinessService;

    @Async
    @EventListener
    public void onPutOnShelvesEvent(PutOnShelvesEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseLogForClientReqDTO addAdvertiseLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseLogForClientReqDTO.class);

        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(event.getAdvertiseLogUsername());
        advertiseBusinessService.updateAdvertiseBusinessOnPutOnShelvesForClient(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @Async
    @EventListener
    public void onPutOffShelvesEvent(PutOffShelvesEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddAdvertiseLogForClientReqDTO addAdvertiseLogForClientReqDTO = ModelMapperUtil.map(event, AddAdvertiseLogForClientReqDTO.class);

        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(event.getAdvertiseLogUsername());
        advertiseBusinessService.updateAdvertiseBusinessOnPutOffShelvesForClient(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @Async
    @EventListener
    public void onAddOrderEvent(AddOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateAdvertiseBusinessOnAddOrderEventForClientReqDTO updateAdvertiseBusinessWhenAddOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateAdvertiseBusinessOnAddOrderEventForClientReqDTO.class);
        updateAdvertiseBusinessWhenAddOrderEventForClientReqDTO.setAdvertiseBusinessCode(event.getAdvertiseBusinessUsername());
        advertiseBusinessService.updateAdvertiseBusinessOnAddOrderEventForClient(updateAdvertiseBusinessWhenAddOrderEventForClientReqDTO);

    }

    @Async
    @EventListener
    public void onReleaseOrderEvent(ReleaseOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.class);
        updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.setAdvertiseBusinessCode(event.getAdvertiseBusinessUsername());
        advertiseBusinessService.updateAdvertiseBusinessOnReleaseOrderEventForClient(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO);
    }

    @Async
    @EventListener
    public void onAddOrderAppealEvent(AddOrderAppealEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));

        UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO = ModelMapperUtil.map(event, UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.class);
        updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.setAdvertiseBusinessCode(event.getAdvertiseBusinessUsername());
        if (updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.getAppealTime() == null) {
            updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.setAppealTime(new Date());
        }
        advertiseBusinessService.updateAdvertiseBusinessOnAppealOrderEventForClient(updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO);
    }

}