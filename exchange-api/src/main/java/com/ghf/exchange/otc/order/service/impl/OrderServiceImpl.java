package com.ghf.exchange.otc.order.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.ClientRespDTO;
import com.ghf.exchange.boss.authorication.client.enums.ClientScopeEnum;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorication.user.util.IpUtil;
import com.ghf.exchange.boss.authorization.role.enums.RolenameEnum;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.exception.ResultCodeException;
import com.ghf.exchange.otc.account.dto.DecFrozenBalanceReqDTO;
import com.ghf.exchange.otc.account.dto.FreezeBalanceReqDTO;
import com.ghf.exchange.otc.account.dto.IncBalanceReqDTO;
import com.ghf.exchange.otc.account.dto.UnFreezeBalanceReqDTO;
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
import com.ghf.exchange.otc.order.enums.OrderCustomerPaymentTermTypeEnum;
import com.ghf.exchange.otc.order.enums.OrderSourceEnum;
import com.ghf.exchange.otc.order.enums.OrderStatusEnum;
import com.ghf.exchange.otc.order.event.AddOrderEvent;
import com.ghf.exchange.otc.order.repository.OrderRepository;
import com.ghf.exchange.otc.order.service.OrderService;
import com.ghf.exchange.otc.orderlog.dto.AddOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.enums.OrderLogTypeEnum;
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
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderServiceImpl(OrderRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Order", key = "'pageOrder:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orderCode).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.orderCustomerPaymentTermType).concat(':').concat(#p0.orderSource).concat(':').concat(#p0.orderBuySellType).concat(':').concat(#p0.advertiseCoinCode).concat(':').concat(#p0.status) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.advertiseBusinessUsername) && T(org.springframework.util.StringUtils).isEmpty(#p0.orderCustomerUsername)      && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderRespDTO>> pageOrder(PageOrderReqDTO pageOrderReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getOrderCode())) {
            predicate.and(QOrder.order.orderCode.contains(pageOrderReqDTO.getOrderCode()));
        }

        if (pageOrderReqDTO.getOrderCustomerPaymentTermType() == OrderCustomerPaymentTermTypeEnum.ALIPAY.getCode()
                || pageOrderReqDTO.getOrderCustomerPaymentTermType() == OrderCustomerPaymentTermTypeEnum.WECHAT.getCode()
                || pageOrderReqDTO.getOrderCustomerPaymentTermType() == OrderCustomerPaymentTermTypeEnum.BANK.getCode()
        ) {
            predicate.and(QOrder.order.orderCustomerPaymentTermType.eq(pageOrderReqDTO.getOrderCustomerPaymentTermType()));
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

        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getOrderCustomerUsername())) {
            predicate.and(QOrder.order.orderCustomerUsername.contains(pageOrderReqDTO.getOrderCustomerUsername()));
        }

        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getAdvertiseCode())) {
            predicate.and(QOrder.order.advertiseCode.contains(pageOrderReqDTO.getAdvertiseCode()));
        }
        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getAdvertiseCoinCode())) {
            predicate.and(QOrder.order.advertiseCoinCode.contains(pageOrderReqDTO.getAdvertiseCoinCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderReqDTO.getAdvertiseBusinessUsername())) {
            predicate.and(QOrder.order.advertiseBusinessUsername.contains(pageOrderReqDTO.getAdvertiseBusinessUsername()));
        }

        if (pageOrderReqDTO.getStatus() == OrderStatusEnum.ADD.getCode()
                || pageOrderReqDTO.getStatus() == OrderStatusEnum.PAY.getCode()
                || pageOrderReqDTO.getStatus() == OrderStatusEnum.CANCEL.getCode()
                || pageOrderReqDTO.getStatus() == OrderStatusEnum.RELEASE.getCode()

        ) {
            predicate.and(QOrder.order.status.eq(pageOrderReqDTO.getStatus()));
        }

        PageRespDTO<OrderRespDTO> pageResult = orderService.page(predicate, pageOrderReqDTO, OrderRespDTO.class);

        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "Order", key = "'getOrderByOrderCode:'+#p0.orderCode")
    @Override
    @SneakyThrows
    public Result<OrderRespDTO> getOrderByOrderCode(GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO) {

        String orderCode = getOrderByOrderCodeReqDTO.getOrderCode();
        Predicate predicate = QOrder.order.orderCode.eq(orderCode);
        Order order = orderService.get(predicate);

        //返回
        OrderRespDTO advertisRespDTO = AutoMapUtils.map(order, OrderRespDTO.class);

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

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrder(AddOrderReqDTO addOrderReqDTO) {
        //TODO 分布式锁，分布式事务
        Order order = AutoMapUtils.map(addOrderReqDTO, Order.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //初始化id
        order.setId(IdUtil.generateLongId());
        //判断订单编号
        if (!ObjectUtils.isEmpty(order.getOrderCode())) {
            //判断唯一性
            String orderCode = addOrderReqDTO.getOrderCode();
            GetOrderByOrderCodeReqDTO getRoleByRolenameReqDTO = new GetOrderByOrderCodeReqDTO();
            getRoleByRolenameReqDTO.setOrderCode(orderCode);
            boolean b = orderService.existsOrderByOrderCode(getRoleByRolenameReqDTO).getData();
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
        }

        //获取币种信息
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode(orderCoinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //获取订单交易量
        BigDecimal orderAmount = addOrderReqDTO.getOrderAmount();
        //校验订单交易量是否满足单笔交易量限制
        if (orderAmount.compareTo(coinRespDTO.getPerMinAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ORDER_AMOUNT_LESS_THEN_PER_MIN_AMOUNT_ERROR);
        }
        if (orderAmount.compareTo(coinRespDTO.getPerMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ORDER_AMOUNT_GREATER_THEN_PER_MAX_AMOUNT_ERROR);
        }

        //校验订单顾客选择的收付款类型
        int orderCustomerPaymentTermType = addOrderReqDTO.getOrderCustomerPaymentTermType();
        if (orderCustomerPaymentTermType != OrderCustomerPaymentTermTypeEnum.ALIPAY.getCode() && orderCustomerPaymentTermType != OrderCustomerPaymentTermTypeEnum.WECHAT.getCode() && orderCustomerPaymentTermType != OrderCustomerPaymentTermTypeEnum.BANK.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_PAYMENT_TERM_TYPE_NOT_EXISTS);
        }

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
            GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getAdvertiseByCodeReqDTO.setAdvertiseCode(advertiseCode);
            advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();

            //确定广告买卖类型
            advertiseBuySellType = advertiseRespDTO.getAdvertiseBuySellType();
            //确定订单买卖类型
            orderBuySellType = advertiseBuySellType == AdvertiseBuySellTypeEnum.BUY.getCode() ? OrderBuySellTypeEnum.SELL.getCode() : OrderBuySellTypeEnum.BUY.getCode();

            //确定单价
            if (advertiseRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
                //广告价格为固定时
                orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
            } else {
                //广告价格为浮动时
                orderPrice = coinRespDTO.getMarketPrice();
            }

            //校验订单交易量是否满足广告库存数量
            if (orderAmount.compareTo(advertiseRespDTO.getAdvertiseAmount()) > 0) {
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
            getMatchAdvertiseReqDTO.setAdvertiseBusinessPaymentTermTypeArray(orderCustomerPaymentTermType + "");

            advertiseRespDTO = advertiseService.getMatchAdvertise(getMatchAdvertiseReqDTO).getData();

            if (addOrderReqDTO == null) {
                return new Result<>(ResultCodeEnum.ORDER_ONE_KEY_MATCH_ERROR);
            } else {
                //确定最终的单价
                orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
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
        //设置订单顾客选择的收付款类型
        order.setOrderCustomerPaymentTermType(orderCustomerPaymentTermType);

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
        order.setAdvertiseAmount(advertiseRespDTO.getAdvertiseAmount());
        order.setAdvertisePerMaxAmount(advertiseRespDTO.getAdvertisePerMaxAmount());
        order.setAdvertisePerMinAmount(advertiseRespDTO.getAdvertisePerMinAmount());
        order.setAdvertiseLegalCurrencyCountryCode(advertiseRespDTO.getAdvertiseLegalCurrencyCountryCode());
        order.setAdvertiseLegalCurrencySymbol(advertiseRespDTO.getAdvertiseLegalCurrencySymbol());
        order.setAdvertiseLegalCurrencyUnit(advertiseRespDTO.getAdvertiseLegalCurrencyUnit());
        order.setAdvertisePriceType(advertiseRespDTO.getAdvertisePriceType());
        order.setAdvertiseFixedPrice(advertiseRespDTO.getAdvertiseFixedPrice());
        order.setAdvertisePremiumRate(advertiseRespDTO.getAdvertisePremiumRate());
        order.setAdvertiseBusinessPaymentTermTypeArray(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeArray());
        order.setAdvertiseBusinessUsername(advertiseRespDTO.getAdvertiseBusinessUsername());
        order.setAdvertiseBusinessNickname(advertiseRespDTO.getAdvertiseBusinessNickname());
        order.setAdvertiseBusinessRealname(advertiseRespDTO.getAdvertiseBusinessRealname());

        //设置订单顾客的支付信息
        int orderCustomerPaymentTermTime = addOrderReqDTO.getOrderCustomerPaymentTermTime();
        if (orderCustomerPaymentTermTime < coinRespDTO.getMinPaymentTermTime() || orderCustomerPaymentTermTime > coinRespDTO.getMaxPaymentTermTime()) {
            //如果用户没有设置支付期限，或者设置了非法的支付期限，直接设置为系统默认的最大支付期限
            orderCustomerPaymentTermTime = coinRespDTO.getMaxPaymentTermTime();
        }
        order.setOrderCustomerPaymentTermTime(orderCustomerPaymentTermTime);
        order.setOrderCustomerPaymentTermTypeArray(addOrderReqDTO.getOrderCustomerPaymentTermTypeArray());
        order.setOrderCustomerPaymentTermTypeAlipayAccount(addOrderReqDTO.getOrderCustomerPaymentTermTypeAlipayAccount());
        order.setOrderCustomerPaymentTermTypeAlipayQrcode(addOrderReqDTO.getOrderCustomerPaymentTermTypeAlipayQrcode());
        order.setOrderCustomerPaymentTermTypeWechatAccount(addOrderReqDTO.getOrderCustomerPaymentTermTypeWechatAccount());
        order.setOrderCustomerPaymentTermTypeWechatQrcode(addOrderReqDTO.getOrderCustomerPaymentTermTypeWechatQrcode());
        order.setOrderCustomerPaymentTermTypeBankName(addOrderReqDTO.getOrderCustomerPaymentTermTypeBankName());
        order.setOrderCustomerPaymentTermTypeBankBranchName(addOrderReqDTO.getOrderCustomerPaymentTermTypeBankBranchName());
        order.setOrderCustomerPaymentTermTypeBankAccount(addOrderReqDTO.getOrderCustomerPaymentTermTypeBankAccount());
        order.setOrderCustomerPaymentTermTypeBankRealname(addOrderReqDTO.getOrderCustomerPaymentTermTypeBankRealname());
        //设置广告商家的支付信息
        order.setAdvertiseBusinessPaymentTermTime(advertiseRespDTO.getAdvertiseBusinessPaymentTermTime());
        order.setAdvertiseBusinessPaymentTermTypeArray(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeArray());
        order.setAdvertiseBusinessPaymentTermTypeAlipayAccount(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeAlipayAccount());
        order.setAdvertiseBusinessPaymentTermTypeAlipayQrcode(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeAlipayQrcode());
        order.setAdvertiseBusinessPaymentTermTypeWechatAccount(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeWechatAccount());
        order.setAdvertiseBusinessPaymentTermTypeWechatQrcode(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeWechatQrcode());
        order.setAdvertiseBusinessPaymentTermTypeBankName(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeBankName());
        order.setAdvertiseBusinessPaymentTermTypeBankBranchName(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeBankBranchName());
        order.setAdvertiseBusinessPaymentTermTypeBankAccount(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeBankAccount());
        order.setAdvertiseBusinessPaymentTermTypeBankRealname(advertiseRespDTO.getAdvertiseBusinessPaymentTermTypeBankRealname());

        //广告状态设置已下单
        order.setStatus(OrderStatusEnum.ADD.getCode());
        //设置广告创建时间
        order.setCreateTime(new Date());
        //设置备注
        order.setRemark(addOrderReqDTO.getRemark());
        //持久化到数据库
        orderService.add(order);

        Result freezeAmountResult = null;
        if (orderBuySellType == OrderBuySellTypeEnum.BUY.getCode()) {
            //买币订单需要冻结广告商家的广告库存
            FreezeAdvertiseAmountReqDTO freezeAmountReqDTO = new FreezeAdvertiseAmountReqDTO();
            freezeAmountReqDTO.setAdvertiseCode(advertiseRespDTO.getAdvertiseCode());
            freezeAmountReqDTO.setAdvertiseAmount(order.getOrderAmount());
            freezeAmountReqDTO.setRemark("订单" + order.getOrderCode() + "冻结");
            freezeAmountResult = advertiseService.freezeAdvertiseAmount(freezeAmountReqDTO);
        } else {
            //卖币订单需要冻结订单顾客的账户余额
            FreezeBalanceReqDTO freezeBalanceReqDTO = new FreezeBalanceReqDTO();
            freezeBalanceReqDTO.setUsername(order.getOrderCustomerUsername());
            freezeBalanceReqDTO.setCoinCode(order.getOrderCoinCode());
            freezeBalanceReqDTO.setBalance(order.getOrderAmount());
            freezeBalanceReqDTO.setRemark("订单" + order.getOrderCode() + "冻结");
            freezeAmountResult = accountService.freezeBalance(freezeBalanceReqDTO);
        }

        if (freezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(freezeAmountResult.getCode(), freezeAmountResult.getMsg());
        }

        //发送到消息队列
        AddOrderLogReqDTO addOrderLogReqDTO = new AddOrderLogReqDTO();
        addOrderLogReqDTO.setOrderCode(order.getOrderCode());
        addOrderLogReqDTO.setOrderLogType(OrderLogTypeEnum.ADD_ORDER.getCode());
        addOrderLogReqDTO.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderLogReqDTO.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderLogReqDTO.setOrderLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new AddOrderEvent(addOrderLogReqDTO));

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
        Order afterOrder = AutoMapUtils.map(orderRespDTO, Order.class);

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
        AddOrderLogReqDTO addOrderLogReqDTO = new AddOrderLogReqDTO();
        addOrderLogReqDTO.setOrderCode(afterOrder.getOrderCode());
        addOrderLogReqDTO.setOrderLogType(OrderLogTypeEnum.PAY_ORDER.getCode());
        addOrderLogReqDTO.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderLogReqDTO.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderLogReqDTO.setOrderLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new AddOrderEvent(addOrderLogReqDTO));

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
        Order afterOrder = AutoMapUtils.map(orderRespDTO, Order.class);

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

            //扣减广告冻结数量
            DecAdvertiseFrozenAmountReqDTO decFrozenAmountReqDTO = new DecAdvertiseFrozenAmountReqDTO();
            decFrozenAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
            decFrozenAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
            decFrozenAmountReqDTO.setRemark("放行订单时扣减广告冻结数量");
            advertiseService.decAdvertiseFrozenAmount(decFrozenAmountReqDTO);

            //扣减卖币方账户冻结余额
            DecFrozenBalanceReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.add(afterOrder.getAdvertiseCoinRate())));
            decFrozenBalanceReqDTO.setRemark("放行订单时扣减账户冻结余额");
            accountService.decFrozenBalance(decFrozenBalanceReqDTO);

            //增加买币方的账户余额
            IncBalanceReqDTO incBalanceReqDTO = new IncBalanceReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            incBalanceReqDTO.setRemark("放行订单抽取手续费时增加账户余额");
            accountService.incBalance(incBalanceReqDTO);

            //增加平台账户余额
            incBalanceReqDTO = new IncBalanceReqDTO();
            incBalanceReqDTO.setUsername(AccountUsernameEnum.ADMIN_USER_NAME.getCode());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(afterOrder.getAdvertiseCoinRate()));
            incBalanceReqDTO.setRemark("放行订单抽取手续费时增加账户余额");
            accountService.incBalance(incBalanceReqDTO);

        } else {
            //订单为卖币订单时，订单顾客顾客才允许放行
            if (!username.equals(afterOrder.getOrderCustomerUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }

            //扣减卖币方账户冻结余额
            DecFrozenBalanceReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            decFrozenBalanceReqDTO.setRemark("放行订单时扣减账户冻结余额");
            accountService.decFrozenBalance(decFrozenBalanceReqDTO);

            //增加买币方的账户余额
            IncBalanceReqDTO incBalanceReqDTO = new IncBalanceReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.subtract(afterOrder.getAdvertiseCoinRate())));
            incBalanceReqDTO.setRemark("放行订单抽取手续费时增加账户余额");
            accountService.incBalance(incBalanceReqDTO);

            //增加平台账户余额
            incBalanceReqDTO = new IncBalanceReqDTO();
            incBalanceReqDTO.setUsername(AccountUsernameEnum.ADMIN_USER_NAME.getCode());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(afterOrder.getAdvertiseCoinRate()));
            incBalanceReqDTO.setRemark("放行订单抽取手续费时增加账户余额");
            accountService.incBalance(incBalanceReqDTO);

        }

        afterOrder.setStatus(OrderStatusEnum.RELEASE.getCode());
        //设置放行时间
        afterOrder.setReleaseTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        //发送到消息队列
        AddOrderLogReqDTO addOrderLogReqDTO = new AddOrderLogReqDTO();
        addOrderLogReqDTO.setOrderCode(afterOrder.getOrderCode());
        addOrderLogReqDTO.setOrderLogType(OrderLogTypeEnum.RELEASE_ORDER.getCode());
        addOrderLogReqDTO.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderLogReqDTO.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderLogReqDTO.setOrderLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new AddOrderEvent(addOrderLogReqDTO));

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
        Order afterOrder = AutoMapUtils.map(orderRespDTO, Order.class);

        //默认无权限
        boolean flag = false;

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登录用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        if (currentLoginClient.getScopes().contains(ClientScopeEnum.SERVER.getCode())) {
            //已下单状态的订单才允许取消
            if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
                return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
            }
            //内部后端服务器发现超时未支付，则有权限取消订单
            if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                if (afterOrder.getCreateTime().getTime() + afterOrder.getAdvertiseBusinessPaymentTermTime() * 60 * 1000 > System.currentTimeMillis()) {
                    flag = true;
                }
            } else {
                if (afterOrder.getCreateTime().getTime() + afterOrder.getOrderCustomerPaymentTermTime() * 60 * 1000 > System.currentTimeMillis()) {
                    flag = true;
                }
            }
        } else {

            if (currentLoginUser.getRolenameSet().contains(RolenameEnum.ROLE_ADMIN.getCode())) {
                //已付款状态的订单但未真正付款，由于被申诉才允许取消
                if (afterOrder.getStatus() != OrderStatusEnum.PAY.getCode()) {
                    return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_PAY);
                }
                //管理员角色发现已付款订单实际并未付款，则有权限取消订单
                flag = true;
            } else {
                //已下单状态的订单才允许取消
                if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
                    return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
                }
                String username = currentLoginUser.getUsername();
                if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
                    //订单为买币订单时，并且为订单顾客时，则有权限取消订单
                    if (username.equals(afterOrder.getOrderCustomerUsername())) {
                        flag = true;

                    }
                } else {
                    //订单为卖币订单时，并且为广告商家，则有权限取消订单
                    if (username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                        flag = true;
                    }
                }
            }

        }

        if (!flag) {
            //无权限取消订单，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //设置取消时间
        afterOrder.setCancelTime(new Date());

        //持久化到数据库
        orderService.update(afterOrder);

        int orderBuySellType = afterOrder.getOrderBuySellType();

        Result unFreezeAmountResult = null;
        if (orderBuySellType == OrderBuySellTypeEnum.BUY.getCode()) {
            //买币订单需要解冻广告商家的广告库存
            UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
            unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
            unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
            unFreezeAmountReqDTO.setRemark("订单" + afterOrder.getOrderCode() + "解冻");
            unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);
        } else {
            //卖币订单需要解冻订单顾客的账户余额
            UnFreezeBalanceReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("订单" + afterOrder.getOrderCode() + "解冻");
            unFreezeAmountResult = accountService.unFreezeBalance(unFreezeBalanceReqDTO);
        }

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }

        //发送到消息队列
        AddOrderLogReqDTO addOrderLogReqDTO = new AddOrderLogReqDTO();
        addOrderLogReqDTO.setOrderCode(afterOrder.getOrderCode());
        addOrderLogReqDTO.setOrderLogType(OrderLogTypeEnum.CANCEL_ORDER.getCode());
        addOrderLogReqDTO.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderLogReqDTO.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderLogReqDTO.setOrderLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new AddOrderEvent(addOrderLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> recoverOrder(RecoverOrderReqDTO recoverOrderReqDTO) {
        //TODO 分布式锁，分布式事务
        String orderCode = recoverOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = AutoMapUtils.map(orderRespDTO, Order.class);

        //已取消状态的订单才允许恢复
        if (afterOrder.getStatus() != OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_CANCEL);
        }

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //管理员角色才允许恢复订单
        if (!currentLoginUser.getRolenameSet().contains(RolenameEnum.ROLE_ADMIN.getCode())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrder.setStatus(OrderStatusEnum.ADD.getCode());

        //持久化到数据库
        orderService.update(afterOrder);

        int orderBuySellType = afterOrder.getOrderBuySellType();

        Result freezeAmountResult = null;
        if (orderBuySellType == OrderBuySellTypeEnum.BUY.getCode()) {
            //买币订单需要冻结广告商家的广告库存
            FreezeAdvertiseAmountReqDTO freezeAmountReqDTO = new FreezeAdvertiseAmountReqDTO();
            freezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
            freezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
            freezeAmountReqDTO.setRemark("订单" + afterOrder.getOrderCode() + "冻结");
            freezeAmountResult = advertiseService.freezeAdvertiseAmount(freezeAmountReqDTO);
        } else {
            //卖币订单需要冻结订单顾客的账户余额
            FreezeBalanceReqDTO freezeBalanceReqDTO = new FreezeBalanceReqDTO();
            freezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            freezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            freezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            freezeBalanceReqDTO.setRemark("订单" + afterOrder.getOrderCode() + "冻结");
            freezeAmountResult = accountService.freezeBalance(freezeBalanceReqDTO);
        }

        if (freezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(freezeAmountResult.getCode(), freezeAmountResult.getMsg());
        }

        //发送到消息队列
        AddOrderLogReqDTO addOrderLogReqDTO = new AddOrderLogReqDTO();
        addOrderLogReqDTO.setOrderCode(afterOrder.getOrderCode());
        addOrderLogReqDTO.setOrderLogType(OrderLogTypeEnum.RECOVER_ORDER.getCode());
        addOrderLogReqDTO.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderLogReqDTO.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderLogReqDTO.setOrderLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new AddOrderEvent(addOrderLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);

    }

}