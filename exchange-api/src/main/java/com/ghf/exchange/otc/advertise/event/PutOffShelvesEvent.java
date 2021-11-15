package com.ghf.exchange.otc.advertise.event;

import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class PutOffShelvesEvent extends ApplicationEvent {

    public PutOffShelvesEvent(AddAdvertiseLogReqDTO source) {
        super(source);
    }
}
