package com.ghf.exchange.boss.authorization.orgrole.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateOrgRoleEvent extends ApplicationEvent {

    public UpdateOrgRoleEvent(String source) {
        super(source);
    }
}
