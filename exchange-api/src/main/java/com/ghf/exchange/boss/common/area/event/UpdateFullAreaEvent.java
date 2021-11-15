package com.ghf.exchange.boss.common.area.event;

import com.ghf.exchange.boss.common.area.dto.GetAreaByAreanameReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class UpdateFullAreaEvent extends ApplicationEvent {

    public UpdateFullAreaEvent(GetAreaByAreanameReqDTO source) {
        super(source);
    }
}
