package com.ghf.exchange.otc.order.event;

import com.ghf.exchange.otc.order.dto.RecoverOrderReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class RecoverOrderEvent extends ApplicationEvent {

    public RecoverOrderEvent(RecoverOrderReqDTO source) {
        super(source);
    }
}
