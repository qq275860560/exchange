package com.ghf.exchange.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 监听ContextRefreshedEvent事件，并清除redis指定key
 * 仅使用在开发测试环境
 *
 * @author jiangyuanlin@163.com
 */
@Component
@Lazy
@Slf4j
public class ClearRedisConfig {

    public static final List<String> CACHE_KEY_PREFIX_LIST = Arrays.asList(
            "Client", "User", "Role", "Org", "Permission", "UserRole", "UserOrg", "OrgRole", "RolePermission"
            , "Dict", "Area", "Advertise", "Order"
    );
    @Resource
    private RedisTemplate redisTemplate;

    @EventListener
    @SneakyThrows
    public void init(ContextRefreshedEvent event) {
        log.info("接收到 : {},开始清理缓存", event);

        for (String prefix : CACHE_KEY_PREFIX_LIST) {
            this.clearPrefix(prefix);
        }

    }

    public void clearPrefix(String prefix) {
        RedisConnection connection = redisTemplate
                .getConnectionFactory().getConnection();

        String pattern = prefix + "*";
        Set<byte[]> caches = connection.keys(pattern.getBytes());
        if (!caches.isEmpty()) {
            connection.del(caches.toArray(new byte[][]{}));
        }

    }

    public void clearPrefixs(String... prefixs) {
        for (String prefix : prefixs) {
            this.clearPrefix(prefix);
        }

    }

}
