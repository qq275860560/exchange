package com.ghf.exchange.otc.advertise.event;

import com.ghf.exchange.otc.advertise.dto.UnFreezeAdvertiseAmountReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class UnFreezeAdvertiseAmountEvent extends ApplicationEvent {

    public UnFreezeAdvertiseAmountEvent(UnFreezeAdvertiseAmountReqDTO source) {
        super(source);
    }
}
