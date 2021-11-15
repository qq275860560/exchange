package com.ghf.exchange.boss.authorication.user.event;

import com.ghf.exchange.boss.authorication.user.dto.AddUserReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class AddUserEvent extends ApplicationEvent {

    public AddUserEvent(AddUserReqDTO source) {
        super(source);
    }
}
