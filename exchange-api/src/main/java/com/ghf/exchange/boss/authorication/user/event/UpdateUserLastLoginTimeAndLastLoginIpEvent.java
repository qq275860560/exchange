package com.ghf.exchange.boss.authorication.user.event;

import com.ghf.exchange.boss.authorication.user.dto.UpdateUserLastLoginTimeAndLastLoginIpReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateUserLastLoginTimeAndLastLoginIpEvent extends ApplicationEvent {

    public UpdateUserLastLoginTimeAndLastLoginIpEvent(UpdateUserLastLoginTimeAndLastLoginIpReqDTO source) {
        super(source);
    }
}
