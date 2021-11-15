package com.ghf.exchange.boss.authorization.rolepermission.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateRolePermissionEvent extends ApplicationEvent {

    public UpdateRolePermissionEvent(String source) {
        super(source);
    }
}
