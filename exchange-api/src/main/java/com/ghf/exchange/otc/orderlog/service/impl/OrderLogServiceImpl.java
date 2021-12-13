package com.ghf.exchange.otc.orderlog.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.ClientRespDTO;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorication.user.util.IpUtil;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.orderlog.dto.AddOrderLogForClientReqDTO;
import com.ghf.exchange.otc.orderlog.dto.GetOrderLogByOrderLogCodeReqDTO;
import com.ghf.exchange.otc.orderlog.dto.OrderLogRespDTO;
import com.ghf.exchange.otc.orderlog.dto.PageOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.entity.OrderLog;
import com.ghf.exchange.otc.orderlog.entity.QOrderLog;
import com.ghf.exchange.otc.orderlog.event.AddOrderLogForClientEvent;
import com.ghf.exchange.otc.orderlog.repository.OrderLogRepository;
import com.ghf.exchange.otc.orderlog.service.OrderLogService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderLogServiceImpl extends BaseServiceImpl<OrderLog, Long> implements OrderLogService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private OrderLogService orderLogService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderLogServiceImpl(OrderLogRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderLogRespDTO>> pageOrderLog(PageOrderLogReqDTO pageOrderLogReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageOrderLogReqDTO.getOrderCode())) {
            predicate.and(QOrderLog.orderLog.orderCode.contains(pageOrderLogReqDTO.getOrderCode()));
        }
        if (!ObjectUtils.isEmpty(pageOrderLogReqDTO.getOrderLogCode())) {
            predicate.and(QOrderLog.orderLog.orderLogCode.contains(pageOrderLogReqDTO.getOrderLogCode()));
        }

        PageRespDTO<OrderLogRespDTO> pageRespDTO = orderLogService.page(predicate, pageOrderLogReqDTO, OrderLogRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<OrderLogRespDTO> getOrderLogByOrderLogCode(GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO) {
        //TODO 权限判断
        String orderLogCode = getOrderLogByOrderLogCodeReqDTO.getOrderLogCode();
        Predicate predicate = QOrderLog.orderLog.orderLogCode.eq(orderLogCode);
        OrderLog orderLog = orderLogService.get(predicate);
        //返回
        OrderLogRespDTO orderLogRespDTO = ModelMapperUtil.map(orderLog, OrderLogRespDTO.class);
        return new Result<>(orderLogRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderLogByOrderLogCode(GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO) {
        //TODO 权限判断
        String orderLogCode = getOrderLogByOrderLogCodeReqDTO.getOrderLogCode();
        Predicate predicate = QOrderLog.orderLog.orderLogCode.eq(orderLogCode);
        boolean b = orderLogService.exists(predicate);
        return new Result<>(b);
    }

    @Transactional
    @Override
    @SneakyThrows
    public Result<Void> addOrderLogForClient(AddOrderLogForClientReqDTO addOrderLogForClientReqDTO) {
        OrderLog orderLog = ModelMapperUtil.map(addOrderLogForClientReqDTO, OrderLog.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();

        //初始化id
        orderLog.setId(IdUtil.generateLongId());
        //判断广告编号
        if (!ObjectUtils.isEmpty(addOrderLogForClientReqDTO.getOrderLogCode())) {
            //判断唯一性
            String orderLogCode = addOrderLogForClientReqDTO.getOrderLogCode();
            GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO = new GetOrderLogByOrderLogCodeReqDTO();
            getOrderLogByOrderLogCodeReqDTO.setOrderLogCode(orderLogCode);
            boolean b = orderLogService.existsOrderLogByOrderLogCode(getOrderLogByOrderLogCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_LOG_EXISTS);
            }
            orderLog.setOrderLogCode(addOrderLogForClientReqDTO.getOrderLogCode());
        } else {
            //自动生成广告日志编号
            orderLog.setOrderLogCode(orderLog.getId() + "");
        }

        orderLog.setCreateTime(new Date());
        //持久化到数据库
        orderLogService.add(orderLog);

        //发送到消息队列
        AddOrderLogForClientEvent addOrderLogForClientEvent = new AddOrderLogForClientEvent();
        addOrderLogForClientEvent.setOrderLogCode(orderLog.getOrderLogCode());
        addOrderLogForClientEvent.setOrderCode(orderLog.getOrderCode());
        addOrderLogForClientEvent.setOrderLogType(orderLog.getOrderLogType());
        addOrderLogForClientEvent.setOrderLogClientId(orderLog.getOrderLogClientId());
        addOrderLogForClientEvent.setOrderLogUsername(orderLog.getOrderLogUsername());
        addOrderLogForClientEvent.setCreateTime(new Date());
        addOrderLogForClientEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(addOrderLogForClientEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

}