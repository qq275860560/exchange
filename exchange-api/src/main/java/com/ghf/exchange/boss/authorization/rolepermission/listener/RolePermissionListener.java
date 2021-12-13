package com.ghf.exchange.boss.authorization.rolepermission.listener;

import com.ghf.exchange.boss.authorization.permission.dto.PermissionRespDTO;
import com.ghf.exchange.boss.authorization.role.dto.UpdateRolePermissionnamesByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
import com.ghf.exchange.boss.authorization.rolepermission.dto.ListPermissionByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.rolepermission.event.UpdateRolePermissionEvent;
import com.ghf.exchange.boss.authorization.rolepermission.service.RolePermissionService;
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
public class RolePermissionListener {

    @Lazy
    @Resource
    private RoleService roleService;

    @Lazy
    @Resource
    private RolePermissionService rolePermissionService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void updateRolePermission(UpdateRolePermissionEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("Role", "Permission", "RolePermission");
        String rolename = (String) event.getSource();

        ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO = new ListPermissionByRolenameReqDTO();
        listPermissionByRolenameReqDTO.setRolename(rolename);
        List<PermissionRespDTO> list = rolePermissionService.listPermissionByRolename(listPermissionByRolenameReqDTO).getData();

        StringBuilder sb = new StringBuilder(",");
        StringBuilder sb2 = new StringBuilder(",");
        list.stream().forEach(e -> {
            sb.append(e.getPermissionname()).append(",");
            sb2.append(e.getPermissiondesc()).append(",");
        });

        String permissionnames = sb.toString();
        String permissiondescs = sb2.toString();
        UpdateRolePermissionnamesByRolenameReqDTO updateRolePermissionnamesByRolenameReqDTO = new UpdateRolePermissionnamesByRolenameReqDTO();
        updateRolePermissionnamesByRolenameReqDTO.setRolename(rolename);
        updateRolePermissionnamesByRolenameReqDTO.setPermissionnames(permissionnames);
        updateRolePermissionnamesByRolenameReqDTO.setPermissiondescs(permissiondescs);
        roleService.updateRolePermissionnamesByRolename(updateRolePermissionnamesByRolenameReqDTO);
    }

}