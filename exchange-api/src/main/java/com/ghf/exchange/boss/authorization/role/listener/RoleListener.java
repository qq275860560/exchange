package com.ghf.exchange.boss.authorization.role.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.role.event.UpdateRolePermissionEvent;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
import com.ghf.exchange.boss.authorization.rolepermission.dto.AddRolePermissionReqDTO;
import com.ghf.exchange.boss.authorization.rolepermission.dto.GetRolePermissionByRolenameAndPermissionnameReqDTO;
import com.ghf.exchange.boss.authorization.rolepermission.dto.ListPermissionByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.rolepermission.service.RolePermissionService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class RoleListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private RoleService roleService;

    @Lazy
    @Resource
    private RolePermissionService rolePermissionService;
    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Value("${security.oauth2.client.client-id}")
    public String clientId;

    @Value("${security.oauth2.client.client-secret}")
    public String secret;

    @Async
    @EventListener
    public void onUpdateRolePermissionEvent(UpdateRolePermissionEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("Role", "User", "UserRole", "OrgRole", "RolePermission");
        UpdateRolePermissionEvent.Payload payload = (UpdateRolePermissionEvent.Payload) event.getSource();
        String rolename = payload.getRolename();
        Set<String> permissionnameSet = payload.getPermissionnameSet();

        ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO = ListPermissionByRolenameReqDTO.builder().rolename(rolename).build();
        Set<String> oldPermissionnameSet = rolePermissionService.listPermissionByRolename(listPermissionByRolenameReqDTO).getData().stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getPermissionname()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());
        Iterator<String> oldPermissionnameIterator = oldPermissionnameSet.iterator();
        while (oldPermissionnameIterator.hasNext()) {
            String oldPermissionname = oldPermissionnameIterator.next();
            //本来存在，现在不存在，相当于执行禁用操作
            if (!permissionnameSet.contains(oldPermissionname)) {
                oldPermissionnameIterator.remove();
                GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO = GetRolePermissionByRolenameAndPermissionnameReqDTO.builder().rolename(rolename).permissionname(oldPermissionname).build();
                rolePermissionService.disableRolePermission(getRolePermissionByRolenameAndPermissionnameReqDTO);
            }
        }
        Iterator<String> permissionnameIterator = permissionnameSet.iterator();
        while (permissionnameIterator.hasNext()) {
            String permissionname = permissionnameIterator.next();
            //本来不存在，现在存在，相当于执行新增操作或启用操作
            if (!oldPermissionnameSet.contains(permissionname)) {
                GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO = GetRolePermissionByRolenameAndPermissionnameReqDTO.builder().rolename(rolename).permissionname(permissionname).build();
                boolean b = rolePermissionService.existsRolePermissionByRolenameAndPermissionname(getRolePermissionByRolenameAndPermissionnameReqDTO).getData();
                if (b) {
                    rolePermissionService.enableRolePermission(getRolePermissionByRolenameAndPermissionnameReqDTO);
                } else {
                    AddRolePermissionReqDTO addRolePermissionReqDTO = new AddRolePermissionReqDTO();
                    addRolePermissionReqDTO.setRolename(rolename);
                    addRolePermissionReqDTO.setPermissionname(permissionname);
                    rolePermissionService.addRolePermission(addRolePermissionReqDTO);
                }
            }
        }

    }

}