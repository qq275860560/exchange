package com.ghf.exchange.boss.authorization.permission.event;

import com.ghf.exchange.boss.authorization.permission.dto.GetPermissionByPermissionnameReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateFullPermissionEvent extends ApplicationEvent {

    public UpdateFullPermissionEvent(GetPermissionByPermissionnameReqDTO source) {
        super(source);
    }
}
