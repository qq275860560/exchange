package com.ghf.exchange.otc.order.event;

import com.ghf.exchange.otc.order.dto.PayOrderReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class PayOrderEvent extends ApplicationEvent {

    public PayOrderEvent(PayOrderReqDTO source) {
        super(source);
    }
}
