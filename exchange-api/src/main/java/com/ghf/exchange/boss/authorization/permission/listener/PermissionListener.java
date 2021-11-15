package com.ghf.exchange.boss.authorization.permission.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.permission.dto.GetPermissionByPermissionnameReqDTO;
import com.ghf.exchange.boss.authorization.permission.dto.ListAncestorByPermissionnameReqDTO;
import com.ghf.exchange.boss.authorization.permission.dto.PermissionRespDTO;
import com.ghf.exchange.boss.authorization.permission.dto.TreePermissionReqDTO;
import com.ghf.exchange.boss.authorization.permission.entity.Permission;
import com.ghf.exchange.boss.authorization.permission.event.UpdateFullPermissionEvent;
import com.ghf.exchange.boss.authorization.permission.service.PermissionService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.AutoMapUtils;
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
public class PermissionListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private PermissionService permissionService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void onUpdateFullpermission(UpdateFullPermissionEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("Permission", "RolePermission");
        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = ((GetPermissionByPermissionnameReqDTO) event.getSource());
        String permissionname = getPermissionByPermissionnameReqDTO.getPermissionname();
        updateFullpermission(permissionname);
    }

    private void updateFullpermission(String permissionname) {
        TreePermissionReqDTO treePermissionReqDTO = new TreePermissionReqDTO();
        treePermissionReqDTO.setPermissionname(permissionname);
        clearRedisService.clearPrefixs("Permission");
        PermissionRespDTO permissionRespDTO = permissionService.treePermission(treePermissionReqDTO).getData();

        //重新初始化完整权限id,完整权限英文名称(编码),完整权限中文名称(描述)

        StringBuilder fullPermissionIdStringBuilder = new StringBuilder(",");
        StringBuilder fullPermissionnameStringBuilder = new StringBuilder(",");
        StringBuilder fullPermissiondescStringBuilder = new StringBuilder(",");
        ListAncestorByPermissionnameReqDTO listAncestorByPermissionnameReqDTO = new ListAncestorByPermissionnameReqDTO();
        listAncestorByPermissionnameReqDTO.setPermissionname(permissionRespDTO.getParentPermissionname());
        permissionService.listAncestorByPermissionname(listAncestorByPermissionnameReqDTO).getData().forEach(e -> {
            fullPermissionIdStringBuilder.append(e.getId()).append(",");
            fullPermissionnameStringBuilder.append(e.getPermissionname()).append(",");
            fullPermissiondescStringBuilder.append(e.getPermissiondesc()).append(",");
        });
        fullPermissionIdStringBuilder.append(permissionRespDTO.getId()).append(",");
        fullPermissionnameStringBuilder.append(permissionRespDTO.getPermissionname()).append(",");
        fullPermissiondescStringBuilder.append(permissionRespDTO.getPermissiondesc()).append(",");

        PermissionRespDTO afterPermissionRespDTO = permissionService.getPermissionByPermissionname(GetPermissionByPermissionnameReqDTO.builder().permissionname(permissionRespDTO.getPermissionname()).build()).getData();
        Permission permission = AutoMapUtils.map(afterPermissionRespDTO, Permission.class);
        permission.setFullPermissionId(fullPermissionIdStringBuilder.toString());
        permission.setFullPermissionname(fullPermissionnameStringBuilder.toString());
        permission.setFullPermissiondesc(fullPermissiondescStringBuilder.toString());
        permission.setDeep(permission.getFullPermissionname().split(",").length - 1);
        permissionService.update(permission);
        clearRedisService.clearPrefixs("Permission");

        //递归
        permissionRespDTO.getChildren().forEach(e -> updateFullpermission(e.getPermissionname()));

    }

    @Value("${security.oauth2.client.client-id}")
    public String clientId;

    @Value("${security.oauth2.client.client-secret}")
    public String secret;

}