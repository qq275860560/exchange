package springeventdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 事件监听(消费者)
 *
 * @author jiangyuanlin@163.com
 */
@Component
@Slf4j
public class ScroeEventListener {
    @EventListener
    public void onApplicationEvent(ScoreEvent event) {
        log.info("接收到消息={}", (String) event.getSource());
    }
}
