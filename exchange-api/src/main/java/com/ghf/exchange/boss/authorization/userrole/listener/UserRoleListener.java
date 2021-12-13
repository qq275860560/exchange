package com.ghf.exchange.boss.authorization.userrole.listener;

import com.ghf.exchange.boss.authorication.user.dto.UpdateUserRolenamesByUsernameReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.orgrole.dto.ListRoleByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.ListOrgByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.dto.ListRoleByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userrole.event.UpdateUserRoleEvent;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
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
public class UserRoleListener {

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private UserRoleService userRoleService;
    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void updateUserRole(UpdateUserRoleEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("User", "UserRole", "Role");
        String username = (String) event.getSource();

        StringBuilder sb = new StringBuilder(",");
        StringBuilder sb2 = new StringBuilder(",");

        ListRoleByUsernameReqDTO listRoleByUsernameReqDTO = new ListRoleByUsernameReqDTO();
        listRoleByUsernameReqDTO.setUsername(username);
        List<RoleRespDTO> roleRespDTOList = userRoleService.listRoleByUsername(listRoleByUsernameReqDTO).getData();

        roleRespDTOList.stream().forEach(e -> {
            sb.append(e.getRolename()).append(",");
            sb2.append(e.getRoledesc()).append(",");
        });

        log.info("接收到消息={},构建的sb={}", username, sb.toString());

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

}