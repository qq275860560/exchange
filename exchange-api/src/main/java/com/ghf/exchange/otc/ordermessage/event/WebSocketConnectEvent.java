package com.ghf.exchange.otc.ordermessage.event;

import com.ghf.exchange.util.IdUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
@Setter
@Slf4j
public class WebSocketConnectEvent extends ApplicationEvent {

    private String username;
    private String sessionId;

    public WebSocketConnectEvent() {
        super(IdUtil.generateLongId());
    }
}
