package com.ghf.exchange.otc.advertiselog.listener;

import com.ghf.exchange.boss.authorication.client.dto.LoginClientReqDTO;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.advertise.event.AddAdvertiseEvent;
import com.ghf.exchange.otc.advertise.event.DeleteAdvertiseEvent;
import com.ghf.exchange.otc.advertise.event.PutOffShelvesEvent;
import com.ghf.exchange.otc.advertise.event.PutOnShelvesEvent;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.service.AdvertiseLogService;
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

    @Value("${security.oauth2.client.client-id}")
    public String clientId;

    @Value("${security.oauth2.client.client-secret}")
    public String secret;

    @Async
    @EventListener
    public void onAdAdvertiseEvent(AddAdvertiseEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = (AddAdvertiseLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        advertiseLogService.addAdvertiseLog(addAdvertiseLogReqDTO);
    }

    @Async
    @EventListener
    public void onPutOnShelvesEvent(PutOnShelvesEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = (AddAdvertiseLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        advertiseLogService.addAdvertiseLog(addAdvertiseLogReqDTO);
    }

    @Async
    @EventListener
    public void onPutOffShelvesEvent(PutOffShelvesEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = (AddAdvertiseLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        advertiseLogService.addAdvertiseLog(addAdvertiseLogReqDTO);

    }

    @Async
    @EventListener
    public void onDeleteAdvertiseEvent(DeleteAdvertiseEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = (AddAdvertiseLogReqDTO) event.getSource();
        clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(secret).build());
        advertiseLogService.addAdvertiseLog(addAdvertiseLogReqDTO);
    }

}