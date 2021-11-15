package com.ghf.exchange.boss.authorization.userorg.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateUserOrgEvent extends ApplicationEvent {

    public UpdateUserOrgEvent(String source) {
        super(source);
    }
}
