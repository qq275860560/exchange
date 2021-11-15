package com.ghf.exchange.repository.impl;

import com.ghf.exchange.repository.BaseRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author jiangyuanlin@163.com
 */
public class BaseRepositoryImpl<T, ID extends Serializable> extends
        SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private QuerydslJpaPredicateExecutor querydslJpaPredicateExecutor;

    public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        querydslJpaPredicateExecutor = new QuerydslJpaPredicateExecutor(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE, super.getRepositoryMethodMetadata());
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        return querydslJpaPredicateExecutor.findOne(predicate);
    }

    @Override
    public Iterable<T> findAll(Predicate predicate) {
        return querydslJpaPredicateExecutor.findAll(predicate);
    }

    @Override
    public Iterable<T> findAll(Predicate predicate, Sort sort) {
        return querydslJpaPredicateExecutor.findAll(predicate, sort);
    }

    @Override
    public Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        return querydslJpaPredicateExecutor.findAll(predicate, orders);
    }

    @Override
    public Iterable<T> findAll(OrderSpecifier<?>... orders) {
        return querydslJpaPredicateExecutor.findAll(orders);
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {
        return querydslJpaPredicateExecutor.findAll(predicate, pageable);
    }

    @Override
    public long count(Predicate predicate) {
        return querydslJpaPredicateExecutor.count(predicate);
    }

    @Override
    public boolean exists(Predicate predicate) {
        return querydslJpaPredicateExecutor.exists(predicate);
    }
}
