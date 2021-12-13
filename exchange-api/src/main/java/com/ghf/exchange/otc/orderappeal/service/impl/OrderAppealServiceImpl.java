package com.ghf.exchange.otc.orderappeal.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.order.dto.GetOrderByOrderCodeReqDTO;
import com.ghf.exchange.otc.order.dto.OrderRespDTO;
import com.ghf.exchange.otc.order.enums.OrderBuySellTypeEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderappeal.dto.*;
import com.ghf.exchange.otc.orderappeal.entity.OrderAppeal;
import com.ghf.exchange.otc.orderappeal.entity.QOrderAppeal;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealStatusEnum;
import com.ghf.exchange.otc.orderappeal.enums.OrderAppealTypeEnum;
import com.ghf.exchange.otc.orderappeal.event.AddOrderAppealEvent;
import com.ghf.exchange.otc.orderappeal.event.AuditOrderAppealForAdminEvent;
import com.ghf.exchange.otc.orderappeal.event.CancelOrderAppealEvent;
import com.ghf.exchange.otc.orderappeal.repository.OrderAppealRepository;
import com.ghf.exchange.otc.orderappeal.service.OrderAppealService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private OrderAppealService orderAppealService;

    @Lazy
    @Resource
    private OrderService orderService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Value("${maxPayTime}")
    private long maxPayTime;
    @Value("${maxReleaseTime}")
    private long maxReleaseTime;

    public OrderAppealServiceImpl(OrderAppealRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "OrderAppeal", key = "'pageOrderAppeal:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderAppealCode).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.status) ", condition = "        #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppeal(PageOrderAppealReqDTO pageOrderAppealReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrderAppeal.orderAppeal.advertiseBusinessUsername.eq(currentLoginUser.getUsername()).or(QOrderAppeal.orderAppeal.orderCustomerUsername.eq(currentLoginUser.getUsername())));

        if (!ObjectUtils.isEmpty(pageOrderAppealReqDTO.getOrderAppealCode())) {
            predicate.and(QOrderAppeal.orderAppeal.orderAppealCode.eq(pageOrderAppealReqDTO.getOrderAppealCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderAppealReqDTO.getOrderCode())) {
            predicate.and(QOrderAppeal.orderAppeal.orderCode.eq(pageOrderAppealReqDTO.getOrderCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderAppealReqDTO.getAdvertiseCode())) {
            predicate.and(QOrderAppeal.orderAppeal.advertiseCode.eq(pageOrderAppealReqDTO.getAdvertiseCode()));
        }
        if (pageOrderAppealReqDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode() || pageOrderAppealReqDTO.getStatus() == OrderAppealStatusEnum.CANCEL.getCode() || pageOrderAppealReqDTO.getStatus() == OrderAppealStatusEnum.FAIL.getCode() || pageOrderAppealReqDTO.getStatus() == OrderAppealStatusEnum.SUCCESS.getCode()) {
            predicate.and(QOrderAppeal.orderAppeal.status.eq(pageOrderAppealReqDTO.getStatus()));
        }

        PageRespDTO<OrderAppealRespDTO> pageRespDTO = orderAppealService.page(predicate, pageOrderAppealReqDTO, OrderAppealRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "OrderAppeal", key = "'pageOrderAppealForAdmin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderAppealCode).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.status) ", condition = " T(org.springframework.util.StringUtils).isEmpty(#p0.username) &&       #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppealForAdmin(PageOrderAppealForAdminReqDTO pageOrderAppealForAdminReqDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageOrderAppealForAdminReqDTO.getUsername())) {
            predicate.and(QOrderAppeal.orderAppeal.advertiseBusinessUsername.eq(pageOrderAppealForAdminReqDTO.getUsername()).or(QOrderAppeal.orderAppeal.orderCustomerUsername.eq(pageOrderAppealForAdminReqDTO.getUsername())));
        }
        if (!ObjectUtils.isEmpty(pageOrderAppealForAdminReqDTO.getOrderAppealCode())) {
            predicate.and(QOrderAppeal.orderAppeal.orderAppealCode.eq(pageOrderAppealForAdminReqDTO.getOrderAppealCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderAppealForAdminReqDTO.getOrderCode())) {
            predicate.and(QOrderAppeal.orderAppeal.orderCode.eq(pageOrderAppealForAdminReqDTO.getOrderCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderAppealForAdminReqDTO.getAdvertiseCode())) {
            predicate.and(QOrderAppeal.orderAppeal.advertiseCode.eq(pageOrderAppealForAdminReqDTO.getAdvertiseCode()));
        }
        if (pageOrderAppealForAdminReqDTO.getStatus() == OrderAppealStatusEnum.APPEAL.getCode() || pageOrderAppealForAdminReqDTO.getStatus() == OrderAppealStatusEnum.CANCEL.getCode() || pageOrderAppealForAdminReqDTO.getStatus() == OrderAppealStatusEnum.FAIL.getCode() || pageOrderAppealForAdminReqDTO.getStatus() == OrderAppealStatusEnum.SUCCESS.getCode()) {
            predicate.and(QOrderAppeal.orderAppeal.status.eq(pageOrderAppealForAdminReqDTO.getStatus()));
        }

        PageRespDTO<OrderAppealRespDTO> pageRespDTO = orderAppealService.page(predicate, pageOrderAppealForAdminReqDTO, OrderAppealRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "OrderAppeal", key = "'getOrderAppealByOrderAppealCode:' +':'+#p0.orderAppealCode")
    @Override
    @SneakyThrows
    public Result<OrderAppealRespDTO> getOrderAppealByOrderAppealCode(GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO) {

        String orderAppealCode = getOrderAppealByOrderAppealCodeReqDTO.getOrderAppealCode();
        Predicate predicate = QOrderAppeal.orderAppeal.orderAppealCode.eq(orderAppealCode);
        OrderAppeal orderAppeal = orderAppealService.get(predicate);

        //返回
        OrderAppealRespDTO orderAppealRespDTO = ModelMapperUtil.map(orderAppeal, OrderAppealRespDTO.class);

        return new Result<>(orderAppealRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderAppealByOrderAppealCode(GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO) {

        String orderAppealCode = getOrderAppealByOrderAppealCodeReqDTO.getOrderAppealCode();
        Predicate predicate = QOrderAppeal.orderAppeal.orderAppealCode.eq(orderAppealCode);
        boolean b = orderAppealService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "OrderAppeal", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrderAppeal(AddOrderAppealReqDTO addOrderAppealReqDTO) {
        OrderAppeal orderAppeal = ModelMapperUtil.map(addOrderAppealReqDTO, OrderAppeal.class);

        //初始化id
        orderAppeal.setId(IdUtil.generateLongId());
        //判断申诉编号
        if (!ObjectUtils.isEmpty(orderAppeal.getOrderAppealCode())) {
            //判断唯一性
            String orderAppealCode = addOrderAppealReqDTO.getOrderAppealCode();
            GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
            getOrderAppealByOrderAppealCodeReqDTO.setOrderAppealCode(orderAppealCode);
            boolean b = orderAppealService.existsOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO).getData();
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
        //已付款状态的订单才允许申诉
        if (orderRespDTO.getStatus() != OrderStatusEnum.PAY.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_PAY);
        }

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (username.equals(orderRespDTO.getOrderCustomerUsername())) {
            //订单顾客,有权限
            flag = true;
        } else if (username.equals(orderRespDTO.getAdvertiseBusinessUsername())) {
            //广告商家,有权限
            flag = true;
        }
        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        orderAppeal.setOrderCode(orderRespDTO.getOrderCode());

        //判断申诉类型
        if (addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.UN_PAY.getCode() && addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.UN_RELEASE.getCode() && addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.OTHER.getCode()) {
            return new Result<>(ResultCodeEnum.OTHER_APPEAL_TYPE_NOT_EXISTS);
        }

        //买币订单时，订单顾客作为买方有权限提交对方未放行申诉,相应的，广告商家作为卖方有权限提交对方未付款申诉
        if (addOrderAppealReqDTO.getOrderAppealType() == OrderAppealTypeEnum.UN_PAY.getCode() || addOrderAppealReqDTO.getOrderAppealType() == OrderAppealTypeEnum.UN_RELEASE.getCode()) {
            if (orderRespDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                if (username.equals(orderRespDTO.getOrderCustomerUsername())) {
                    if (addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.UN_RELEASE.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_APPEAL_TYPE_IS_NOT_UN_RELEASE);
                    }
                    //订单状态不是付款状态，无法申诉未放行
                    if (orderRespDTO.getStatus() != OrderStatusEnum.PAY.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_PAY);
                    }
                    //订单放行时间不逾期，无法申诉未放行
                    if (orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode() && orderRespDTO.getPayTime().getTime() + maxReleaseTime > System.currentTimeMillis()) {
                        return new Result<>(ResultCodeEnum.ORDER_RELEASE_TIME_LEGAL);
                    }

                } else if (username.equals(orderRespDTO.getAdvertiseBusinessUsername())) {
                    if (addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.UN_PAY.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_APPEAL_TYPE_IS_NOT_UN_PAY);
                    }
                    //订单状态不是下单状态且未付款状态，无法申诉未付款
                    if (orderRespDTO.getStatus() != OrderStatusEnum.ADD.getCode() && orderRespDTO.getStatus() != OrderStatusEnum.PAY.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD_OR_PAY);
                    }

                    //订单付款时间不逾期，无法申诉未付款
                    if (orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode() && orderRespDTO.getPayTime().getTime() + maxPayTime > System.currentTimeMillis()) {
                        return new Result<>(ResultCodeEnum.ORDER_PAY_TIME_LEGAL);
                    }
                }
            } else if (orderRespDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
                //卖币订单时，订单顾客作为卖方有权限提交对方未付款申诉,相应的，广告商家作为买方有权限提交对方未放行申诉
                if (username.equals(orderRespDTO.getOrderCustomerUsername())) {
                    if (addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.UN_PAY.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_APPEAL_TYPE_IS_NOT_UN_PAY);
                    }
                    //订单状态不是下单状态且未付款状态，无法申诉未付款
                    if (orderRespDTO.getStatus() != OrderStatusEnum.ADD.getCode() && orderRespDTO.getStatus() != OrderStatusEnum.PAY.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD_OR_PAY);
                    }

                    //订单付款时间不逾期，无法申诉未付款
                    if (orderRespDTO.getStatus() == OrderStatusEnum.ADD.getCode() && orderRespDTO.getPayTime().getTime() + maxPayTime > System.currentTimeMillis()) {
                        return new Result<>(ResultCodeEnum.ORDER_PAY_TIME_LEGAL);
                    }

                } else if (username.equals(orderRespDTO.getAdvertiseBusinessUsername())) {
                    if (addOrderAppealReqDTO.getOrderAppealType() != OrderAppealTypeEnum.UN_RELEASE.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_APPEAL_TYPE_IS_NOT_UN_RELEASE);
                    }
                    //订单状态不是付款状态，无法申诉未放行
                    if (orderRespDTO.getStatus() != OrderStatusEnum.PAY.getCode()) {
                        return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_PAY);
                    }
                    //订单放行时间不逾期，无法申诉未放行
                    if (orderRespDTO.getStatus() == OrderStatusEnum.PAY.getCode() && orderRespDTO.getPayTime().getTime() + maxReleaseTime > System.currentTimeMillis()) {
                        return new Result<>(ResultCodeEnum.ORDER_RELEASE_TIME_LEGAL);
                    }

                }

            }
        }

        //设置申诉类型
        orderAppeal.setOrderAppealType(addOrderAppealReqDTO.getOrderAppealType());

        orderAppeal.setOrderOldStatus(orderRespDTO.getStatus());
        orderAppeal.setOrderCustomerUsername(orderRespDTO.getOrderCustomerUsername());
        orderAppeal.setAdvertiseCode(orderRespDTO.getAdvertiseCode());
        orderAppeal.setAdvertiseBusinessUsername(orderRespDTO.getAdvertiseBusinessUsername());
        orderAppeal.setOrderAppealContent(addOrderAppealReqDTO.getOrderAppealContent());

        orderAppeal.setOrderAppealUsername(currentLoginUser.getUsername());
        orderAppeal.setCreateTime(new Date());
        orderAppeal.setStatus(OrderAppealStatusEnum.APPEAL.getCode());

        //持久化到数据库
        orderAppealService.add(orderAppeal);

        //发送到消息队列
        AddOrderAppealEvent addOrderAppealEvent = ModelMapperUtil.map(orderRespDTO, AddOrderAppealEvent.class);
        addOrderAppealEvent.setOrderCode(addOrderAppealReqDTO.getOrderCode());
        addOrderAppealEvent.setOrderAppealType(addOrderAppealReqDTO.getOrderAppealType());
        applicationEventPublisher.publishEvent(addOrderAppealEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "OrderAppeal", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> auditOrderAppealForAdmin(AuditOrderAppealForAdminReqDTO auditOrderAppealForAdminReqDTO) {

        String orderAppealCode = auditOrderAppealForAdminReqDTO.getOrderAppealCode();
        GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getOrderAppealByOrderAppealCodeReqDTO.setOrderAppealCode(orderAppealCode);
        OrderAppealRespDTO orderAppealRespDTO = orderAppealService.getOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO).getData();
        OrderAppeal afterOrderAppeal = ModelMapperUtil.map(orderAppealRespDTO, OrderAppeal.class);

        //已申诉状态的申诉才允许被审核
        if (afterOrderAppeal.getStatus() != OrderAppealStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_APPEAL_STATUS_IS_NOT_APPEAL);
        }

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        afterOrderAppeal.setOrderAppealAuditResult(auditOrderAppealForAdminReqDTO.getOrderAppealAuditResult());
        afterOrderAppeal.setOrderAppealAuditUsername(currentLoginUser.getUsername());
        afterOrderAppeal.setOrderAppealAuditTime(new Date());
        //判断审核状态
        if (auditOrderAppealForAdminReqDTO.getStatus() != OrderAppealStatusEnum.FAIL.getCode() && auditOrderAppealForAdminReqDTO.getStatus() != OrderAppealStatusEnum.SUCCESS.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_APPEAL_STATUS_IS_NOT_FAIL_OR_SUCCESS);
        }
        //设置审核状态

        afterOrderAppeal.setStatus(auditOrderAppealForAdminReqDTO.getStatus());
        afterOrderAppeal.setOrderAppealAuditResult(auditOrderAppealForAdminReqDTO.getOrderAppealAuditResult());

        //持久化到数据库
        orderAppealService.update(afterOrderAppeal);

        //发送到消息队列
        AuditOrderAppealForAdminEvent auditOrderAppealForAdminEvent = new AuditOrderAppealForAdminEvent();
        auditOrderAppealForAdminEvent.setOrderAppealType(afterOrderAppeal.getOrderAppealType());
        auditOrderAppealForAdminEvent.setOrderCode(afterOrderAppeal.getOrderCode());
        auditOrderAppealForAdminEvent.setStatus(afterOrderAppeal.getStatus());
        auditOrderAppealForAdminEvent.setOrderOldStatus(afterOrderAppeal.getOrderOldStatus());
        applicationEventPublisher.publishEvent(auditOrderAppealForAdminEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "OrderAppeal", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> cancelOrderAppeal(CancelOrderAppealReqDTO cancelOrderAppealReqDTO) {

        String orderAppealCode = cancelOrderAppealReqDTO.getOrderAppealCode();
        GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO = new GetOrderAppealByOrderAppealCodeReqDTO();
        getOrderAppealByOrderAppealCodeReqDTO.setOrderAppealCode(orderAppealCode);
        OrderAppealRespDTO orderAppealRespDTO = orderAppealService.getOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO).getData();
        OrderAppeal afterOrderAppeal = ModelMapperUtil.map(orderAppealRespDTO, OrderAppeal.class);

        //已申诉状态的申诉才允许被取消
        if (afterOrderAppeal.getStatus() != OrderAppealStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_APPEAL_STATUS_IS_NOT_APPEAL);
        }

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        if (currentLoginUser.getUsername().equals(afterOrderAppeal.getOrderAppealUsername())) {
            //创建人才有权限
            flag = true;
        }
        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrderAppeal.setOrderAppealCancelResult(cancelOrderAppealReqDTO.getOrderAppealCancelResult());
        afterOrderAppeal.setOrderAppealCanelUsername(currentLoginUser.getUsername());
        afterOrderAppeal.setOrderAppealCanelTime(new Date());
        afterOrderAppeal.setStatus(OrderAppealStatusEnum.CANCEL.getCode());

        //持久化到数据库
        orderAppealService.update(afterOrderAppeal);

        //发送到消息队列
        CancelOrderAppealEvent cancelOrderAppealEvent = new CancelOrderAppealEvent();
        cancelOrderAppealEvent.setOrderCode(afterOrderAppeal.getOrderCode());
        cancelOrderAppealEvent.setOrderOldStatus(afterOrderAppeal.getOrderOldStatus());
        applicationEventPublisher.publishEvent(cancelOrderAppealEvent);

        return new Result<>(ResultCodeEnum.OK);
    }
}