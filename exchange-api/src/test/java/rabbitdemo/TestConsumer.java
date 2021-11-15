package rabbitdemo;

import com.ghf.exchange.boss.authorication.user.dto.UpdateUserLastLoginTimeAndLastLoginIpReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Component
@Slf4j
public class TestConsumer {

    @Resource
    private UserService userService;

    /**
     * 监听到更新最后登陆时间和最后登录ip的事件处理器
     *
     * @param updateUserLastLoginTimeAndLastLoginIpReqDTO
     */
    @Async
    @RabbitListener(queues = {QueueConfig.UPDATE_USER_LAST_LOGIN_TIME_AND_LAST_LOGIN_IP})
    @RabbitHandler
    public void updateUserLastLoginTimeAndLastLoginIpHandler(@Payload UpdateUserLastLoginTimeAndLastLoginIpReqDTO updateUserLastLoginTimeAndLastLoginIpReqDTO) {
        log.info("接收到rabbit消息={}", updateUserLastLoginTimeAndLastLoginIpReqDTO);
        //userService.updateUserLastLoginTimeAndLastLoginIp(updateUserLastLoginTimeAndLastLoginIpReqDTO);
    }
}
