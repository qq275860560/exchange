package com.ghf.exchange.boss.authorization.orgrole.listener;

import com.ghf.exchange.boss.authorication.user.dto.UpdateUserRolenamesByUsernameReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.org.dto.UpdateOrgRolenamesByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.orgrole.dto.ListRoleByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.event.UpdateOrgRoleEvent;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.ListOrgByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.ListUserOrgByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.UserOrgRespDTO;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.dto.ListRoleByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
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
public class OrgRoleListener {

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private OrgService orgService;

    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @Lazy
    @Resource
    private UserRoleService userRoleService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void updateOrgRole(UpdateOrgRoleEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("User", "UserOrg", "OrgRole");
        String orgname = (String) event.getSource();

        //更新user的冗余字段
        ListUserOrgByOrgnameReqDTO listUserOrgByOrgnameReqDTO = new ListUserOrgByOrgnameReqDTO();
        listUserOrgByOrgnameReqDTO.setOrgname(orgname);
        List<UserOrgRespDTO> userOrgRespDTOList = userOrgService.listUserOrgByOrgname(listUserOrgByOrgnameReqDTO).getData();
        for (UserOrgRespDTO userOrgRespDTO : userOrgRespDTOList) {
            String username = userOrgRespDTO.getUsername();

            StringBuilder sb = new StringBuilder(",");
            StringBuilder sb2 = new StringBuilder(",");

            ListRoleByUsernameReqDTO listRoleByUsernameReqDTO = new ListRoleByUsernameReqDTO();
            listRoleByUsernameReqDTO.setUsername(username);
            List<RoleRespDTO> roleRespDTOList = userRoleService.listRoleByUsername(listRoleByUsernameReqDTO).getData();

            roleRespDTOList.stream().forEach(e -> {
                sb.append(e.getRolename()).append(",");
                sb2.append(e.getRoledesc()).append(",");
            });

            ListOrgByUsernameReqDTO listOrgByUsernameReqDTO = new ListOrgByUsernameReqDTO();
            listOrgByUsernameReqDTO.setUsername(username);
            List<OrgRespDTO> orgRespDTOList = userOrgService.listOrgByUsername(listOrgByUsernameReqDTO).getData();
            for (OrgRespDTO orgRespDTO : orgRespDTOList) {
                ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
                listRoleByOrgnameReqDTO.setOrgname(orgRespDTO.getOrgname());
                List<RoleRespDTO> list2 = orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData();
                list2.stream().forEach(e -> {
                    sb.append(e.getRolename()).append(",");
                    sb2.append(e.getRoledesc()).append(",");
                });
            }

            String rolenames = sb.toString();
            String roledescs = sb2.toString();

            UpdateUserRolenamesByUsernameReqDTO updateUserRolenamesByUsernameReqDTO = new UpdateUserRolenamesByUsernameReqDTO();
            updateUserRolenamesByUsernameReqDTO.setUsername(username);
            updateUserRolenamesByUsernameReqDTO.setRolenames(rolenames);
            updateUserRolenamesByUsernameReqDTO.setRoledescs(roledescs);
            userService.updateUserRolenamesByUsername(updateUserRolenamesByUsernameReqDTO);
        }

        //更新org的冗余字段
        ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
        listRoleByOrgnameReqDTO.setOrgname(orgname);
        List<RoleRespDTO> list = orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData();

        StringBuilder sb = new StringBuilder(",");
        StringBuilder sb2 = new StringBuilder(",");
        list.stream().forEach(e -> {
            sb.append(e.getRolename()).append(",");
            sb2.append(e.getRoledesc()).append(",");
        });

        String rolenames = sb.toString();
        String roledescs = sb2.toString();
        UpdateOrgRolenamesByOrgnameReqDTO updateOrgRolenamesByOrgnameReqDTO = new UpdateOrgRolenamesByOrgnameReqDTO();
        updateOrgRolenamesByOrgnameReqDTO.setOrgname(orgname);
        updateOrgRolenamesByOrgnameReqDTO.setRolenames(rolenames);
        updateOrgRolenamesByOrgnameReqDTO.setRoledescs(roledescs);
        orgService.updateOrgRolenamesByOrgname(updateOrgRolenamesByOrgnameReqDTO);
    }

}