package com.ghf.exchange.boss.authorization.userorg.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.UpdateUserOrgnamesByUsernameReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.userorg.dto.ListOrgByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.event.UpdateUserOrgEvent;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class UserOrgListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private OrgService orgService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Value("${security.oauth2.client.client-id}")
    public String clientId;

    @Value("${security.oauth2.client.client-secret}")
    public String secret;

    @Async
    @EventListener
    public void updateUserOrg(UpdateUserOrgEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("User", "UserOrg", "OrgRole");
        String username = (String) event.getSource();

        ListOrgByUsernameReqDTO listOrgByUsernameReqDTO = new ListOrgByUsernameReqDTO();
        listOrgByUsernameReqDTO.setUsername(username);
        List<OrgRespDTO> list = userOrgService.listOrgByUsername(listOrgByUsernameReqDTO).getData();

        StringBuilder sb = new StringBuilder(",");
        StringBuilder sb2 = new StringBuilder(",");
        list.stream().forEach(e -> {
            sb.append(e.getOrgname()).append(",");
            sb2.append(e.getOrgdesc()).append(",");
        });

        String orgnames = sb.toString();
        String orgdescs = sb2.toString();
        UpdateUserOrgnamesByUsernameReqDTO updateUserOrgnamesByUsernameReqDTO = new UpdateUserOrgnamesByUsernameReqDTO();
        updateUserOrgnamesByUsernameReqDTO.setUsername(username);
        updateUserOrgnamesByUsernameReqDTO.setOrgnames(orgnames);
        updateUserOrgnamesByUsernameReqDTO.setOrgdescs(orgdescs);
        userService.updateUserOrgnamesByUsername(updateUserOrgnamesByUsernameReqDTO);
    }

}