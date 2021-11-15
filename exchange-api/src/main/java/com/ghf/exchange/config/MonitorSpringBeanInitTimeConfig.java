package com.ghf.exchange.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监控启动项目时spring bean 实例化的耗时
 * 仅使用在开发测试环境
 *
 * @author jiangyuanlin@163.com
 */
@Configuration
@Slf4j
public class MonitorSpringBeanInitTimeConfig implements BeanPostProcessor {
    private static final Map<String, Long> COST_MAP = new ConcurrentHashMap<>();

    private static final int DURATION = 100;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        COST_MAP.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        long start = COST_MAP.get(beanName);
        long cost = System.currentTimeMillis() - start;
        if (cost > DURATION) {
            log.warn("类: {}, 实例: {}, spring bean 实例化的耗时: {}毫秒", bean.getClass().getName(), beanName, cost);
        }
        return bean;
    }
}
