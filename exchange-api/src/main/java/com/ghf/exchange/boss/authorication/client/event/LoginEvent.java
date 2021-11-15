package com.ghf.exchange.boss.authorication.client.event;

import com.ghf.exchange.boss.authorication.client.dto.LoginClientReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class LoginEvent extends ApplicationEvent {

    public LoginEvent(LoginClientReqDTO source) {
        super(source);
    }
}
