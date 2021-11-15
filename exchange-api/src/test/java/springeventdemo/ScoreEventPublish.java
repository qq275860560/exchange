package springeventdemo;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 事件发布（生产者）
 *
 * @author jiangyuanlin@163.com
 */
@Component
public class ScoreEventPublish {
    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void publish(String message) {
        ScoreEvent scoreEvent = new ScoreEvent(message);
        applicationEventPublisher.publishEvent(scoreEvent);
    }
}
