package com.ghf.exchange.otc.ordermessage.event;

import com.ghf.exchange.otc.ordermessage.entity.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class AddOrderMessageEvent extends ApplicationEvent {

    public AddOrderMessageEvent(OrderMessage source) {
        super(source);
    }
}
