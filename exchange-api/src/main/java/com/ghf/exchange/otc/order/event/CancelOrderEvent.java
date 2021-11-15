package com.ghf.exchange.otc.order.event;

import com.ghf.exchange.otc.order.dto.CancelOrderReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class CancelOrderEvent extends ApplicationEvent {

    public CancelOrderEvent(CancelOrderReqDTO source) {
        super(source);
    }
}
