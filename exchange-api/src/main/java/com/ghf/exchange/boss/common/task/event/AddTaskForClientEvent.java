package com.ghf.exchange.boss.common.task.event;

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
public class AddTaskForClientEvent extends ApplicationEvent {

    public AddTaskForClientEvent() {
        super(IdUtil.generateLongId());
    }
}
