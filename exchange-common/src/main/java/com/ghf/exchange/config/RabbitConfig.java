package com.ghf.exchange.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;


/**
 * @author jiangyuanlin@163.com
 * @Lazy
 * @EnableRabbit 暂时使用本地spring事件监听代替rabbit
 * @Configuration
 */
@Slf4j
public class RabbitConfig implements ApplicationRunner {

    /**
     * 主题交换机
     */
    public static final String TOPIC_EXCHANGE_NAME = "com.ghf.exchange.exchange";

    @Lazy
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    /**
     * rabbitTemplate
     */

    @Lazy
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        log.info("初始化rabbit开始:修改rabbittemplate处理策略");
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {
            log.debug("消息发送成功,correlationData={},ack={},cause={}", correlationData, ack, cause);
        });

        rabbitTemplate.setReturnsCallback((ReturnedMessage returnedMessage ) -> {
            log.error("消息发送失败,message={},replyCode={},replyText={},exchange={},routingKey={}", returnedMessage.getMessage(), returnedMessage.getReplyCode(),returnedMessage.getReplyText(),returnedMessage.getExchange(),returnedMessage.getRoutingKey());
        });
        log.info("初始化rabbit结束");

    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}