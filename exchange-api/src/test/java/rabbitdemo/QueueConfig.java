package rabbitdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiangyuanlin@163.com
 */
@Configuration
@Slf4j
public class QueueConfig {

    /**
     * 队列名称,路由键名称
     */
    public static final String UPDATE_USER_LAST_LOGIN_TIME_AND_LAST_LOGIN_IP = "updateUserLastLoginTimeAndLastLoginIp";

    /**
     * 队列
     */
    @Bean(name = UPDATE_USER_LAST_LOGIN_TIME_AND_LAST_LOGIN_IP)
    public Queue updateUserLastLoginInfoQueue() {
        return new Queue(QueueConfig.UPDATE_USER_LAST_LOGIN_TIME_AND_LAST_LOGIN_IP);
    }

    /**
     * 绑定
     */
    @Bean
    public Binding updateUserLastLoginInfoBinding(TopicExchange topicExchange) {
        return BindingBuilder.bind(updateUserLastLoginInfoQueue()).to(topicExchange).with(UPDATE_USER_LAST_LOGIN_TIME_AND_LAST_LOGIN_IP);
    }

}
