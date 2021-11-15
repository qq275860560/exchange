package com.ghf.exchange.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author jiangyuanlin@163.com
 */
@Lazy
@Configuration
@Slf4j
public class QuerydslConfig {
    @Lazy
    @Resource
    @PersistenceContext
    private EntityManager entityManager;

    @Lazy
    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}


