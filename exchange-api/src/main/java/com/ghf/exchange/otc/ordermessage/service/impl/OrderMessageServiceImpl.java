package com.ghf.exchange.otc.ordermessage.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.order.dto.GetOrderByOrderCodeReqDTO;
import com.ghf.exchange.otc.order.dto.OrderRespDTO;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.ordermessage.dto.*;
import com.ghf.exchange.otc.ordermessage.entity.OrderMessage;
import com.ghf.exchange.otc.ordermessage.entity.QOrderMessage;
import com.ghf.exchange.otc.ordermessage.enums.OrderMessageStatusEnum;
import com.ghf.exchange.otc.ordermessage.repository.OrderMessageRepository;
import com.ghf.exchange.otc.ordermessage.service.OrderMessageService;
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
public class OrderMessageServiceImpl extends BaseServiceImpl<OrderMessage, Long> implements OrderMessageService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private OrderMessageService appealService;

    @Lazy
    @Resource
    private OrderService orderService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderMessageServiceImpl(OrderMessageRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "OrderMessage", key = "'pageOrderMessage:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.status) ", condition = "        #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessage(PageOrderMessageReqDTO pageOrderMessageReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageOrderMessageReqDTO.getOrderCode())) {
            predicate.and(QOrderMessage.orderMessage.orderCode.eq(pageOrderMessageReqDTO.getOrderCode()));
        }

        if (pageOrderMessageReqDTO.getStatus() == OrderMessageStatusEnum.UN_READ.getCode() || pageOrderMessageReqDTO.getStatus() == OrderMessageStatusEnum.READ.getCode()) {
            predicate.and(QOrderMessage.orderMessage.status.eq(pageOrderMessageReqDTO.getStatus()));
        }

        PageRespDTO<OrderMessageRespDTO> pageResult = appealService.page(predicate, pageOrderMessageReqDTO, OrderMessageRespDTO.class);

        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "OrderMessage", key = "'getOrderMessageByOrderMessageCode:' +':'+#p0.orderMessageCode")
    @Override
    @SneakyThrows
    public Result<OrderMessageRespDTO> getOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {

        String orderMessageCode = getOrderMessageByOrderMessageCodeReqDTO.getOrderMessageCode();
        Predicate predicate = QOrderMessage.orderMessage.orderMessageCode.eq(orderMessageCode);
        OrderMessage orderMessage = appealService.get(predicate);

        //返回
        OrderMessageRespDTO orderMessageRespDTO = AutoMapUtils.map(orderMessage, OrderMessageRespDTO.class);

        return new Result<>(orderMessageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {

        String orderMessageCode = getOrderMessageByOrderMessageCodeReqDTO.getOrderMessageCode();
        Predicate predicate = QOrderMessage.orderMessage.orderMessageCode.eq(orderMessageCode);
        boolean b = appealService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "OrderMessage", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrderMessage(AddOrderMessageReqDTO addOrderMessageReqDTO) {
        OrderMessage orderMessage = AutoMapUtils.map(addOrderMessageReqDTO, OrderMessage.class);

        //初始化id
        orderMessage.setId(IdUtil.generateLongId());
        //判断消息编号
        if (!ObjectUtils.isEmpty(orderMessage.getOrderMessageCode())) {
            //判断唯一性
            String orderMessageCode = addOrderMessageReqDTO.getOrderMessageCode();
            GetOrderMessageByOrderMessageCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderMessageByOrderMessageCodeReqDTO();
            getAppealByAppealCodeReqDTO.setOrderMessageCode(orderMessageCode);
            boolean b = appealService.existsOrderMessageByOrderMessageCode(getAppealByAppealCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_MESSAGE_EXISTS);
            }
            orderMessage.setOrderMessageCode(addOrderMessageReqDTO.getOrderMessageCode());
        } else {
            //自动生成消息编号
            orderMessage.setOrderMessageCode(orderMessage.getId() + "");
        }

        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderMessageReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (currentLoginUser.getUsername().equals(orderRespDTO.getOrderCustomerUsername())) {
            //订单顾客,有权限消息
            flag = true;
            orderMessage.setOrderMessageReceiverUsername(orderRespDTO.getAdvertiseBusinessUsername());
        } else if (currentLoginUser.getUsername().equals(orderRespDTO.getAdvertiseBusinessUsername())) {
            //广告商家,有权限消息
            flag = true;
            orderMessage.setOrderMessageReceiverUsername(orderRespDTO.getOrderCustomerUsername());
        }
        if (!flag) {
            //无权限取消订单，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        orderMessage.setOrderCode(orderRespDTO.getOrderCode());

        orderMessage.setOrderMessageContent(addOrderMessageReqDTO.getOrderMessageContent());

        orderMessage.setCreateTime(new Date());
        orderMessage.setStatus(OrderMessageStatusEnum.UN_READ.getCode());

        //持久化到数据库
        appealService.add(orderMessage);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "OrderMessage", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> readOrderMessage(ReadOrderMessageReqDTO readOrderMessageReqDTO) {

        String orderMessageCode = readOrderMessageReqDTO.getOrderMessageCode();
        GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO = new GetOrderMessageByOrderMessageCodeReqDTO();
        getOrderMessageByOrderMessageCodeReqDTO.setOrderMessageCode(orderMessageCode);
        OrderMessageRespDTO orderMessageRespDTO = appealService.getOrderMessageByOrderMessageCode(getOrderMessageByOrderMessageCodeReqDTO).getData();
        OrderMessage afterOrderMessage = AutoMapUtils.map(orderMessageRespDTO, OrderMessage.class);

        //未读状态的消息才允许被审核
        if (afterOrderMessage.getStatus() != OrderMessageStatusEnum.UN_READ.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_MESSAGE_STATUS_IS_READ);
        }

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (username.equals(orderMessageRespDTO.getOrderMessageReceiverUsername())) {

            //管理员角色的,才有权限审核
            flag = true;
        }
        if (!flag) {
            //无权限取消订单，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrderMessage.setReadTime(new Date());
        afterOrderMessage.setStatus(OrderMessageStatusEnum.READ.getCode());

        //持久化到数据库
        appealService.update(afterOrderMessage);

        return new Result<>(ResultCodeEnum.OK);
    }

}