package com.ghf.exchange.otc.ordermessage.config;

import com.ghf.exchange.otc.ordermessage.enums.OrderMessageRedisKeyEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Component
@Lazy
@Slf4j
public class OrderMessageQueueConfig {

    @Resource
    private RedisMessageListenerContainer redisMessageListenerContainer;
    @Resource
    private MessageListener messageListener;

    @EventListener
    @SneakyThrows
    public void init(ContextRefreshedEvent event) {
        redisMessageListenerContainer.addMessageListener(messageListener, new PatternTopic(OrderMessageRedisKeyEnum.ORDER_MESSAGE_QUEUE.getCode()));
    }

}
