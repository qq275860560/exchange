package com.ghf.exchange.boss.authorization.userrole.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateUserRoleEvent extends ApplicationEvent {

    public UpdateUserRoleEvent(String source) {
        super(source);
    }
}
