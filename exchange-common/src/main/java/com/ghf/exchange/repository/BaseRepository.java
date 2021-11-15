package com.ghf.exchange.repository;

import com.ghf.exchange.repository.impl.BaseRepositoryImpl;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author jiangyuanlin@163.com
 */
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class, basePackages = {"com.ghf.**.repository"})
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepositoryImplementation<T, ID>, QuerydslPredicateExecutor<T> {

}
