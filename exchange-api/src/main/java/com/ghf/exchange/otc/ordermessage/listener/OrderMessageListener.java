package com.ghf.exchange.otc.ordermessage.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.otc.ordermessage.dto.OrderMessageRespDTO;
import com.ghf.exchange.otc.ordermessage.enums.OrderMessageRedisKeyEnum;
import com.ghf.exchange.otc.ordermessage.event.AddOrderMessageEvent;
import com.ghf.exchange.otc.ordermessage.event.WebSocketConnectEvent;
import com.ghf.exchange.otc.ordermessage.event.WebSocketDisConnectEvent;
import com.ghf.exchange.otc.ordermessage.event.WebSocketHeartBeatEvent;
import com.ghf.exchange.otc.ordermessage.service.OrderMessageService;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderMessageListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private OrderMessageService orderMessageService;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    //TODO  二级缓存

    @EventListener
    public void onWebSocketConnectSuccess(WebSocketConnectEvent event) {
        /*log.info("接收到消息={}", JsonUtil.toJsonString(event));
        String username = event.getUsername();
        String sessionId = event.getSessionId();
        //连接成功，在线username及sessionid
        redisTemplate.opsForSet().add(OrderMessageRedisKeyEnum.ORDER_MESSAGE.getCode() + ":" + username, sessionId, OrderMessageRedisKeyExpireEnum.SESSION_EXPIRE.getCode(), TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(OrderMessageRedisKeyEnum.ORDER_MESSAGE.getCode() + ":" + username + ":" + sessionId, sessionId, OrderMessageRedisKeyExpireEnum.SESSION_EXPIRE.getCode(), TimeUnit.SECONDS);
        */
    }

    @EventListener
    public void onWebSocketDisConnectSuccess(WebSocketDisConnectEvent event) {
         /*log.info("接收到消息={}", JsonUtil.toJsonString(event));
        String username = event.getUsername();
        String sessionId = event.getSessionId();
        //连接断开，移除redis中的用户
        redisTemplate.opsForSet().remove(OrderMessageRedisKeyEnum.ORDER_MESSAGE.getCode() + ":" + username, sessionId);
        redisTemplate.delete(OrderMessageRedisKeyEnum.ORDER_MESSAGE.getCode() + ":" + username + ":" + sessionId);
 */
    }

    @EventListener
    public void onWebSocketHeartBeat(WebSocketHeartBeatEvent event) {
       /*log.info("接收到消息={}", JsonUtil.toJsonString(event));
        String username = event.getUsername();
        String sessionId = event.getSessionId();
        //心跳，延长时间
        redisTemplate.expire(OrderMessageRedisKeyEnum.ORDER_MESSAGE.getCode() + ":" + username, OrderMessageRedisKeyExpireEnum.SESSION_EXPIRE.getCode(), TimeUnit.SECONDS);
        redisTemplate.expire(OrderMessageRedisKeyEnum.ORDER_MESSAGE.getCode() + ":" + username + ":" + sessionId, OrderMessageRedisKeyExpireEnum.SESSION_EXPIRE.getCode(), TimeUnit.SECONDS);
*/
    }

    /**
     * 接收到本地队列消息，转发到redis全局队列
     *
     * @param event
     */
    @Async
    @EventListener
    public void onAddOrderMessageEvent(AddOrderMessageEvent event) {
        //log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        OrderMessageRespDTO orderMessageRespDTO = ModelMapperUtil.map(event, OrderMessageRespDTO.class);
        //转发到分布式消息队列
        redisTemplate.convertAndSend(OrderMessageRedisKeyEnum.ORDER_MESSAGE_QUEUE.getCode(), orderMessageRespDTO);
    }

    /**
     * 从全局队列监听到消息后，广播给所有的发送方客户端和所有的接收方客户端（APP/浏览器/桌面端）
     *
     * @return
     */
    @Bean
    public MessageListener messageListener() {
        return (message, pattern) -> {
            OrderMessageRespDTO orderMessageRespDTO = JsonUtil.parse(message.getBody(), OrderMessageRespDTO.class);
            simpMessagingTemplate.convertAndSendToUser(orderMessageRespDTO.getOrderMessageReceiverUsername(), "/queue/notifications",
                    orderMessageRespDTO);
            simpMessagingTemplate.convertAndSendToUser(orderMessageRespDTO.getOrderMessageSenderUsername(), "/queue/notifications",
                    orderMessageRespDTO);

        };
    }

}