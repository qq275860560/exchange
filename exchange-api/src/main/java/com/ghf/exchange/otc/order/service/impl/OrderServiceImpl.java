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
        //??????????????????????????????
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

        //??????
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
        //TODO ??????????????????????????????
        Order order = ModelMapperUtil.map(addOrderReqDTO, Order.class);

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //??????????????????????????????????????????????????????
        ExistsAddStatusOrderForClientReqDTO existsAddStatusOrderForClientReqDTO = new ExistsAddStatusOrderForClientReqDTO();
        existsAddStatusOrderForClientReqDTO.setUsername(username);
        boolean b = orderService.existsUnPayOrderForClient(existsAddStatusOrderForClientReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ORDER_ADD_STATUS_EXISTS);
        }
        //??????????????????????????????????????????????????????????????????
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(username);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO.getOrderTodayCancelCount() >= maxCancelTimes) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_ORDER_TODAY_CANCEL_COUNT_OVERLOAD);
        }

        //?????????id
        order.setId(IdUtil.generateLongId());
        //??????????????????
        if (!ObjectUtils.isEmpty(order.getOrderCode())) {
            //???????????????
            String orderCode = addOrderReqDTO.getOrderCode();
            GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
            getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
            b = orderService.existsOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_EXISTS);
            }
            order.setOrderCode(addOrderReqDTO.getOrderCode());
        } else {
            //????????????????????????
            order.setOrderCode(order.getId() + "");
        }

        //????????????
        int orderSource = addOrderReqDTO.getOrderSource();
        if (orderSource != OrderSourceEnum.ADVERTISE_SELECT.getCode() && orderSource != OrderSourceEnum.SHORTCUT_ONE_KEY_MATCH.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_SOURCE_NOT_EXISTS);
        }

        //????????????????????????
        String orderCoinCode = null;
        //?????????????????????,??????????????????????????????
        if (orderSource == OrderSourceEnum.ADVERTISE_SELECT.getCode()) {
            //????????????
            String advertiseCode = addOrderReqDTO.getAdvertiseCode();
            GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getAdvertiseByCodeReqDTO.setAdvertiseCode(advertiseCode);
            AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
            orderCoinCode = advertiseRespDTO.getAdvertiseCoinCode();
        }
        //????????????????????????????????????????????????????????????
        else {

            orderCoinCode = addOrderReqDTO.getOrderCoinCode();
            if (ObjectUtils.isEmpty(orderCoinCode)) {
                return new Result<>(ResultCodeEnum.ORDER_COIN_CODE_CAN_NOT_EMPTY);
            }
        }

        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode(orderCoinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //?????????????????????
        BigDecimal orderAmount = addOrderReqDTO.getOrderAmount();
        //??????????????????????????????????????????????????????
        if (orderAmount.compareTo(coinRespDTO.getPerOrderMinAmount()) < 0 || orderAmount.compareTo(coinRespDTO.getPerOrderMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ORDER_AMOUNT_ERROR);
        }

        //??????????????????????????????????????????
        if (addOrderReqDTO.getOrderCustomerPaymentType() != PaymentTypeEnum.ALIPAY.getCode() && addOrderReqDTO.getOrderCustomerPaymentType() != PaymentTypeEnum.WECHAT.getCode() && addOrderReqDTO.getOrderCustomerPaymentType() != PaymentTypeEnum.BANK.getCode()) {
            return new Result<>(ResultCodeEnum.PAYMENT_TYPE_NOT_EXISTS);
        }
        //??????????????????????????????????????????
        order.setOrderCustomerPaymentType(addOrderReqDTO.getOrderCustomerPaymentType());

        //????????????
        AdvertiseRespDTO advertiseRespDTO = null;
        //??????????????????
        int orderBuySellType = 0;

        //??????????????????
        int advertiseBuySellType = 0;

        BigDecimal orderPrice = null;
        //?????????????????????
        if (orderSource == OrderSourceEnum.ADVERTISE_SELECT.getCode()) {
            //?????????????????????????????????????????????
            String advertiseCode = addOrderReqDTO.getAdvertiseCode();
            GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
            advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();

            //????????????????????????
            advertiseBuySellType = advertiseRespDTO.getAdvertiseBuySellType();
            //????????????????????????
            orderBuySellType = advertiseBuySellType == AdvertiseBuySellTypeEnum.BUY.getCode() ? OrderBuySellTypeEnum.SELL.getCode() : OrderBuySellTypeEnum.BUY.getCode();

            //????????????
            if (advertiseRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
                //????????????????????????
                orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
            } else {
                orderPrice = coinRespDTO.getMarketPrice();
                //????????????????????????
                if (advertiseRespDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
                    //??????????????????????????????????????????????????????????????????
                    orderPrice = orderPrice.multiply(BigDecimal.ONE.subtract(advertiseRespDTO.getAdvertisePremiumRate()));
                } else {
                    //??????????????????????????????????????????????????????????????????
                    orderPrice = orderPrice.multiply(BigDecimal.ONE.add(advertiseRespDTO.getAdvertisePremiumRate()));
                }

            }

            //???????????????????????????????????????????????????
            if (orderAmount.compareTo(advertiseRespDTO.getAdvertiseAvailableAmount()) > 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
            }
        }
        //???????????????????????????
        else {

            //????????????????????????
            orderBuySellType = addOrderReqDTO.getOrderBuySellType();
            if (orderBuySellType != OrderBuySellTypeEnum.BUY.getCode() && orderBuySellType != OrderBuySellTypeEnum.SELL.getCode()) {
                return new Result<>(ResultCodeEnum.ORDER_BUY_SELL_TYPE_NOT_EXISTS);
            }
            //????????????????????????
            advertiseBuySellType = orderBuySellType == OrderBuySellTypeEnum.BUY.getCode() ? AdvertiseBuySellTypeEnum.SELL.getCode() : AdvertiseBuySellTypeEnum.BUY.getCode();

            //?????????????????????
            orderPrice = coinRespDTO.getMarketPrice();

            //?????????????????????
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

            //????????????
            if (advertiseRespDTO.getAdvertisePriceType() == AdvertisePriceTypeEnum.FIXED.getCode()) {
                //????????????????????????
                orderPrice = advertiseRespDTO.getAdvertiseFixedPrice();
            } else {
                //????????????????????????
                if (getMatchAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
                    //??????????????????????????????????????????????????????????????????
                    orderPrice = orderPrice.multiply(BigDecimal.valueOf(1).subtract(advertiseRespDTO.getAdvertisePremiumRate()));
                } else {
                    //??????????????????????????????????????????????????????????????????
                    orderPrice = orderPrice.multiply(BigDecimal.valueOf(1).add(advertiseRespDTO.getAdvertisePremiumRate()));
                }
            }
            //???????????????????????????????????????????????????
            if (orderAmount.compareTo(advertiseRespDTO.getAdvertiseAvailableAmount()) > 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
            }
        }
        //??????????????????
        order.setOrderSource(orderSource);
        //????????????????????????
        order.setOrderBuySellType(orderBuySellType);
        //????????????????????????
        order.setOrderCoinCode(orderCoinCode);
        order.setOrderCoinName(coinRespDTO.getCoinName());
        order.setOrderCoinUnit(coinRespDTO.getCoinUnit());
        order.setOrderCoinRate(coinRespDTO.getCoinRate());
        //?????????????????????
        order.setOrderAmount(orderAmount);
        //????????????????????????
        order.setOrderPrice(orderPrice);
        //????????????????????????
        order.setOrderTotalPrice(order.getOrderPrice().multiply(order.getOrderAmount()));
        //?????????????????????????????????????????????????????????????????????????????????,?????????????????????????????????????????????

        //TODO ?????????????????????
        //TODO ?????????????????????????????????????????????????????????????????????????????????????????????????????????

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

        //??????????????????
        order.setOrderCustomerUsername(currentLoginUser.getUsername());
        order.setOrderCustomerNickname(currentLoginUser.getNickname());
        order.setOrderCustomerRealname(currentLoginUser.getRealname());

        //??????????????????
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

        //???????????????????????????
        order.setStatus(OrderStatusEnum.ADD.getCode());
        //????????????????????????
        order.setAddTime(new Date());
        //????????????
        order.setRemark(addOrderReqDTO.getRemark());
        //?????????????????????
        orderService.add(order);

        if (orderBuySellType == OrderBuySellTypeEnum.SELL.getCode()) {
            //???????????????????????????????????????????????????(?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)
            FreezeBalanceForClientReqDTO freezeBalanceReqDTO = new FreezeBalanceForClientReqDTO();
            freezeBalanceReqDTO.setUsername(order.getOrderCustomerUsername());
            freezeBalanceReqDTO.setCoinCode(order.getOrderCoinCode());
            freezeBalanceReqDTO.setBalance(order.getOrderAmount());
            freezeBalanceReqDTO.setRemark("?????????????????????????????????????????????(??????????????????)");
            Result freezeBalanceResult = accountService.freezeBalanceForClient(freezeBalanceReqDTO);
            if (freezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(freezeBalanceResult.getCode(), freezeBalanceResult.getMsg());
            }
        }

        //???????????????????????????????????????
        FreezeAdvertiseAmountReqDTO freezeAmountReqDTO = new FreezeAdvertiseAmountReqDTO();
        freezeAmountReqDTO.setAdvertiseCode(advertiseRespDTO.getAdvertiseCode());
        freezeAmountReqDTO.setAdvertiseAmount(order.getOrderAmount());
        freezeAmountReqDTO.setRemark("?????????????????????????????????(??????????????????)");
        Result freezeAdvertiseAmountResult = advertiseService.freezeAdvertiseAmount(freezeAmountReqDTO);
        if (freezeAdvertiseAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(freezeAdvertiseAmountResult.getCode(), freezeAdvertiseAmountResult.getMsg());
        }

        //?????????????????????
        AddOrderEvent addOrderEvent = ModelMapperUtil.map(order, AddOrderEvent.class);
        addOrderEvent.setOrderCode(order.getOrderCode());
        addOrderEvent.setOrderLogType(OrderLogTypeEnum.ADD_ORDER.getCode());
        addOrderEvent.setOrderLogClientId(currentLoginClient.getClientId());
        addOrderEvent.setOrderLogUsername(currentLoginUser.getUsername());
        addOrderEvent.setCreateTime(new Date());
        addOrderEvent.setOrderLogIpAddr(IpUtil.getIpAddr());

        addOrderEvent.setTaskname("??????-" + order.getId());
        addOrderEvent.setTaskdesc("???????????????????????????");
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
        //TODO ??????????????????????????????
        String orderCode = payOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //???????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //??????????????????????????????????????????????????????
            if (!username.equals(afterOrder.getOrderCustomerUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }
        } else {
            //??????????????????????????????????????????????????????
            if (!username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }
        }

        afterOrder.setStatus(OrderStatusEnum.PAY.getCode());
        //??????????????????
        afterOrder.setPayTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        //?????????????????????
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
        //TODO ??????????????????????????????

        String orderCode = agreeUnPayOrderAppealForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //??????????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_APPEAL);
        }

        afterOrder.setStatus(OrderStatusEnum.ADD.getCode());
        //???????????????????????????
        afterOrder.setAgreeUnPayTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        //?????????????????????
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
        //TODO ??????????????????????????????
        String orderCode = releaseOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //???????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.PAY.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_PAY);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //??????????????????????????????????????????????????????
            if (!username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }

            //?????????????????????????????????
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.add(afterOrder.getAdvertiseCoinRate())));
            decFrozenBalanceReqDTO.setRemark("????????????????????????????????????????????????????????????(??????????????????????????????)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //??????????????????????????????
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            incBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(??????????????????)");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }

        } else {
            //????????????????????????????????????????????????????????????
            if (!username.equals(afterOrder.getOrderCustomerUsername())) {
                return new Result<>(ResultCodeEnum.FORBIDDEN);
            }

            //?????????????????????????????????
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            decFrozenBalanceReqDTO.setRemark("????????????????????????????????????????????????????????????(??????????????????)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //??????????????????????????????
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.subtract(afterOrder.getAdvertiseCoinRate())));
            incBalanceReqDTO.setRemark("?????????????????????????????????????????????????????????????????????????????????????????????");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);

            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }
        }

        //???????????????????????????
        IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
        incBalanceReqDTO.setUsername(AccountUsernameEnum.ADMIN_USER_NAME.getCode());
        incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
        incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(afterOrder.getAdvertiseCoinRate()));
        incBalanceReqDTO.setRemark("??????????????????????????????????????????(?????????)");
        Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
        if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
        }

        //??????????????????????????????
        DecAdvertiseFrozenAmountReqDTO decFrozenAmountReqDTO = new DecAdvertiseFrozenAmountReqDTO();
        decFrozenAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        decFrozenAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        decFrozenAmountReqDTO.setRemark("?????????????????????????????????????????????(??????????????????)");
        Result decAdvertiseFrozenAmountResult = advertiseService.decAdvertiseFrozenAmount(decFrozenAmountReqDTO);
        if (decAdvertiseFrozenAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(decAdvertiseFrozenAmountResult.getCode(), decAdvertiseFrozenAmountResult.getMsg());
        }

        afterOrder.setStatus(OrderStatusEnum.RELEASE.getCode());
        //??????????????????
        afterOrder.setReleaseTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        //?????????????????????
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
        //TODO ??????????????????????????????
        String orderCode = agreeUnReleaseOrderAppealForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //??????????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.APPEAL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_APPEAL);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {

            //?????????????????????????????????
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.add(afterOrder.getAdvertiseCoinRate())));
            decFrozenBalanceReqDTO.setRemark("????????????????????????????????????????????????????????????(??????????????????????????????)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //??????????????????????????????
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            incBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(??????????????????)");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }

        } else {

            //?????????????????????????????????
            DecFrozenBalanceForClientReqDTO decFrozenBalanceReqDTO = new DecFrozenBalanceForClientReqDTO();
            decFrozenBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            decFrozenBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            decFrozenBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            decFrozenBalanceReqDTO.setRemark("????????????????????????????????????????????????????????????(??????????????????)");
            Result decFrozenBalanceResult = accountService.decFrozenBalanceForClient(decFrozenBalanceReqDTO);

            if (decFrozenBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(decFrozenBalanceResult.getCode(), decFrozenBalanceResult.getMsg());
            }

            //??????????????????????????????
            IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
            incBalanceReqDTO.setUsername(afterOrder.getAdvertiseBusinessUsername());
            incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(BigDecimal.ONE.subtract(afterOrder.getAdvertiseCoinRate())));
            incBalanceReqDTO.setRemark("?????????????????????????????????????????????????????????????????????????????????????????????");
            Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);

            if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
            }
        }

        //???????????????????????????
        IncBalanceForClientReqDTO incBalanceReqDTO = new IncBalanceForClientReqDTO();
        incBalanceReqDTO.setUsername(AccountUsernameEnum.ADMIN_USER_NAME.getCode());
        incBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
        incBalanceReqDTO.setBalance(afterOrder.getOrderAmount().multiply(afterOrder.getAdvertiseCoinRate()));
        incBalanceReqDTO.setRemark("??????????????????????????????????????????(?????????)");
        Result incBalanceResult = accountService.incBalanceForClient(incBalanceReqDTO);
        if (incBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(incBalanceResult.getCode(), incBalanceResult.getMsg());
        }

        //??????????????????????????????
        DecAdvertiseFrozenAmountReqDTO decFrozenAmountReqDTO = new DecAdvertiseFrozenAmountReqDTO();
        decFrozenAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        decFrozenAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        decFrozenAmountReqDTO.setRemark("?????????????????????????????????????????????(??????????????????)");
        Result decAdvertiseFrozenAmountResult = advertiseService.decAdvertiseFrozenAmount(decFrozenAmountReqDTO);
        if (decAdvertiseFrozenAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(decAdvertiseFrozenAmountResult.getCode(), decAdvertiseFrozenAmountResult.getMsg());
        }

        afterOrder.setStatus(OrderStatusEnum.RELEASE.getCode());
        //??????????????????
        afterOrder.setAgreeUnReleaseTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        //?????????????????????
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
        //TODO ??????????????????????????????

        String orderCode = cancelOrderReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //???????????????
        boolean flag = false;

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //???????????????????????????
        if (afterOrder.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_CANCEL);
        }
        //????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        //?????????????????????????????????????????????????????????????????????????????????????????????????????????
        String username = currentLoginUser.getUsername();
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //??????????????????????????????????????????????????????????????????
            if (username.equals(afterOrder.getOrderCustomerUsername())) {
                flag = true;

            }
        } else {
            //???????????????????????????????????????????????????????????????
            if (username.equals(afterOrder.getAdvertiseBusinessUsername())) {
                flag = true;
            }
        }

        if (!flag) {
            //????????????????????????403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //??????????????????
        afterOrder.setCancelTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
        unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        unFreezeAmountReqDTO.setRemark("???????????????????????????????????????(??????????????????)");
        Result unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(??????????????????)");
            Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
            if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
            }
        }

        //?????????????????????
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
        //TODO ??????????????????????????????

        String orderCode = cancelOrderForAdminReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //???????????????????????????
        if (afterOrder.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_CANCEL);
        }
        //????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //??????????????????
        afterOrder.setCancelTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
        unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        unFreezeAmountReqDTO.setRemark("???????????????????????????????????????(??????????????????)");
        Result unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(??????????????????)");
            Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
            if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
            }
        }

        //?????????????????????
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
        //TODO ??????????????????????????????

        String orderCode = cancelOrderForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //???????????????????????????
        if (afterOrder.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_CANCEL);
        }
        //????????????????????????????????????
        if (afterOrder.getStatus() != OrderStatusEnum.ADD.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_STATUS_IS_NOT_ADD);
        }

        //???????????????????????????????????????????????????
        if (afterOrder.getAddTime().getTime() + maxPayTime > System.currentTimeMillis()) {
            return new Result<>(ResultCodeEnum.ORDER_PAY_TIME_LEGAL);
        }

        afterOrder.setStatus(OrderStatusEnum.CANCEL.getCode());
        //??????????????????
        afterOrder.setCancelTime(new Date());

        //?????????????????????
        orderService.update(afterOrder);

        UnFreezeAdvertiseAmountReqDTO unFreezeAmountReqDTO = new UnFreezeAdvertiseAmountReqDTO();
        unFreezeAmountReqDTO.setAdvertiseCode(afterOrder.getAdvertiseCode());
        unFreezeAmountReqDTO.setAdvertiseAmount(afterOrder.getOrderAmount());
        unFreezeAmountReqDTO.setRemark("???????????????????????????????????????(??????????????????)");
        Result unFreezeAmountResult = advertiseService.unFreezeAdvertiseAmount(unFreezeAmountReqDTO);

        if (unFreezeAmountResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeAmountResult.getCode(), unFreezeAmountResult.getMsg());
        }
        if (afterOrder.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
            unFreezeBalanceReqDTO.setUsername(afterOrder.getOrderCustomerUsername());
            unFreezeBalanceReqDTO.setCoinCode(afterOrder.getOrderCoinCode());
            unFreezeBalanceReqDTO.setBalance(afterOrder.getOrderAmount());
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(??????????????????)");
            Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
            if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
            }
        }

        //?????????????????????
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
        //TODO ??????????????????????????????

        String orderCode = updateOrderStatusForClientReqDTO.getOrderCode();
        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(orderCode);
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();
        Order afterOrder = ModelMapperUtil.map(orderRespDTO, Order.class);

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //?????????????????????10??????
        orderService.getJpaQueryFactory()
                .update(QOrder.order)
                .set(QOrder.order.status, updateOrderStatusForClientReqDTO.getStatus())
                .where(QOrder.order.orderCode.eq(
                        updateOrderStatusForClientReqDTO.getOrderCode()

                )).execute();

        //?????????????????????
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
        //TODO ??????????????????????????????
        //?????????????????????10??????
        orderService.getJpaQueryFactory()
                .delete(QOrder.order)
                .execute();
        return new Result<>(ResultCodeEnum.OK);
    }

    //TODO ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
    //TODO ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
}