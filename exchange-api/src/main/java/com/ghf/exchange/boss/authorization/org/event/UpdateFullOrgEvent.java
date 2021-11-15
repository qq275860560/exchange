package com.ghf.exchange.boss.authorization.org.event;

import com.ghf.exchange.boss.authorization.org.dto.GetOrgByOrgnameReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateFullOrgEvent extends ApplicationEvent {

    public UpdateFullOrgEvent(GetOrgByOrgnameReqDTO source) {
        super(source);
    }
}
