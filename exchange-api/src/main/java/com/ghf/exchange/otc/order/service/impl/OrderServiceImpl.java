package com.ghf.exchange.otc.order.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.ClientRespDTO;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorication.user.util.IpUtil;
import com.ghf.exchange.boss.common.task.enums.TaskInvokeTypeEnum;
import com.ghf.exchange.boss.common.task.enums.TaskTypeEnum;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.exception.ResultCodeException;
import com.ghf.exchange.otc.account.dto.DecFrozenBalanceForClientReqDTO;
import com.ghf.exchange.otc.account.dto.FreezeBalanceForClientReqDTO;
import com.ghf.exchange.otc.account.dto.IncBalanceForClientReqDTO;
import com.ghf.exchange.otc.account.dto.UnFreezeBalanceForClientReqDTO;
import com.ghf.exchange.otc.account.enums.AccountUsernameEnum;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.order.dto.*;
import com.ghf.exchange.otc.order.entity.Order;
import com.ghf.exchange.otc.order.entity.QOrder;
import com.ghf.exchange.otc.order.enums.OrderBuySellTypeEnum;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.event.*;
import com.ghf.exchange.otc.order.repository.OrderRepository;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.ordercustomer.dto.GetOrderCustomerByOrderCustomerCodeReqDTO;
import com.ghf.exchange.otc.ordercustomer.dto.OrderCustomerRespDTO;
import com.ghf.exchange.otc.ordercustomer.service.OrderCustomerService;
import com.ghf.exchange.otc.orderlog.enums.OrderLogTypeEnum;
import com.ghf.exchange.otc.payment.dto.GetPaymentByPaymentTypeForClientReqDTO;
import com.ghf.exchange.otc.payment.dto.PaymentRespDTO;
import com.ghf.exchange.otc.payment.enums.PaymentTypeEnum;
import com.ghf.exchange.otc.payment.service.PaymentService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.JsonUtil;
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
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderServiceImpl extends BaseServiceImpl<Order, Long> implements OrderService {

    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private OrderService orderService;

    @Lazy
    @Resource
    private AdvertiseService advertiseService;

    @Lazy
    @Resource
    private AccountService accountService;

    @Lazy
    @Resource
    private CoinService coinService;

    @Lazy
    @Resource
    private PaymentService paymentService;

    @Lazy
    @Resource
    private OrderCustomerService orderCustomerService;

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
    @Value("${maxCancelTimes}")
    private long maxCancelTimes;

    public OrderServiceImpl(OrderRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Order", key = "'pageOrder:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.orderCustomerPaymentType).concat(':').concat(#p0.orderSource).concat(':').concat(#p0.orderBuySellType).concat(':').concat(#p0.advertiseCoinCode).concat(':').concat(#p0.status) ", condition = "        #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderRespDTO>> pageOrder(PageOrderReqDTO pageOrderReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrder.order.orderCustomerUsername.contains(currentLoginUser.getUsername()).or(QOrder.order.advertiseBusinessUsername.contains(currentLoginUser.getUsername())));

        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getOrderCode())) {
            predicate.and(QOrder.order.orderCode.contains(pageOrderReqDTO.getOrderCode()));
        }

        if (pageOrderReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || pageOrderReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || pageOrderReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.BANK.getCode()
        ) {
            predicate.and(QOrder.order.orderCustomerPaymentType.eq(pageOrderReqDTO.getOrderCustomerPaymentType()));
        }

        if (pageOrderReqDTO.getOrderSource() == OrderSourceEnum.ADVERTISE_SELECT.getCode()
                || pageOrderReqDTO.getOrderSource() == OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode()

        ) {
            predicate.and(QOrder.order.orderSource.eq(pageOrderReqDTO.getOrderSource()));
        }

        if (pageOrderReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()
                || pageOrderReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()

        ) {
            predicate.and(QOrder.order.orderBuySellType.eq(pageOrderReqDTO.getOrderBuySellType()));
        }

        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getAdvertiseCode())) {
            predicate.and(QOrder.order.advertiseCode.contains(pageOrderReqDTO.getAdvertiseCode()));
        }
        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getAdvertiseCoinCode())) {
            predicate.and(QOrder.order.advertiseCoinCode.contains(pageOrderReqDTO.getAdvertiseCoinCode()));
        }

        if (pageOrderReqDTO.getStatus() == OrderStatusEnum.ADD.getCode()
                || pageOrderReqDTO.getStatus() == OrderStatusEnum.PAY.getCode()
                || pageOrderReqDTO.getStatus() == OrderStatusEnum.CANCEL.getCode()
                || pageOrderReqDTO.getStatus() == OrderStatusEnum.RELEASE.getCode()

        ) {
            predicate.and(QOrder.order.status.eq(pageOrderReqDTO.getStatus()));
        }

        PageRespDTO<OrderRespDTO> pageRespDTO = orderService.page(predicate, pageOrderReqDTO, OrderRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Order", key = "'pageOrderForAdmin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.orderCustomerPaymentType).concat(':').concat(#p0.orderSource).concat(':').concat(#p0.orderBuySellType).concat(':').concat(#p0.advertiseCoinCode).concat(':').concat(#p0.status) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.username)       && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderRespDTO>> pageOrderForAdmin(PageOrderForAdminReqDTO pageOrderForAdminReqDTO) {

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageOrderForAdminReqDTO.getUsername())) {
            predicate.and(QOrder.order.orderCustomerUsername.contains(pageOrderForAdminReqDTO.getUsername()).or(QOrder.order.advertiseBusinessUsername.contains(pageOrderForAdminReqDTO.getUsername())));
        }

        if (!ObjectUtils.isEmpty(pageOrderForAdminReqDTO.getOrderCode())) {
            predicate.and(QOrder.order.orderCode.contains(pageOrderForAdminReqDTO.getOrderCode()));
        }

        if (pageOrderForAdminReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || pageOrderForAdminReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || pageOrderForAdminReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.BANK.getCode()
        ) {
            predicate.and(QOrder.order.orderCustomerPaymentType.eq(pageOrderForAdminReqDTO.getOrderCustomerPaymentType()));
        }

        if (pageOrderForAdminReqDTO.getOrderSource() == OrderSourceEnum.ADVERTISE_SELECT.getCode()
                || pageOrderForAdminReqDTO.getOrderSource() == OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode()

        ) {
            predicate.and(QOrder.order.orderSource.eq(pageOrderForAdminReqDTO.getOrderSource()));
        }

        if (pageOrderForAdminReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()
                || pageOrderForAdminReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()

        ) {
            predicate.and(QOrder.order.orderBuySellType.eq(pageOrderForAdminReqDTO.getOrderBuySellType()));
        }

        if (!ObjectUtils.isEmpty(pageOrderForAdminReqDTO.getAdvertiseCode())) {
            predicate.and(QOrder.order.advertiseCode.contains(pageOrderForAdminReqDTO.getAdvertiseCode()));
        }
        if (!ObjectUtils.isEmpty(pageOrderForAdminReqDTO.getAdvertiseCoinCode())) {
            predicate.and(QOrder.order.advertiseCoinCode.contains(pageOrderForAdminReqDTO.getAdvertiseCoinCode()));
        }

        if (pageOrderForAdminReqDTO.getStatus() == OrderStatusEnum.ADD.getCode()
                || pageOrderForAdminReqDTO.getStatus() == OrderStatusEnum.PAY.getCode()
                || pageOrderForAdminReqDTO.getStatus() == OrderStatusEnum.CANCEL.getCode()
                || pageOrderForAdminReqDTO.getStatus() == OrderStatusEnum.RELEASE.getCode()

        ) {
            predicate.and(QOrder.order.status.eq(pageOrderForAdminReqDTO.getStatus()));
        }

        PageRespDTO<OrderRespDTO> pageRespDTO = orderService.page(predicate, pageOrderForAdminReqDTO, OrderRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Order", key = "'getOrderByOrderCode:'+#p0.orderCode")
    @Override
    @SneakyThrows
    public Result<OrderRespDTO> getOrderByOrderCode(GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO) {

        String orderCode = getOrderByOrderCodeReqDTO.getOrderCode();
        Predicate predicate = QOrder.order.orderCode.eq(orderCode);
        Order order = orderService.get(predicate);

        //返回
        OrderRespDTO advertisRespDTO = ModelMapperUtil.map(order, OrderRespDTO.class);

        return new Result<>(advertisRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderByOrderCode(GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO) {

        String orderCode = getOrderByOrderCodeReqDTO.getOrderCode();
        Predicate predicate = QOrder.order.orderCode.eq(orderCode);
        boolean b = orderService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsUnPayOrderForClient(ExistsAddStatusOrderForClientReqDTO existsUnPayOrderForClientReqDTO) {

        String username = existsUnPayOrderForClientReqDTO.getUsername();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QOrder.order.orderBuySellType.eq(OrderBuySellTypeEnum.BUY.getCode()));
        booleanBuilder.and(QOrder.order.orderCustomerUsername.eq(username));
        booleanBuilder.and(QOrder.order.status.eq(OrderStatusEnum.ADD.getCode()));

        boolean b = orderService.exists(booleanBuilder);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrder(AddOrderReqDTO addOrderReqDTO) {
        //TODO 分布式锁，分布式事务
        Order order = ModelMapperUtil.map(addOrderReqDTO, Order.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //一个用户同一时刻只允许一个未完成订单
        ExistsAddStatusOrderForClientReqDTO existsAddStatusOrderForClientReqDTO = new ExistsAddStatusOrderForClientReqDTO();
        existsAddStatusOrderForClientReqDTO.setUsername(username);
        boolean b = orderService.existsUnPayOrderForClient(existsAddStatusOrderForClientReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ORDER_ADD_STATUS_EXISTS);
        }
        //判断订单顾客每日订单取消次数是否超过系统规定
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(username);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO.getOrderTodayCancelCount() >= maxCancelTimes) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_ORDER_TODAY_CANCEL_COUNT_OVERLOAD);
        }

        //初始化id
        order.setId(IdUtil.generateLongId());
        //判断订单编号
        if (!ObjectUtils.isEmpty(order.getOrderCode())) {
            //判断唯一性
            String orderCode = addOrderReqDTO.getOrderCode();
            GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
            getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
            b = orderService.existsOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_EXISTS);
            }
            order.setOrderCode(addOrderReqDTO.getOrderCode());
        } else {
            //自动生成广告编号
            order.setOrderCode(order.getId() + "");
        }

        //订单来源
        int orderSource = addOrderReqDTO.getOrderSource();
        if (orderSource != OrderSourceEnum.ADVERTISE_SELECT.getCode() && orderSource != OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_SOURCE_NOT_EXISTS);
        }

        //确定币种编码
        String orderCoinCode = null;
        //广告区选中下单,币种编码来自广告
        if (orderSource == OrderSourceEnum.ADVERTISE_SELECT.getCode()) {
            //获取广告
            String advertiseCode = addOrderReqDTO.getAdvertiseCode();
            GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getAdvertiseByCodeReqDTO.setAdvertiseCode(advertiseCode);
            AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
            orderCoinCode = advertiseRespDTO.getAdvertiseCoinCode();
        }
        //快捷区一键匹配下单，币种编码来自输入
        else {

            orderCoinCode = addOrderReqDTO.getOrderCoinCode();
            if (ObjectUtils.isEmpty(orderCoinCode)) {
                return new Result<>(ResultCodeEnum.ORDER_COIN_CODE_CAN_NOT_EMPTY);
            }
        }

        //获取币种信息
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode(orderCoinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //获取订单交易量
        BigDecimal orderAmount = addOrderReqDTO.getOrderAmount();
        //校验订单交易量是否满足单笔交易量限制
        if (orderAmount.compareTo(coinRespDTO.getPerOrderMinAmount()) < 0 || orderAmount.compareTo(coinRespDTO.getPerOrderMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ORDER_AMOUNT_ERROR);
        }

        //校验订单顾客选择的收付款类型
        if (addOrderReqDTO.getOrderCustomerPaymentType() != PaymentTypeEnum.ALIPAY.getCode() && addOrderReqDTO.getOrderCustomerPaymentType() != PaymentTypeEnum.WECHAT.getCode() && addOrderReqDTO.getOrderCustomerPaymentType() != PaymentTypeEnum.BANK.getCode()) {
            return new Result<>(ResultCodeEnum.PAYMENT_TYPE_NOT_EXISTS);
        }
        //设置订单顾客选择的收付款类型
        order.setOrderCustomerPaymentType(addOrderReqDTO.getOrderCustomerPaymentType());

        //确定广告
        AdvertiseRespDTO advertiseRespDTO = null;
        //订单买卖类型
        int orderBuySellType = 0;

        //广告买卖类型
        int advertiseBuySellType = 0;

        BigDecimal orderPrice = null;
        //广告区选中下单
        if (orderSource == OrderSourceEnum.ADVERTISE_SELECT.getCode()) {
            //确定广告，直接根据广告编码获取
            String advertiseCode = addOrderReqDTO.getAdvertiseCode();
            GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
            advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();

            //确定广告买卖类型
            advertiseBuySellType = advertiseRespDTO.getAdvertiseBuySellType();
            //确定订单买卖类型
            orderBuySellType = advertiseBuySellType == AdvertiseBuySellTypeEnum.BUY.getCode() ? OrderBuySellTypeEnum.SELL.getCode() : OrderBuySellTypeEnum.BUY.getCode();

            //确定单价
            if (advertiseRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
                //广告价格为固定时
                orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
            } else {
                orderPrice = coinRespDTO.getMarketPrice();
                //广告价格为浮动时
                if (advertiseRespDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
                    //此时订单顾客作为买币方，希望尽可能低价格成交
                    orderPrice = orderPrice.multiply(BigDecimal.ONE.subtract(advertiseRespDTO.getAdvertisePremiumRate()));
                } else {
                    //此时订单顾客作为卖币方，希望尽可能高价格成交
                    orderPrice = orderPrice.multiply(BigDecimal.ONE.add(advertiseRespDTO.getAdvertisePremiumRate()));
                }

            }

            //校验订单交易量是否满足广告库存数量
            if (orderAmount.compareTo(advertiseRespDTO.getAdvertiseAvailableAmount()) > 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
            }
        }
        //快捷区一键匹配下单
        else {

            //校验订单买卖类型
            orderBuySellType = addOrderReqDTO.getOrderBuySellType();
            if (orderBuySellType != OrderBuySellTypeEnum.BUY.getCode() && orderBuySellType != OrderBuySellTypeEnum.SELL.getCode()) {
                return new Result<>(ResultCodeEnum.ORDER_BUY_SELL_TYPE_NOT_EXISTS);
            }
            //确定广告买卖类型
            advertiseBuySellType = orderBuySellType == OrderBuySellTypeEnum.BUY.getCode() ? AdvertiseBuySellTypeEnum.SELL.getCode() : AdvertiseBuySellTypeEnum.BUY.getCode();

            //确定可能的单价
            orderPrice = coinRespDTO.getMarketPrice();

            //获取最匹配广告
            GetMatchAdvertiseReqDTO getMatchAdvertiseReqDTO = new GetMatchAdvertiseReqDTO();
            getMatchAdvertiseReqDTO.setAdvertiseCoinCode(orderCoinCode);
            getMatchAdvertiseReqDTO.setAdvertiseBuySellType(advertiseBuySellType);
            getMatchAdvertiseReqDTO.setAdvertiseAmount(orderAmount);
            getMatchAdvertiseReqDTO.setAdvertiseFixedPrice(orderPrice);
            getMatchAdvertiseReqDTO.setAdvertiseBusinessPaymentType(addOrderReqDTO.getOrderCustomerPaymentType());

            advertiseRespDTO = advertiseService.getMatchAdvertise(getMatchAdvertiseReqDTO).getData();

            if (advertiseRespDTO == null) {
                return new Result<>(ResultCodeEnum.ORDER_ONE_KEY_MATCH_ERROR);
            }

            //确定单价
            if (advertiseRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
                //广告价格为固定时
                orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
            } else {
                //广告价格为浮动时
                if (getMatchAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
                    //此时订单顾客作为买币方，希望尽可能低价格成交
                    orderPrice = orderPrice.multiply(BigDecimal.valueOf(1).subtract(advertiseRespDTO.getAdvertisePremiumRate()));
                } else {
                    //此时订单顾客作为卖币方，希望尽可能高价格成交
                    orderPrice = orderPrice.multiply(BigDecimal.valueOf(1).add(advertiseRespDTO.getAdvertisePremiumRate()));
                }
            }
            //校验订单交易量是否满足广告库存数量
            if (orderAmount.compareTo(advertiseRespDTO.getAdvertiseAvailableAmount()) > 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
            }
        }
        //设置订单来源
        order.setOrderSource(orderSource);
        //设置订单买卖类型
        order.setOrderBuySellType(orderBuySellType);
        //设置币种编码
        order.setOrderCoinCode(orderCoinCode);
        order.setOrderCoinName(coinRespDTO.getCoinName());
        order.setOrderCoinUnit(coinRespDTO.getCoinUnit());
        order.setOrderCoinRate(coinRespDTO.getCoinRate());
        //设置订单交易量
        order.setOrderAmount(orderAmount);
        //设置订单成交单价
        order.setOrderPrice(orderPrice);
        //设置订单成交总价
        order.setOrderTotalPrice(order.getOrderPrice().multiply(order.getOrderAmount()));
        //订单类型为卖出币种时，要判断订单顾客的余额是否充足,如果充足则需要冻结订单顾客余额

        //TODO 当前默认为美元
        //TODO 用户所在国家支持的法币列表和广告支持的法币做交集，存在则认为有权限交易

        GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO = new GetPaymentByPaymentTypeForClientReqDTO();
        getPaymentByPaymentTypeForClientReqDTO.setUsername(currentLoginUser.getUsername());
        getPaymentByPaymentTypeForClientReqDTO.setPaymentType(addOrderReqDTO.getOrderCustomerPaymentType());
        PaymentRespDTO paymentRespDTO = paymentService.getPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO).getData();

        GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO1 = new GetPaymentByPaymentTypeForClientReqDTO();
        getPaymentByPaymentTypeForClientReqDTO1.setUsername(advertiseRespDTO.getAdvertiseBusinessUsername());
        getPaymentByPaymentTypeForClientReqDTO1.setPaymentType(addOrderReqDTO.getOrderCustomerPaymentType());
        PaymentRespDTO paymentRespDTO2 = paymentService.getPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO1).getData();

        if (addOrderReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.ALIPAY.getCode()) {
            order.setOrderCustomerPaymentCode(paymentRespDTO.getPaymentCode());
            order.setOrderCustomerPaymentTypeAlipayAccount(paymentRespDTO.getPaymentTypeAlipayAccount());
            order.setOrderCustomerPaymentTypeAlipayQrcode(paymentRespDTO.getPaymentTypeAlipayQrcode());
            order.setAdvertiseBusinessPaymentCode(paymentRespDTO2.getPaymentCode());
            order.setAdvertiseBusinessPaymentTypeAlipayAccount(paymentRespDTO2.getPaymentTypeAlipayAccount());
            order.setAdvertiseBusinessPaymentTypeAlipayQrcode(paymentRespDTO2.getPaymentTypeAlipayQrcode());

        } else if (addOrderReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.WECHAT.getCode()) {
            order.setOrderCustomerPaymentCode(paymentRespDTO.getPaymentCode());
            order.setOrderCustomerPaymentTypeWechatAccount(paymentRespDTO.getPaymentTypeWechatAccount());
            order.setOrderCustomerPaymentTypeWechatQrcode(paymentRespDTO.getPaymentTypeWechatQrcode());
            order.setAdvertiseBusinessPaymentCode(paymentRespDTO2.getPaymentCode());
            order.setAdvertiseBusinessPaymentTypeWechatAccount(paymentRespDTO2.getPaymentTypeWechatAccount());
            order.setAdvertiseBusinessPaymentTypeWechatQrcode(paymentRespDTO2.getPaymentTypeWechatQrcode());

        } else if (addOrderReqDTO.getOrderCustomerPaymentType() == PaymentTypeEnum.BANK.getCode()) {
            order.setOrderCustomerPaymentCode(paymentRespDTO.getPaymentCode());
            order.setOrderCustomerPaymentTypeBankName(paymentRespDTO.getPaymentTypeBankName());
            order.setOrderCustomerPaymentTypeBankBranchName(paymentRespDTO.getPaymentTypeBankBranchName());
            order.setOrderCustomerPaymentTypeBankAccount(paymentRespDTO.getPaymentTypeBankAccount());
            order.setOrderCustomerPaymentTypeBankRealname(paymentRespDTO.getPaymentTypeBankRealname());

            order.setAdvertiseBusinessPaymentCode(paymentRespDTO2.getPaymentCode());
            order.setAdvertiseBusinessPaymentTypeBankName(paymentRespDTO2.getPaymentTypeBankName());
            order.setAdvertiseBusinessPaymentTypeBankBranchName(paymentRespDTO2.getPaymentTypeBankBranchName());
            order.setAdvertiseBusinessPaymentTypeBankAccount(paymentRespDTO2.getPaymentTypeBankAccount());
            order.setAdvertiseBusinessPaymentTypeBankRealname(paymentRespDTO2.getPaymentTypeBankRealname());

        } else {
            return new Result<>(ResultCodeEnum.PAYMENT_NOT_EXISTS);
        }

        //设置订单顾客
        order.setOrderCustomerUsername(currentLoginUser.getUsername());
        order.setOrderCustomerNickname(currentLoginUser.getNickname());
        order.setOrderCustomerRealname(currentLoginUser.getRealname());

        //设置广告快照
        order.setAdvertiseCode(advertiseRespDTO.getAdvertiseCode());
        order.setAdvertiseBuySellType(advertiseBuySellType);
        order.setAdvertiseCoinCode(advertiseRespDTO.getAdvertiseCoinCode());
        order.setAdvertiseCoinName(advertiseRespDTO.getAdvertiseCoinName());
        order.setAdvertiseCoinUnit(advertiseRespDTO.getAdvertiseCoinUnit());
        order.setAdvertiseCoinRate(advertiseRespDTO.getAdvertiseCoinRate());
        order.setAdvertiseAvailableAmount(advertiseRespDTO.getAdvertiseAvailableAmount());

        order.setAdvertiseLegalCurrencyCountryCode(advertiseRespDTO.getAdvertiseLegalCurrencyCountryCode());
        order.setAdvertiseLegalCurrencyCountryName(advertiseRespDTO.getAdvertiseLegalCurrencyCountryName());
        order.setAdvertiseLegalCurrencyCode(advertiseRespDTO.getAdvertiseLegalCurrencyCode());
        order.setAdvertiseLegalCurrencyName(advertiseRespDTO.getAdvertiseLegalCurrencyName());
        order.setAdvertiseLegalCurrencySymbol(advertiseRespDTO.getAdvertiseLegalCurrencyCountryCode());
        order.setAdvertiseLegalCurrencyUnit(advertiseRespDTO.getAdvertiseLegalCurrencyUnit());

        order.setAdvertisePriceType(advertiseRespDTO.getAdvertisePriceType());
        order.setAdvertiseFixedPrice(advertiseRespDTO.getAdvertiseFixedPrice());
        order.setAdvertisePremiumRate(advertiseRespDTO.getAdvertisePremiumRate());

        order.setAdvertiseBusinessUsername(advertiseRespDTO.getAdvertiseBusinessUsername());
        order.setAdvertiseBusinessNickname(advertiseRespDTO.getAdvertiseBusinessNickname());
        order.setAdvertiseBusinessRealname(advertiseRespDTO.getAdvertiseBusinessRealname());

        //广告状态设置已下单
        order.setStatus(OrderStatusEnum.ADD.getCode());
        //设置广告创建时间
        order.setAddTime(new Date());
        //设置备注
        order.setRemark(addOrderReqDTO.getRemark());
        //持久化到数据库
        orderService.add(order);

        if (orderBuySellType == OrderBuySellTypeEnum.SELL.getCode()) {
            //卖币订单需要冻结订单顾客的账户余额(广告商家无论买币还是卖币都需要扣手续费，订单顾客无论买币还是卖币都无需扣手续费)
            FreezeBalanceForClientReqDTO freezeBalanceReqDTO = new FreezeBalanceForClientReqDTO();
            freezeBalanceReqDTO.setUsername(order.getOrderCustomerUsername());
            freezeBalanceReqDTO.setCoinCode(order.getOrderCoinCode());
            freezeBalanceReqDTO.setBalance(order.getOrderAmount());
            freezeBalanceReqDTO.setRemark("卖币订单冻结订单顾客的账户余额(订单交易数量)");
            Result freezeBalanceResult = accountService.freezeBalanceForClient(freezeBalanceReqDTO);
            if (freezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(freezeBalanceResult.getCode(), freezeBalanceResult.getMsg());
            }
        }

        //冻结广告商家的广告库存数量
        FreezeAdvertiseAmountReqDTO freezeAmountReqDTO = new FreezeAdvertiseAmountReqDTO();
        freezeAmountReqDTO.setAdvertiseCode(advertiseRespDTO.getAdvertiseCode());
        freezeAmountReqDTO.setAdvertiseAmount(order.getOrderAmount());
        freezeAmountReqDTO.setRemark("下单时冻结广告库存数量(订单交易数量)");
        Result freezeAdvertiseAmountResult = advertiseService.freezeAdvertiseAmount(freezeAmountReqDTO);
        if (freezeAdvertiseAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(freezeAdvertiseAmountResult.getCode(), freezeAdvertiseAmountResult.getMsg());
        }

        //发送到消息队列
        AddOrderEvent addOrderEvent = ModelMapperUtil.map(order, AddOrderEvent.class);
        addOrderEvent.setOrderCode(order.getOrderCode());
        addOrderEvent.setOrderLogType(OrderLogTypeEnum.ADD_ORDER.getCode());
        addOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderEvent.setCreateTime(new Date());
        addOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());

        addOrderEvent.setTaskname("订单-" + order.getId());
        addOrderEvent.setTaskdesc("订单超时未支付处理");
        addOrderEvent.setStartAt(new Date(order.getAddTime().getTime() + maxPayTime));
        addOrderEvent.setEndAt(new Date(order.getAddTime().getTime() + maxPayTime * 2));

        addOrderEvent.setTasktype(TaskTypeEnum.SIMPLY.getCode());
        addOrderEvent.setTaskInvokeType(TaskInvokeTypeEnum.LOCAL.getCode());

        addOrderEvent.setTaskRepeatCount(2);
        addOrderEvent.setTaskInterval(60);

        addOrderEvent.setTaskClassName("com.ghf.exchange.otc.order.service.OrderService");
        addOrderEvent.setTaskMethodName("cancelOrderForClient");
        addOrderEvent.setTaskParameterClassName("com.ghf.exchange.otc.order.dto.CancelOrderForClientReqDTO");
        CancelOrderForClientReqDTO cancelOrderForClientReqDTO = new CancelOrderForClientReqDTO();
        cancelOrderForClientReqDTO.setOrderCode(addOrderReqDTO.getOrderCode());
        addOrderEvent.setTaskParameterJson(JsonUtil.toJsonString(cancelOrderForClientReqDTO));
        addOrderEvent.setAdvertiseCode(order.getAdvertiseCode());
        applicationEventPublisher.publishEvent(addOrderEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> payOrder(PayOrderReqDTO payOrderReqDTO) {
        //TODO 分布式锁，分布式事务
        String orderCode = payOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //已下单状态的订单才允许付款
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //订单为买币订单时，订单顾客才允许付款
            if (!username.equals(afterOrder.getOrderCustomerUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }
        } else {
            //订单为卖币订单时，广告商家才允许付款
            if (!username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }
        }

        afterOrder.setStatus(OrderStatusEnum.PAY.getCode());
        //设置付款时间
        afterOrder.setPayTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        //发送到消息队列
        PayOrderEvent payOrderEvent = ModelMapperUtil.map(afterOrder, PayOrderEvent.class);
        payOrderEvent.setOrderCode(afterOrder.getOrderCode());
        payOrderEvent.setOrderLogType(OrderLogTypeEnum.PAY_ORDER.getCode());
        payOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        payOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        payOrderEvent.setCreateTime(new Date());
        payOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        payOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(payOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> agreeUnPayOrderAppealForClient(AgreeUnPayOrderAppealForClientReqDTO agreeUnPayOrderAppealForClientReqDTO) {
        //TODO 分布式锁，分布式事务

        String orderCode = agreeUnPayOrderAppealForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登录用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //已付款状态的订单被申诉未付款
        if (afterOrder.getStatus() != OrderStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_APPEAL);
        }

        afterOrder.setStatus(OrderStatusEnum.ADD.getCode());
        //设置确认未付款时间
        afterOrder.setAgreeUnPayTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        //发送到消息队列
        AgreeUnPayOrderEvent agreeUnPayOrderEvent = ModelMapperUtil.map(afterOrder, AgreeUnPayOrderEvent.class);
        agreeUnPayOrderEvent.setOrderCode(afterOrder.getOrderCode());
        agreeUnPayOrderEvent.setOrderLogType(OrderLogTypeEnum.AGREE_UN_PAY_ORDER.getCode());
        agreeUnPayOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        agreeUnPayOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        agreeUnPayOrderEvent.setCreateTime(new Date());
        agreeUnPayOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        agreeUnPayOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(agreeUnPayOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> releaseOrder(ReleaseOrderReqDTO releaseOrderReqDTO) {
        //TODO 分布式锁，分布式事务
        String orderCode = releaseOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //已付款状态的订单才允许付款
        if (afterOrder.getStatus() != OrderStatusEnum.PAY.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_PAY);
        }

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //订单为买币订单时，广告商家才允许放行
            if (!username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }

            //扣减卖币方账户冻结余额
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.add(afterOrder.getAdvertiseCoinRate())));
            decFrozenBalanceReqDTO.setRemark("放行买币订单时扣减广告商家的账户冻结余额(订单交易数量和手续费)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //增加买币方的账户余额
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            incBalanceReqDTO.setRemark("放行买币订单时增加订单顾客的账户余额(订单交易数量)");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }

        } else {
            //订单为卖币订单时，订单顾客顾客才允许放行
            if (!username.equals(afterOrder.getOrderCustomerUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }

            //扣减卖币方账户冻结余额
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            decFrozenBalanceReqDTO.setRemark("放行卖币订单时扣减订单顾客的账户冻结余额(订单交易数量)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //增加买币方的账户余额
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.subtract(afterOrder.getAdvertiseCoinRate())));
            incBalanceReqDTO.setRemark("放行卖币订单时增加广告商家的账户余额（订单交易数量减去手续费）");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);

            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }
        }

        //增加平台的账户余额
        IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
        incBalanceReqDTO.setUsername(AccountUsernameEnum.ADMIN_USER_NAME.getCode());
        incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
        incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(afterOrder.getAdvertiseCoinRate()));
        incBalanceReqDTO.setRemark("放行订单时增加平台的账户余额(手续费)");
        Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
        if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
        }

        //扣减广告冻结库存数量
        DecAdvertiseFrozenAmountReqDTO decFrozenAmountReqDTO = new DecAdvertiseFrozenAmountReqDTO();
        decFrozenAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        decFrozenAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        decFrozenAmountReqDTO.setRemark("放行订单时扣减广告冻结库存数量(订单交易数量)");
        Result decAdvertiseFrozenAmountResult = advertiseService.decAdvertiseFrozenAmount(decFrozenAmountReqDTO);
        if (decAdvertiseFrozenAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(decAdvertiseFrozenAmountResult.getCode(), decAdvertiseFrozenAmountResult.getMsg());
        }

        afterOrder.setStatus(OrderStatusEnum.RELEASE.getCode());
        //设置放行时间
        afterOrder.setReleaseTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        //发送到消息队列
        ReleaseOrderEvent releaseOrderEvent = ModelMapperUtil.map(afterOrder, ReleaseOrderEvent.class);
        releaseOrderEvent.setOrderCode(afterOrder.getOrderCode());
        releaseOrderEvent.setOrderLogType(OrderLogTypeEnum.RELEASE_ORDER.getCode());
        releaseOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        releaseOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        releaseOrderEvent.setCreateTime(new Date());
        releaseOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        releaseOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(releaseOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> agreeUnReleaseOrderAppealForClient(AgreeUnReleaseOrderAppealForClientReqDTO agreeUnReleaseOrderAppealForClientReqDTO) {
        //TODO 分布式锁，分布式事务
        String orderCode = agreeUnReleaseOrderAppealForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //已付款状态的订单被申诉未放行
        if (afterOrder.getStatus() != OrderStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_APPEAL);
        }

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {

            //扣减卖币方账户冻结余额
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.add(afterOrder.getAdvertiseCoinRate())));
            decFrozenBalanceReqDTO.setRemark("放行买币订单时扣减广告商家的账户冻结余额(订单交易数量和手续费)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //增加买币方的账户余额
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            incBalanceReqDTO.setRemark("放行买币订单时增加订单顾客的账户余额(订单交易数量)");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }

        } else {

            //扣减卖币方账户冻结余额
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            decFrozenBalanceReqDTO.setRemark("放行卖币订单时扣减订单顾客的账户冻结余额(订单交易数量)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //增加买币方的账户余额
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.subtract(afterOrder.getAdvertiseCoinRate())));
            incBalanceReqDTO.setRemark("放行卖币订单时增加广告商家的账户余额（订单交易数量减去手续费）");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);

            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }
        }

        //增加平台的账户余额
        IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
        incBalanceReqDTO.setUsername(AccountUsernameEnum.ADMIN_USER_NAME.getCode());
        incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
        incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(afterOrder.getAdvertiseCoinRate()));
        incBalanceReqDTO.setRemark("放行订单时增加平台的账户余额(手续费)");
        Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
        if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
        }

        //扣减广告冻结库存数量
        DecAdvertiseFrozenAmountReqDTO decFrozenAmountReqDTO = new DecAdvertiseFrozenAmountReqDTO();
        decFrozenAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        decFrozenAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        decFrozenAmountReqDTO.setRemark("放行订单时扣减广告冻结库存数量(订单交易数量)");
        Result decAdvertiseFrozenAmountResult = advertiseService.decAdvertiseFrozenAmount(decFrozenAmountReqDTO);
        if (decAdvertiseFrozenAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(decAdvertiseFrozenAmountResult.getCode(), decAdvertiseFrozenAmountResult.getMsg());
        }

        afterOrder.setStatus(OrderStatusEnum.RELEASE.getCode());
        //设置放行时间
        afterOrder.setAgreeUnReleaseTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        //发送到消息队列
        AgreeUnReleaseOrderEvent agreeUnReleaseOrderEvent = ModelMapperUtil.map(afterOrder, AgreeUnReleaseOrderEvent.class);
        agreeUnReleaseOrderEvent.setOrderCode(afterOrder.getOrderCode());
        agreeUnReleaseOrderEvent.setOrderLogType(OrderLogTypeEnum.AGREE_UN_RELEASE_ORDER.getCode());
        agreeUnReleaseOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        agreeUnReleaseOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        agreeUnReleaseOrderEvent.setCreateTime(new Date());
        agreeUnReleaseOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        agreeUnReleaseOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(agreeUnReleaseOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> cancelOrder(CancelOrderReqDTO cancelOrderReqDTO) {
        //TODO 分布式锁，分布式事务

        String orderCode = cancelOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //默认无权限
        boolean flag = false;

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登录用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //已经取消的无法取消
        if (afterOrder.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_CANCEL);
        }
        //下单状态的订单才允许取消
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        //卖币方无权限取消，防止线下转账后，买币方还未来得及点击确认付款就被取消
        String username = currentLoginUser.getUsername();
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //订单为买币订单时，并且为订单顾客时，则有权限
            if (username.equals(afterOrder.getOrderCustomerUsername())) {
                flag = true;

            }
        } else {
            //订单为卖币订单时，并且为广告商家，则有权限
            if (username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                flag = true;
            }
        }

        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //设置取消时间
        afterOrder.setCancelTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
        unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        unFreezeAmountReqDTO.setRemark("取消订单时解冻广告库存数量(订单交易数量)");
        Result unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("取消卖币订单时解冻订单顾客的账户余额(订单交易数量)");
            Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
            if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
            }
        }

        //发送到消息队列
        CancelOrderEvent cancelOrderEvent = ModelMapperUtil.map(afterOrder, CancelOrderEvent.class);
        cancelOrderEvent.setOrderCode(afterOrder.getOrderCode());
        cancelOrderEvent.setOrderLogType(OrderLogTypeEnum.CANCEL_ORDER.getCode());
        cancelOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        cancelOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        cancelOrderEvent.setCreateTime(new Date());
        cancelOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        cancelOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(cancelOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> cancelOrderForAdmin(CancelOrderForAdminReqDTO cancelOrderForAdminReqDTO) {
        //TODO 分布式锁，分布式事务

        String orderCode = cancelOrderForAdminReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登录用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //已经取消的无法取消
        if (afterOrder.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_CANCEL);
        }
        //下单状态的订单才允许取消
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //设置取消时间
        afterOrder.setCancelTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
        unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        unFreezeAmountReqDTO.setRemark("取消订单时解冻广告库存数量(订单交易数量)");
        Result unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("取消卖币订单时解冻订单顾客的账户余额(订单交易数量)");
            Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
            if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
            }
        }

        //发送到消息队列
        CancelOrderEvent cancelOrderEvent = ModelMapperUtil.map(afterOrder, CancelOrderEvent.class);
        cancelOrderEvent.setOrderCode(afterOrder.getOrderCode());
        cancelOrderEvent.setOrderLogType(OrderLogTypeEnum.CANCEL_ORDER.getCode());
        cancelOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        cancelOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        cancelOrderEvent.setCreateTime(new Date());
        cancelOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        cancelOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(cancelOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> cancelOrderForClient(CancelOrderForClientReqDTO cancelOrderForClientReqDTO) {
        //TODO 分布式锁，分布式事务

        String orderCode = cancelOrderForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登录用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //已经取消的无法取消
        if (afterOrder.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_CANCEL);
        }
        //下单状态的订单才允许取消
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        //支付时间正常，微服务客户端无法取消
        if (afterOrder.getAddTime().getTime() + maxPayTime > System.currentTimeMillis()) {
            return new Result<>(ResultCodeEnum.ORDER_PAY_TIME_LEGAL);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //设置取消时间
        afterOrder.setCancelTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
        unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        unFreezeAmountReqDTO.setRemark("取消订单时解冻广告库存数量(订单交易数量)");
        Result unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("取消卖币订单时解冻订单顾客的账户余额(订单交易数量)");
            Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
            if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
            }
        }

        //发送到消息队列
        CancelOrderEvent cancelOrderEvent = ModelMapperUtil.map(afterOrder, CancelOrderEvent.class);
        cancelOrderEvent.setOrderCode(afterOrder.getOrderCode());
        cancelOrderEvent.setOrderLogType(OrderLogTypeEnum.CANCEL_ORDER.getCode());
        cancelOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        cancelOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        cancelOrderEvent.setCreateTime(new Date());
        cancelOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        cancelOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(cancelOrderEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @Transactional
    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateOrderStatusForClient(UpdateOrderStatusForClientReqDTO updateOrderStatusForClientReqDTO) {
        //TODO 分布式锁，分布式事务

        String orderCode = updateOrderStatusForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登录用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //设置支付期限为10分钟
        orderService.getJpaQueryFactory()
                .update(QOrder.order)
                .set(QOrder.order.status, updateOrderStatusForClientReqDTO.getStatus())
                .where(QOrder.order.orderCode.eq(
                        updateOrderStatusForClientReqDTO.getOrderCode()

                )).execute();

        //发送到消息队列
        UpdateOrderStatusEvent cancelOrderEvent = ModelMapperUtil.map(afterOrder, UpdateOrderStatusEvent.class);
        cancelOrderEvent.setOrderCode(updateOrderStatusForClientReqDTO.getOrderCode());
        cancelOrderEvent.setOrderLogType(OrderLogTypeEnum.CANCEL_ORDER.getCode());
        cancelOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        cancelOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        cancelOrderEvent.setCreateTime(new Date());
        cancelOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());
        cancelOrderEvent.setAdvertiseCode(afterOrder.getAdvertiseCode());
        applicationEventPublisher.publishEvent(cancelOrderEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> deleteAllOrderForAdmin() {
        //TODO 分布式锁，分布式事务
        //设置支付期限为10分钟
        orderService.getJpaQueryFactory()
                .delete(QOrder.order)
                .execute();
        return new Result<>(ResultCodeEnum.OK);
    }


    //TODO 订单客户只能查看国家跟自己相同的广告，订单客户只能查看法币跟自己支持的广告
    //TODO 订单客户只能交易国家跟自己相同的广告，订单客户只能交易法币跟自己支持的广告
}