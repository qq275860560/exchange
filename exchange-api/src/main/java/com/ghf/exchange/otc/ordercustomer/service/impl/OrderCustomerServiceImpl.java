package com.ghf.exchange.otc.ordercustomer.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.legalcurrency.dto.GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO;
import com.ghf.exchange.otc.legalcurrency.dto.LegalCurrencyRespDTO;
import com.ghf.exchange.otc.legalcurrency.service.LegalCurrencyService;
import com.ghf.exchange.otc.order.enums.OrderBuySellTypeEnum;
import com.ghf.exchange.otc.ordercustomer.dto.*;
import com.ghf.exchange.otc.ordercustomer.entity.OrderCustomer;
import com.ghf.exchange.otc.ordercustomer.entity.QOrderCustomer;
import com.ghf.exchange.otc.ordercustomer.enums.OrderCustomerStatusEnum;
import com.ghf.exchange.otc.ordercustomer.repository.OrderCustomerRepository;
import com.ghf.exchange.otc.ordercustomer.service.OrderCustomerService;
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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderCustomerServiceImpl extends BaseServiceImpl<OrderCustomer, Long> implements OrderCustomerService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private OrderCustomerService orderCustomerService;
    @Lazy
    @Resource
    private LegalCurrencyService legalCurrencyService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderCustomerServiceImpl(OrderCustomerRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderCustomerRespDTO>> pageOrderCustomer(PageOrderCustomerReqDTO pageOrderCustomerReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageOrderCustomerReqDTO.getOrderCustomerCode())) {
            predicate.and(QOrderCustomer.orderCustomer.orderCustomerCode.eq(pageOrderCustomerReqDTO.getOrderCustomerCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderCustomerReqDTO.getUsername())) {
            predicate.and(QOrderCustomer.orderCustomer.username.eq(pageOrderCustomerReqDTO.getUsername()));
        }

        PageRespDTO<OrderCustomerRespDTO> pageRespDTO = orderCustomerService.page(predicate, pageOrderCustomerReqDTO, OrderCustomerRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderCustomerRespDTO>> pageOrderCustomerForAdmin(PageOrderCustomerForAdminReqDTO pageOrderCustomerForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageOrderCustomerForAdminReqDTO.getOrderCustomerCode())) {
            predicate.and(QOrderCustomer.orderCustomer.orderCustomerCode.eq(pageOrderCustomerForAdminReqDTO.getOrderCustomerCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderCustomerForAdminReqDTO.getUsername())) {
            predicate.and(QOrderCustomer.orderCustomer.username.eq(pageOrderCustomerForAdminReqDTO.getUsername()));
        }

        PageRespDTO<OrderCustomerRespDTO> pageRespDTO = orderCustomerService.page(predicate, pageOrderCustomerForAdminReqDTO, OrderCustomerRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomer(ListOrderCustomerReqDTO listOrderCustomerReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listOrderCustomerReqDTO.getOrderCustomerCode())) {
            predicate.and(QOrderCustomer.orderCustomer.orderCustomerCode.eq(listOrderCustomerReqDTO.getOrderCustomerCode()));
        }

        if (!ObjectUtils.isEmpty(listOrderCustomerReqDTO.getUsername())) {
            predicate.and(QOrderCustomer.orderCustomer.username.eq(listOrderCustomerReqDTO.getUsername()));
        }

        predicate.and(QOrderCustomer.orderCustomer.status.eq(OrderCustomerStatusEnum.ENABLE.getCode()));

        List<OrderCustomerRespDTO> list = orderCustomerService.list(predicate, OrderCustomerRespDTO.class);

        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomerForAdmin(ListOrderCustomerForAdminReqDTO listOrderCustomerForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(listOrderCustomerForAdminReqDTO.getOrderCustomerCode())) {
            predicate.and(QOrderCustomer.orderCustomer.orderCustomerCode.eq(listOrderCustomerForAdminReqDTO.getOrderCustomerCode()));
        }

        if (!ObjectUtils.isEmpty(listOrderCustomerForAdminReqDTO.getUsername())) {
            predicate.and(QOrderCustomer.orderCustomer.username.eq(listOrderCustomerForAdminReqDTO.getUsername()));
        }
        predicate.and(QOrderCustomer.orderCustomer.status.eq(OrderCustomerStatusEnum.ENABLE.getCode()));

        List<OrderCustomerRespDTO> list = orderCustomerService.list(predicate, OrderCustomerRespDTO.class);

        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomerForClient(ListOrderCustomerForClientReqDTO listOrderCustomerForClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listOrderCustomerForClientReqDTO.getOrderCustomerCode())) {
            predicate.and(QOrderCustomer.orderCustomer.orderCustomerCode.eq(listOrderCustomerForClientReqDTO.getOrderCustomerCode()));
        }

        if (!ObjectUtils.isEmpty(listOrderCustomerForClientReqDTO.getUsername())) {
            predicate.and(QOrderCustomer.orderCustomer.username.eq(listOrderCustomerForClientReqDTO.getUsername()));
        }
        predicate.and(QOrderCustomer.orderCustomer.status.eq(OrderCustomerStatusEnum.ENABLE.getCode()));

        List<OrderCustomerRespDTO> list = orderCustomerService.list(predicate, OrderCustomerRespDTO.class);

        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<OrderCustomerRespDTO> getOrderCustomerByOrderCustomerCode(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {

        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        Predicate predicate = QOrderCustomer.orderCustomer.orderCustomerCode.eq(orderCustomerCode);
        OrderCustomer orderCustomer = orderCustomerService.get(predicate);

        //返回
        OrderCustomerRespDTO orderCustomerRespDTO = ModelMapperUtil.map(orderCustomer, OrderCustomerRespDTO.class);

        return new Result<>(orderCustomerRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderCustomerByOrderCustomerCode(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {

        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        Predicate predicate = QOrderCustomer.orderCustomer.orderCustomerCode.eq(orderCustomerCode);
        boolean b = orderCustomerService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Void> addOrderCustomerForClient(AddOrderCustomerForClientReqDTO addOrderCustomerForAdminReqDTO) {
        OrderCustomer orderCustomer = new OrderCustomer();

        //初始化id
        orderCustomer.setId(IdUtil.generateLongId());
        //判断编号
        if (!ObjectUtils.isEmpty(addOrderCustomerForAdminReqDTO.getOrderCustomerCode())) {
            //判断唯一性
            String orderCustomerCode = addOrderCustomerForAdminReqDTO.getOrderCustomerCode();
            GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
            getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
            boolean b = orderCustomerService.existsOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_EXISTS);
            }
            orderCustomer.setOrderCustomerCode(addOrderCustomerForAdminReqDTO.getOrderCustomerCode());
        } else {
            //自动生成编号
            orderCustomer.setOrderCustomerCode(addOrderCustomerForAdminReqDTO.getUsername() + "");
        }

        if (ObjectUtils.isEmpty(addOrderCustomerForAdminReqDTO.getUsername())) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_USERNAME_EXISTS);
        }
        orderCustomer.setUsername(addOrderCustomerForAdminReqDTO.getUsername());

        orderCustomer.setPassword(addOrderCustomerForAdminReqDTO.getPassword());
        orderCustomer.setNickname(addOrderCustomerForAdminReqDTO.getNickname());
        orderCustomer.setRealname(addOrderCustomerForAdminReqDTO.getRealname());
        orderCustomer.setMobile(addOrderCustomerForAdminReqDTO.getMobile());
        orderCustomer.setEmail(addOrderCustomerForAdminReqDTO.getEmail());

        //设置国家信息和法币信息
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(addOrderCustomerForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        orderCustomer.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        orderCustomer.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        orderCustomer.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        orderCustomer.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        orderCustomer.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        orderCustomer.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());

        //设置状态
        orderCustomer.setStatus(OrderCustomerStatusEnum.ENABLE.getCode());

        orderCustomer.setCreateTime(new Date());

        //持久化到数据库
        orderCustomerService.add(orderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerByOrderCustomerForAdminCode(UpdateOrderCustomerByOrderCustomerCodeForAdminReqDTO updateOrderCustomerByOrderCustomerCodeForAdminReqDTO) {
        String orderCustomerCode = updateOrderCustomerByOrderCustomerCodeForAdminReqDTO.getOrderCustomerCode();
        //加载
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }
        //已禁用状态无权限
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        if (ObjectUtils.isEmpty(orderCustomerRespDTO.getUsername())) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_USERNAME_EXISTS);
        }
        afterOrderCustomer.setUsername(orderCustomerRespDTO.getUsername());

        afterOrderCustomer.setPassword(orderCustomerRespDTO.getPassword());
        afterOrderCustomer.setNickname(orderCustomerRespDTO.getNickname());
        afterOrderCustomer.setRealname(orderCustomerRespDTO.getRealname());
        afterOrderCustomer.setMobile(orderCustomerRespDTO.getMobile());
        afterOrderCustomer.setEmail(orderCustomerRespDTO.getEmail());

        //设置国家信息和法币信息
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(updateOrderCustomerByOrderCustomerCodeForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        afterOrderCustomer.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterOrderCustomer.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        afterOrderCustomer.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterOrderCustomer.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        afterOrderCustomer.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        afterOrderCustomer.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());

        //设置状态
        afterOrderCustomer.setStatus(OrderCustomerStatusEnum.ENABLE.getCode());
        //更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> enableOrderCustomerForAdmin(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        //加载
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //已启用状态无法启用
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_ENABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //设置状态
        afterOrderCustomer.setStatus(OrderCustomerStatusEnum.ENABLE.getCode());

        //更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> disableOrderCustomerForAdmin(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        //加载
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //已禁用状态无法禁用
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //设置状态
        afterOrderCustomer.setStatus(OrderCustomerStatusEnum.DISABLE.getCode());

        //更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnAddOrderEventForClient(UpdateOrderCustomerOnAddOrderEventForClientReqDTO updateOrderCustomerOnAddOrderEventForClientReqDTO) {
        String orderCustomerCode = updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderCustomerCode();
        //加载
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //已禁用
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //设置订单顾客下单次数
        afterOrderCustomer.setOrderBuySellCount(orderCustomerRespDTO.getOrderBuySellCount() + 1);
        if (updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单次数
            afterOrderCustomer.setOrderBuyCount(orderCustomerRespDTO.getOrderBuyCount() + 1);
        } else if (updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单次数
            afterOrderCustomer.setOrderSellCount(orderCustomerRespDTO.getOrderSellCount() + 1);
        }
        //设置买总完成率=买币订单放行次数/订单顾客买币订单下单次数
        if (updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterOrderCustomer.setOrderBuyReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount()).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyCount() + 1), 8, BigDecimal.ROUND_UP));
        }
        //设置总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数
        afterOrderCustomer.setOrderBuySellReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount() + orderCustomerRespDTO.getOrderSellReleaseCount()).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuySellCount() + 1), 8, BigDecimal.ROUND_UP));

