package com.ghf.exchange.otc.orderappeal.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.role.enums.RolenameEnum;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.order.dto.GetOrderByOrderCodeReqDTO;
import com.ghf.exchange.otc.order.dto.OrderRespDTO;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderappeal.dto.*;
import com.ghf.exchange.otc.orderappeal.entity.OrderAppeal;
import com.ghf.exchange.otc.orderappeal.entity.QOrderAppeal;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealStatusEnum;
import com.ghf.exchange.otc.orderappeal.repository.OrderAppealRepository;
import com.ghf.exchange.otc.orderappeal.service.OrderAppealService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderAppealServiceImpl extends BaseServiceImpl<OrderAppeal, Long> implements OrderAppealService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private OrderAppealService appealService;

    @Lazy
    @Resource
    private OrderService orderService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderAppealServiceImpl(OrderAppealRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "OrderAppeal", key = "'pageOrderAppeal:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderAppealCode).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.status) ", condition = "        #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppeal(PageOrderAppealReqDTO pageOrderAppealReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageOrderAppealReqDTO.getOrderAppealCode())) {
            predicate.and(QOrderAppeal.orderAppeal.orderAppealCode.eq(pageOrderAppealReqDTO.getOrderAppealCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderAppealReqDTO.getOrderCode())) {
            predicate.and(QOrderAppeal.orderAppeal.orderCode.eq(pageOrderAppealReqDTO.getOrderCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderAppealReqDTO.getAdvertiseCode())) {
            predicate.and(QOrderAppeal.orderAppeal.advertiseCode.eq(pageOrderAppealReqDTO.getAdvertiseCode()));
        }
        if (pageOrderAppealReqDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode() || pageOrderAppealReqDTO.getStatus() == OrderAppealStatusEnum.AUDIT.getCode()) {
            predicate.and(QOrderAppeal.orderAppeal.status.eq(pageOrderAppealReqDTO.getStatus()));
        }

        PageRespDTO<OrderAppealRespDTO> pageResult = appealService.page(predicate, pageOrderAppealReqDTO, OrderAppealRespDTO.class);

        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "OrderAppeal", key = "'getOrderAppealByOrderAppealCode:' +':'+#p0.orderAppealCode")
    @Override
    @SneakyThrows
    public Result<OrderAppealRespDTO> getOrderAppealByOrderAppealCode(GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO) {

        String orderAppealCode = getOrderAppealByOrderAppealCodeReqDTO.getOrderAppealCode();
        Predicate predicate = QOrderAppeal.orderAppeal.orderAppealCode.eq(orderAppealCode);
        OrderAppeal orderAppeal = appealService.get(predicate);

        //返回
        OrderAppealRespDTO orderAppealRespDTO = AutoMapUtils.map(orderAppeal, OrderAppealRespDTO.class);

        return new Result<>(orderAppealRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderAppealByOrderAppealCode(GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO) {

        String orderAppealCode = getOrderAppealByOrderAppealCodeReqDTO.getOrderAppealCode();
        Predicate predicate = QOrderAppeal.orderAppeal.orderAppealCode.eq(orderAppealCode);
        boolean b = appealService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "OrderAppeal", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrderAppeal(AddOrderAppealReqDTO addOrderAppealReqDTO) {
        OrderAppeal orderAppeal = AutoMapUtils.map(addOrderAppealReqDTO, OrderAppeal.class);

        //初始化id
        orderAppeal.setId(IdUtil.generateLongId());
        //判断申诉编号
        if (!ObjectUtils.isEmpty(orderAppeal.getOrderAppealCode())) {
            //判断唯一性
            String orderAppealCode = addOrderAppealReqDTO.getOrderAppealCode();
            GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
            getOrderAppealByOrderAppealCodeReqDTO.setOrderAppealCode(orderAppealCode);
            boolean b = appealService.existsOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_APPEAL_EXISTS);
            }
            orderAppeal.setOrderAppealCode(addOrderAppealReqDTO.getOrderAppealCode());
        } else {
            //自动生成申诉编号
            orderAppeal.setOrderAppealCode(orderAppeal.getId() + "");
        }

        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderAppealReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (currentLoginUser.getUsername().equals(orderRespDTO.getOrderCustomerUsername())) {
            //订单顾客,有权限申诉
            flag = true;
        } else if (currentLoginUser.getUsername().equals(orderRespDTO.getAdvertiseBusinessUsername())) {
            //广告商家,有权限申诉
            flag = true;
        }
        if (!flag) {
            //无权限取消订单，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        orderAppeal.setOrderCode(orderRespDTO.getOrderCode());
        orderAppeal.setOrderCustomerUsername(orderRespDTO.getOrderCustomerUsername());
        orderAppeal.setAdvertiseCode(orderRespDTO.getAdvertiseCode());
        orderAppeal.setAdvertiseBusinessUsername(orderRespDTO.getAdvertiseBusinessUsername());
        orderAppeal.setOrderAppealContent(addOrderAppealReqDTO.getOrderAppealContent());

        orderAppeal.setOrderAppealUsername(currentLoginUser.getUsername());
        orderAppeal.setCreateTime(new Date());
        orderAppeal.setStatus(OrderAppealStatusEnum.APPEAL.getCode());

        //持久化到数据库
        appealService.add(orderAppeal);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "OrderAppeal", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> auditOrderAppeal(AuditOrderAppealReqDTO auditOrderAppealReqDTO) {

        String orderAppealCode = auditOrderAppealReqDTO.getOrderAppealCode();
        GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getOrderAppealByOrderAppealCodeReqDTO.setOrderAppealCode(orderAppealCode);
        OrderAppealRespDTO orderAppealRespDTO = appealService.getOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO).getData();
        OrderAppeal afterOrderAppeal = AutoMapUtils.map(orderAppealRespDTO, OrderAppeal.class);

        //已申诉状态的申诉才允许被审核
        if (afterOrderAppeal.getStatus() != OrderAppealStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_APPEAL_STATUS_IS_NOT_APPEAL);
        }

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (currentLoginUser.getRolenameSet().contains(RolenameEnum.ROLE_ADMIN.getCode())) {

            //管理员角色的,才有权限审核
            flag = true;
        }
        if (!flag) {
            //无权限取消订单，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrderAppeal.setOrderAppealAuditResult(auditOrderAppealReqDTO.getOrderAppealAuditResult());
        afterOrderAppeal.setOrderAppealAuditUsername(currentLoginUser.getUsername());
        afterOrderAppeal.setOrderAppealAuditTime(new Date());
        afterOrderAppeal.setStatus(OrderAppealStatusEnum.AUDIT.getCode());

        //持久化到数据库
        appealService.update(afterOrderAppeal);

        return new Result<>(ResultCodeEnum.OK);
    }

}