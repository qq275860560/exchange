package com.ghf.exchange.boss.authorization.role.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.Set;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class UpdateRolePermissionEvent extends ApplicationEvent {

    @Data
    @Builder
    public static class Payload {
        @ApiModelProperty("角色名称，角色英文名称，角色编码，唯一,ROLE_开头")
        private String rolename;

        @ApiModelProperty("权限名称列表，权限英文名称列表，权限编码列表")
        private Set<String> permissionnameSet = Collections.emptySet();
    }

    public UpdateRolePermissionEvent(Payload source) {
        super(source);
    }
}
