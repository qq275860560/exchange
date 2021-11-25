package com.ghf.exchange.service.impl;

import com.ghf.exchange.dto.PageReqDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.repository.BaseRepository;
import com.ghf.exchange.service.BaseService;
import com.ghf.exchange.util.AutoMapUtils;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 * querydsl学习参考文章:https://blog.csdn.net/weixin_42033269/category_7608855.html
 * jpa学习参考文章:https://www.cnblogs.com/toSeeMyDream/p/6170790.html
 * jpa学习参考文章(英文):https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference
 */
@Slf4j
public class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {

    private BaseRepository<T, ID> baseRepository;

    public BaseServiceImpl(BaseRepository<T, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    public BaseRepository<T, ID> getRepository() {
        return baseRepository;
    }

    @Lazy
    @Resource
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public JPAQueryFactory getJpaQueryFactory() {
        return jpaQueryFactory;
    }

    @Override
    public T get(ID id) {
        return baseRepository.findById(id).orElseGet(() -> null);
    }

    @Override
    public List<T> list(List<ID> ids) {
        return baseRepository.findAllById(ids);
    }

    @Override
    public T get(Predicate predicate) {
        return baseRepository.findOne(predicate).orElseGet(() -> null);
    }

    @Override
    public <R> R get(Predicate predicate, Class<R> outputType) {
        T t = baseRepository.findOne(predicate).orElseGet(() -> null);
        return AutoMapUtils.map(t, outputType);
    }

    @Override
    public List<T> list(Predicate predicate) {
        return Lists.newArrayList(baseRepository.findAll(predicate));
    }

    @Override
    public <R> List<R> list(Predicate predicate, Class<R> outputType) {
        List<T> list = Lists.newArrayList(baseRepository.findAll(predicate));
        return AutoMapUtils.mapForList(list, outputType);
    }

    @Override
    public long count(Predicate predicate) {
        return baseRepository.count(predicate);
    }

    @Override
    public boolean exists(ID id) {
        return baseRepository.existsById(id);
    }

    @Override
    public boolean exists(Predicate predicate) {
        return baseRepository.exists(predicate);
    }

    @Override
    public Page<T> page(Predicate predicate, Pageable pageable) {
        return baseRepository.findAll(predicate, pageable);
    }

    @Override
    public <R> PageRespDTO<R> page(Predicate predicate, PageReqDTO pageReqDTO, Class<R> outputType) {
        Sort sort = null;
        if (pageReqDTO.getSort() == null || pageReqDTO.getSort().isEmpty()) {
            sort = Sort.unsorted();
        } else {
            List<Sort.Order> orders = pageReqDTO.getSort().stream().map(e ->
                    new Sort.Order(Sort.Direction.fromString(e.getDirection()), e.getProperty())
            ).collect(Collectors.toList());
            sort = Sort.by(orders);
        }
        PageRespDTO<R> pageRespDTO  = getPageRespDTO(predicate, pageReqDTO, outputType, sort);
        //refactor page[1,max] 超过当前页边界，返回边界页的列表数据
        if (pageReqDTO.getPageNum() > pageRespDTO.getPages()) {
            pageReqDTO.setPageNum(pageRespDTO.getPages());
            pageRespDTO = getPageRespDTO(predicate, pageReqDTO, outputType, sort);
        }
        return pageRespDTO;
    }

    private <R> PageRespDTO<R> getPageRespDTO(Predicate predicate, PageReqDTO pageReqDTO, Class<R> outputType, Sort sort) {
        Pageable pageable = PageRequest.of(pageReqDTO.getPageNum() - 1, pageReqDTO.getPageSize(), sort);
        Page<T> page = baseRepository.findAll(predicate, pageable);
        List<R> list = AutoMapUtils.mapForList(page.getContent(), outputType);
        return new PageRespDTO<R>(pageReqDTO.getPageNum(), pageReqDTO.getPageSize(), (int) page.getTotalElements(),list);

    }

    @Override
    public T add(T entity) {
        return baseRepository.save(entity);
    }

    @Override
    public List<T> addAll(List<T> entities) {
        return baseRepository.saveAll(entities);
    }

    @Override
    public T delete(ID id) {
        T entity = this.get(id);
        baseRepository.delete(entity);
        return entity;
    }

    @Override
    public List<T> deleteAll(List<ID> ids) {
        List<T> list = this.list(ids);
        baseRepository.deleteAll(list);
        return list;
    }

    @Override
    public T delete(Predicate predicate) {
        T entity = this.get(predicate);
        baseRepository.delete(entity);
        return entity;
    }

    @Override
    public List<T> deleteAll(Predicate predicate) {
        List<T> list = this.list(predicate);
        baseRepository.deleteAll(list);
        return list;
    }

    @Override
    public T update(T entity) {
        return baseRepository.save(entity);
    }

    @Override
    public List<T> updateAll(List<T> entities) {
        return baseRepository.saveAll(entities);
    }

}

