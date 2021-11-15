package com.ghf.exchange.otc.advertise.event;

import com.ghf.exchange.otc.advertise.dto.DecAdvertiseFrozenAmountReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class DecAdvertiseFrozenAmountEvent extends ApplicationEvent {

    public DecAdvertiseFrozenAmountEvent(DecAdvertiseFrozenAmountReqDTO source) {
        super(source);
    }
}
