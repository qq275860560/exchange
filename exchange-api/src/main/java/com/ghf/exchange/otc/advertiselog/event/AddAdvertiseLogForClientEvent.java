package com.ghf.exchange.otc.advertiselog.event;

import com.ghf.exchange.util.IdUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Setter
@Getter
@Slf4j
public class AddAdvertiseLogForClientEvent extends ApplicationEvent {

    public AddAdvertiseLogForClientEvent() {
        super(IdUtil.generateLongId());
    }
}
