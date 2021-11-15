package com.ghf.exchange.boss.authorication.user.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateUserByUsernameEvent extends ApplicationEvent {

    public UpdateUserByUsernameEvent(UpdateUserByUsernameReqDTO source) {
        super(source);
    }
}
