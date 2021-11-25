package com.ghf.exchange.otc.ordermessage.config;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.otc.ordermessage.event.WebSocketConnectEvent;
import com.ghf.exchange.otc.ordermessage.event.WebSocketDisConnectEvent;
import com.ghf.exchange.otc.ordermessage.event.WebSocketHeartBeatEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * https://www.it610.com/article/1294179492406829056.htm
 *
 * @author jiangyuanlin@163.com
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {

        //注册一个名为/endpointChat的节点，并指定使用SockJS协议。
        stompEndpointRegistry.addEndpoint("/websocket")
                //.setAllowedOrigins("*")//跨域
                .withSockJS();//开启socketJs
    }

    /**
     * 配置消息代理（Message Broker），可以理解为信息传输的通道
     *
     * @param messageBrokerRegistry MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        //点对点式应增加一个/queue的消息代理。相应的如果是广播室模式可以设置为"/topic"
        //messageBrokerRegistry.enableSimpleBroker("/queue");

        // 自定义调度器，用于控制心跳线程
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 线程池线程数，心跳连接开线程
        taskScheduler.setPoolSize(1);
        // 线程名前缀
        taskScheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
        // 初始化
        taskScheduler.initialize();

        messageBrokerRegistry.enableSimpleBroker("/queue", "/topic")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(taskScheduler);
        //messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
        messageBrokerRegistry.setUserDestinationPrefix("/user");

    }

    @Resource
    public JwtTokenStore jwtTokenStore;

    @Resource
    private ClientService clientService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override

    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelInterceptor channelInterceptor = new ChannelInterceptor() {

            @Override

            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                //连接请求
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    List<String> nativeHeader = accessor.getNativeHeader("Authorization");

                    String authorization = nativeHeader.get(0);

                    String tokenValue = authorization.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();

                    PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(tokenValue, "");

                    if (authentication == null) {
                        log.error("token无效");
                        return null;

                    }

                    OAuth2AccessToken accessToken = jwtTokenStore.readAccessToken(tokenValue);
                    if (accessToken == null) {
                        throw new InvalidTokenException("Invalid access token: " + tokenValue);
                    } else if (accessToken.isExpired()) {
                        jwtTokenStore.removeAccessToken(accessToken);
                        throw new InvalidTokenException("Access token expired: " + tokenValue);
                    }

                    OAuth2Authentication oAuth2Authentication = jwtTokenStore.readAuthentication(accessToken);
                    if (oAuth2Authentication == null) {
                        // in case of race condition
                        throw new InvalidTokenException("Invalid access token: " + tokenValue);
                    }

                    ClientDetails
                            client = clientService.getClientDetailsByClientId(oAuth2Authentication.getOAuth2Request().getClientId());

                    Set<String> allowed = client.getScope();
                    for (String scope : oAuth2Authentication.getOAuth2Request().getScope()) {
                        if (!allowed.contains(scope)) {
                            throw new OAuth2AccessDeniedException(
                                    "Invalid token contains disallowed scope (" + scope + ") for this client");
                        }
                    }

                    oAuth2Authentication.setAuthenticated(true);
                    accessor.setUser(oAuth2Authentication);

                    String username = ((org.springframework.security.core.userdetails.User) oAuth2Authentication.getUserAuthentication().getPrincipal()).getUsername();
                    String sessionId = accessor.getSessionId();
                    WebSocketConnectEvent webSocketConnectEvent = new WebSocketConnectEvent();
                    webSocketConnectEvent.setUsername(username);
                    webSocketConnectEvent.setSessionId(sessionId);
                    applicationEventPublisher.publishEvent(webSocketConnectEvent);
                    return message;
                } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

                    OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) accessor.getUser();
                    String username = ((org.springframework.security.core.userdetails.User) oAuth2Authentication.getUserAuthentication().getPrincipal()).getUsername();
                    String sessionId = accessor.getSessionId();
                    WebSocketDisConnectEvent webSocketDisConnectEvent = new WebSocketDisConnectEvent();
                    webSocketDisConnectEvent.setUsername(username);
                    webSocketDisConnectEvent.setSessionId(sessionId);
                    applicationEventPublisher.publishEvent(webSocketDisConnectEvent);

                    return null;
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    return message;
                } else if (StompCommand.ERROR.equals(accessor.getCommand())) {
                    log.error("发生错误={}", accessor);
                    return null;
                } else {
                    if (SimpMessageType.HEARTBEAT.equals(accessor.getMessageType())) {
                        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) accessor.getUser();
                        String username = ((org.springframework.security.core.userdetails.User) oAuth2Authentication.getUserAuthentication().getPrincipal()).getUsername();
                        String sessionId = accessor.getSessionId();
                        WebSocketHeartBeatEvent webSocketHeartBeatEvent = new WebSocketHeartBeatEvent();
                        webSocketHeartBeatEvent.setUsername(username);
                        webSocketHeartBeatEvent.setSessionId(sessionId);
                        applicationEventPublisher.publishEvent(webSocketHeartBeatEvent);
                        return message;
                    }

                    return message;
                }

            }

        };
        registration.interceptors(channelInterceptor);

    }
}