//更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnReleaseOrderEventForClient(UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO updateOrderCustomerOnReleaseOrderEventForClientReqDTO) {
        String orderCustomerCode = updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderCustomerCode();
        //加载
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //已禁用
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单放行次数
            afterOrderCustomer.setOrderBuyReleaseCount(orderCustomerRespDTO.getOrderBuyReleaseCount() + 1);
        } else if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单放行次数
            afterOrderCustomer.setOrderSellReleaseCount(orderCustomerRespDTO.getOrderSellReleaseCount() + 1);
        }

        //设置买总完成率=买币订单放行次数/订单顾客买币订单下单次数
        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterOrderCustomer.setOrderBuyReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount() + 1).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyCount()), 8, BigDecimal.ROUND_UP));
        }
        //设置总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数
        afterOrderCustomer.setOrderBuySellReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount() + orderCustomerRespDTO.getOrderSellReleaseCount() + 1).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuySellCount()), 8, BigDecimal.ROUND_UP));

        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单累计放行时间
            afterOrderCustomer.setOrderBuyTotalReleaseTime(orderCustomerRespDTO.getOrderBuyTotalReleaseTime() + updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        } else if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单累计放行时间
            afterOrderCustomer.setOrderSellTotalReleaseTime(orderCustomerRespDTO.getOrderSellTotalReleaseTime() + updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        }

        //设置订单平均放行时间
        afterOrderCustomer.setOrderBuySellAvgReleaseTime((orderCustomerRespDTO.getOrderBuyTotalReleaseTime() + orderCustomerRespDTO.getOrderSellTotalReleaseTime() + updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getPayTime().getTime()) / orderCustomerRespDTO.getOrderBuySellCount());

