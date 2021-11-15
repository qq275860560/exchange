package rabbitdemo;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.UpdateUserLastLoginTimeAndLastLoginIpReqDTO;
import com.ghf.exchange.boss.authorication.user.util.IpUtil;
import com.ghf.exchange.config.RabbitConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class TestProducer {

    @Lazy
    @Resource
    private RabbitTemplate rabbitTemplate;

    @SneakyThrows
    @Test
    public void test() {

        String username = "admin";
        /**********生产者:推送事件到消息队列START**********/
        UpdateUserLastLoginTimeAndLastLoginIpReqDTO updateUserLastLoginTimeAndLastLoginIpDto = new UpdateUserLastLoginTimeAndLastLoginIpReqDTO();
        updateUserLastLoginTimeAndLastLoginIpDto.setUsername(username);
        updateUserLastLoginTimeAndLastLoginIpDto.setLastLoginTime(new Date());
        updateUserLastLoginTimeAndLastLoginIpDto.setLastLoginIp(IpUtil.getIpAddr());
        rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE_NAME, QueueConfig.UPDATE_USER_LAST_LOGIN_TIME_AND_LAST_LOGIN_IP, updateUserLastLoginTimeAndLastLoginIpDto);
        /**********生产者:推送事件到消息队列END**********/

        Assert.assertTrue(1 == 1);
    }

}
