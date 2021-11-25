package com.ghf.exchange.service;

import com.ghf.exchange.dto.PageReqDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.repository.BaseRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
public interface BaseService<T, ID extends Serializable> {

    /**
     * 获取repository，方便进行单表增删改查
     *
     * @return
     */
    BaseRepository<T, ID> getRepository();

    /**
     * 获取jpaQueryFactory 方便进行关联查询，可参考这篇文章https://blog.csdn.net/weixin_42033269/category_7608855.html
     *
     * @return
     */
    JPAQueryFactory getJpaQueryFactory();

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    T get(ID id);

    /**
     * 获取列表
     *
     * @param ids
     * @return
     */
    List<T> list(List<ID> ids);

    /**
     * 获取详情
     *
     * @param predicate
     * @return
     */
    T get(Predicate predicate);

    /**
     * 获取详情
     *
     * @param predicate
     * @param outputType 返回的dto类型
     * @return
     */
    <R> R get(Predicate predicate, Class<R> outputType);

    /**
     * 获取列表
     *
     * @param predicate
     * @return
     */
    List<T> list(Predicate predicate);

    /**
     * 获取列表
     *
     * @param predicate  查询条件
     * @param outputType 返回的dto类型
     * @return
     */
    <R> List<R> list(Predicate predicate, Class<R> outputType);

    /**
     * 判断是否存在
     *
     * @param id
     * @return
     */
    boolean exists(ID id);

    /**
     * 判断是否存在
     *
     * @param predicate
     * @return
     */
    boolean exists(Predicate predicate);

    /**
     * 统计
     *
     * @param predicate
     * @return
     */
    long count(Predicate predicate);

    /**
     * 使用spring内置的分页对象进行分页查询
     *
     * @param predicate
     * @param pageable
     * @return
     */
    Page<T> page(Predicate predicate, Pageable pageable);

    /**
     * 使用autumn的分页对象进行分页查询
     *
     * @param predicate       查询条件
     * @param pageQueryReqDTO 包含要查询的页号，每页数量，排序规则
     * @param outputType      返回的dto类型
     * @return
     */
    <R> PageRespDTO<R> page(Predicate predicate, PageReqDTO pageQueryReqDTO, Class<R> outputType);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    T add(T entity);

    /**
     * 批量保存
     *
     * @param entities
     * @return
     */
    List<T> addAll(List<T> entities);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    T delete(ID id);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    List<T> deleteAll(List<ID> ids);

    /**
     * 删除
     *
     * @param predicate
     * @return
     */
    T delete(Predicate predicate);

    /**
     * 批量删除
     *
     * @param predicate
     * @return
     */
    List<T> deleteAll(Predicate predicate);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    T update(T entity);

    /**
     * 批量更新
     *
     * @param entities
     * @return
     */
    List<T> updateAll(List<T> entities);

}
