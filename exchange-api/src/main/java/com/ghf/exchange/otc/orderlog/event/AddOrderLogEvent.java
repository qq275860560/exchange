package com.ghf.exchange.otc.orderlog.event;

import com.ghf.exchange.otc.orderlog.dto.AddOrderLogReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class AddOrderLogEvent extends ApplicationEvent {

    public AddOrderLogEvent(AddOrderLogReqDTO source) {
        super(source);
    }
}