//更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnAppealOrderEventForClient(UpdateOrderCustomerOnAppealOrderEventForClientReqDTO updateOrderCustomerOnAppealOrderEventForClientReqDTO) {

        String orderCustomerCode = updateOrderCustomerOnAppealOrderEventForClientReqDTO.getOrderCustomerCode();
        //加载
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //已禁用
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //设置订单当日申诉次数
        if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(updateOrderCustomerOnAppealOrderEventForClientReqDTO.getAppealTime()))) {
            afterOrderCustomer.setOrderTodayAppealCount(1);
        } else {
            afterOrderCustomer.setOrderTodayAppealCount(orderCustomerRespDTO.getOrderTodayAppealCount() + 1);
        }
        //设置订单最后申诉时间
        afterOrderCustomer.setOrderLastAppealTime(updateOrderCustomerOnAppealOrderEventForClientReqDTO.getAppealTime());
        //更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnCancelOrderEventForClient(UpdateOrderCustomerOnCancelOrderEventForClientReqDTO updateOrderCustomerOnCancelOrderEventForClientReqDTO) {

        String orderCustomerCode = updateOrderCustomerOnCancelOrderEventForClientReqDTO.getOrderCustomerCode();
        //加载
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //已禁用
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //设置订单当日取消次数
        if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(updateOrderCustomerOnCancelOrderEventForClientReqDTO.getCancelTime()))) {
            afterOrderCustomer.setOrderTodayCancelCount(1);
        } else {
            afterOrderCustomer.setOrderTodayCancelCount(orderCustomerRespDTO.getOrderTodayCancelCount() + 1);
        }
        //设置订单最后取消时间
        afterOrderCustomer.setOrderLastCancelTime(updateOrderCustomerOnCancelOrderEventForClientReqDTO.getCancelTime());
        //更新到数据库
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

}