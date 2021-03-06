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
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageOrderCustomerReqDTO.getOrderCustomerCode())) {
            predicate.and(QOrderCustomer.orderCustomer.orderCustomerCode.eq(pageOrderCustomerReqDTO.getOrderCustomerCode()));
        }

        if (!ObjectUtils.isEmpty(pageOrderCustomerReqDTO.getUsername())) {
            predicate.and(QOrderCustomer.orderCustomer.username.eq(pageOrderCustomerReqDTO.getUsername()));
        }

        PageRespDTO<OrderCustomerRespDTO> pageRespDTO = orderCustomerService.page(predicate, pageOrderCustomerReqDTO, OrderCustomerRespDTO.class);

        pageRespDTO.getList().forEach(orderCustomerRespDTO -> {
            if (orderCustomerRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayAppealCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayCancelCount(0);
            }
        });
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

        pageRespDTO.getList().forEach(orderCustomerRespDTO -> {
            if (orderCustomerRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayAppealCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayCancelCount(0);
            }
        });
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomer(ListOrderCustomerReqDTO listOrderCustomerReqDTO) {

        //??????????????????????????????
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

        list.forEach(orderCustomerRespDTO -> {
            if (orderCustomerRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayAppealCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayCancelCount(0);
            }
        });
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

        list.forEach(orderCustomerRespDTO -> {
            if (orderCustomerRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayAppealCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayCancelCount(0);
            }
        });
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

        list.forEach(orderCustomerRespDTO -> {
            if (orderCustomerRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayAppealCount(0);
            }

            if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                orderCustomerRespDTO.setOrderTodayCancelCount(0);
            }
        });
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<OrderCustomerRespDTO> getOrderCustomerByOrderCustomerCode(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {

        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        Predicate predicate = QOrderCustomer.orderCustomer.orderCustomerCode.eq(orderCustomerCode);
        OrderCustomer orderCustomer = orderCustomerService.get(predicate);

        //??????
        OrderCustomerRespDTO orderCustomerRespDTO = ModelMapperUtil.map(orderCustomer, OrderCustomerRespDTO.class);

        if (orderCustomerRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
            //?????????????????????????????????0
            orderCustomerRespDTO.setOrderMonthBuySellCount(0);
        }

        if (orderCustomerRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(orderCustomerRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
            //?????????????????????????????????0
            orderCustomerRespDTO.setOrderMonthBuySellReleaseCount(0);
        }

        if (orderCustomerRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
            //?????????????????????????????????0
            orderCustomerRespDTO.setOrderTodayAppealCount(0);
        }

        if (orderCustomerRespDTO.getOrderLastCancelTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(orderCustomerRespDTO.getOrderLastCancelTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
            //?????????????????????????????????0
            orderCustomerRespDTO.setOrderTodayCancelCount(0);
        }
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

        //?????????id
        orderCustomer.setId(IdUtil.generateLongId());
        //????????????
        if (!ObjectUtils.isEmpty(addOrderCustomerForAdminReqDTO.getOrderCustomerCode())) {
            //???????????????
            String orderCustomerCode = addOrderCustomerForAdminReqDTO.getOrderCustomerCode();
            GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
            getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
            boolean b = orderCustomerService.existsOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_EXISTS);
            }
            orderCustomer.setOrderCustomerCode(addOrderCustomerForAdminReqDTO.getOrderCustomerCode());
        } else {
            //??????????????????
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

        //?????????????????????????????????
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(addOrderCustomerForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        orderCustomer.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        orderCustomer.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        orderCustomer.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        orderCustomer.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        orderCustomer.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        orderCustomer.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());

        //????????????
        orderCustomer.setStatus(OrderCustomerStatusEnum.ENABLE.getCode());

        orderCustomer.setCreateTime(new Date());

        //?????????????????????
        orderCustomerService.add(orderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerByOrderCustomerForAdminCode(UpdateOrderCustomerByOrderCustomerCodeForAdminReqDTO updateOrderCustomerByOrderCustomerCodeForAdminReqDTO) {
        String orderCustomerCode = updateOrderCustomerByOrderCustomerCodeForAdminReqDTO.getOrderCustomerCode();
        //??????
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }
        //????????????????????????
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

        //?????????????????????????????????
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(updateOrderCustomerByOrderCustomerCodeForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        afterOrderCustomer.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterOrderCustomer.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        afterOrderCustomer.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterOrderCustomer.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        afterOrderCustomer.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        afterOrderCustomer.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());

        //????????????
        afterOrderCustomer.setStatus(OrderCustomerStatusEnum.ENABLE.getCode());
        //??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> enableOrderCustomerForAdmin(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        //??????
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //???????????????????????????
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_ENABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //????????????
        afterOrderCustomer.setStatus(OrderCustomerStatusEnum.ENABLE.getCode());

        //??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> disableOrderCustomerForAdmin(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        String orderCustomerCode = getOrderCustomerByOrderCustomerCodeReqDTO.getOrderCustomerCode();
        //??????
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //???????????????????????????
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //????????????
        afterOrderCustomer.setStatus(OrderCustomerStatusEnum.DISABLE.getCode());

        //??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnAddOrderEventForClient(UpdateOrderCustomerOnAddOrderEventForClientReqDTO updateOrderCustomerOnAddOrderEventForClientReqDTO) {
        String orderCustomerCode = updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderCustomerCode();
        //??????
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //?????????
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //??????????????????????????????
        afterOrderCustomer.setOrderBuySellCount(orderCustomerRespDTO.getOrderBuySellCount() + 1);
        if (updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //????????????????????????
            afterOrderCustomer.setOrderBuyCount(orderCustomerRespDTO.getOrderBuyCount() + 1);
        } else if (updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //????????????????????????
            afterOrderCustomer.setOrderSellCount(orderCustomerRespDTO.getOrderSellCount() + 1);
        }
        //?????????????????????=????????????????????????/????????????????????????????????????
        if (updateOrderCustomerOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterOrderCustomer.setOrderBuyReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount()).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyCount() + 1), 8, BigDecimal.ROUND_UP));
        }
        //??????????????????=(????????????????????????+????????????????????????)/????????????????????????
        afterOrderCustomer.setOrderBuySellReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount() + orderCustomerRespDTO.getOrderSellReleaseCount()).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuySellCount() + 1), 8, BigDecimal.ROUND_UP));

        //??????????????????????????????
        afterOrderCustomer.setOrderMonthBuySellCount(orderCustomerRespDTO.getOrderMonthBuySellCount() + 1);

        //??????????????????????????????
        afterOrderCustomer.setOrderLastAddTime(updateOrderCustomerOnAddOrderEventForClientReqDTO.getAddTime());

//??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnReleaseOrderEventForClient(UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO updateOrderCustomerOnReleaseOrderEventForClientReqDTO) {
        String orderCustomerCode = updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderCustomerCode();
        //??????
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //?????????
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //??????????????????????????????
            afterOrderCustomer.setOrderBuyReleaseCount(orderCustomerRespDTO.getOrderBuyReleaseCount() + 1);
        } else if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //??????????????????????????????
            afterOrderCustomer.setOrderSellReleaseCount(orderCustomerRespDTO.getOrderSellReleaseCount() + 1);
        }

        //?????????????????????=????????????????????????/????????????????????????????????????
        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterOrderCustomer.setOrderBuyReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount() + 1).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyCount()), 8, BigDecimal.ROUND_UP));
        }
        //??????????????????=(????????????????????????+????????????????????????)/????????????????????????
        afterOrderCustomer.setOrderBuySellReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuyReleaseCount() + orderCustomerRespDTO.getOrderSellReleaseCount() + 1).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderBuySellCount()), 8, BigDecimal.ROUND_UP));

        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //????????????????????????????????????
            afterOrderCustomer.setOrderBuyTotalReleaseTime(orderCustomerRespDTO.getOrderBuyTotalReleaseTime() + updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        } else if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //????????????????????????????????????
            afterOrderCustomer.setOrderSellTotalReleaseTime(orderCustomerRespDTO.getOrderSellTotalReleaseTime() + updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        }

        //??????????????????????????????
        afterOrderCustomer.setOrderBuySellAvgReleaseTime((orderCustomerRespDTO.getOrderBuyTotalReleaseTime() + orderCustomerRespDTO.getOrderSellTotalReleaseTime() + updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getPayTime().getTime()) / orderCustomerRespDTO.getOrderBuySellCount());

        //????????????????????????????????????
        if (new SimpleDateFormat("yyyy-MM").format(updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime()))) {

            //??????????????????????????????
            afterOrderCustomer.setOrderMonthBuySellReleaseCount(orderCustomerRespDTO.getOrderMonthBuySellReleaseCount() + 1);
            //??????????????????????????????
            afterOrderCustomer.setOrderMonthBuySellReleaseRate(BigDecimal.valueOf(orderCustomerRespDTO.getOrderMonthBuySellReleaseCount() + 1).divide(BigDecimal.valueOf(orderCustomerRespDTO.getOrderMonthBuySellCount()), 8, BigDecimal.ROUND_UP));

            //??????????????????????????????
            afterOrderCustomer.setOrderLastReleaseTime(updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getReleaseTime());

        }

        if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //????????????????????????
            afterOrderCustomer.setOrderBuyTotalPrice(orderCustomerRespDTO.getOrderBuyTotalPrice().add(updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderTotalPrice()));
        } else if (updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //????????????????????????
            afterOrderCustomer.setOrderSellTotalPrice(orderCustomerRespDTO.getOrderSellTotalPrice().add(updateOrderCustomerOnReleaseOrderEventForClientReqDTO.getOrderTotalPrice()));
        }

//??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnAppealOrderEventForClient(UpdateOrderCustomerOnAppealOrderEventForClientReqDTO updateOrderCustomerOnAppealOrderEventForClientReqDTO) {

        String orderCustomerCode = updateOrderCustomerOnAppealOrderEventForClientReqDTO.getOrderCustomerCode();
        //??????
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //?????????
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //??????????????????????????????
        afterOrderCustomer.setOrderTodayAppealCount(orderCustomerRespDTO.getOrderTodayAppealCount() + 1);

        //??????????????????????????????
        afterOrderCustomer.setOrderLastAppealTime(updateOrderCustomerOnAppealOrderEventForClientReqDTO.getAppealTime());
        //??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnCancelOrderEventForClient(UpdateOrderCustomerOnCancelOrderEventForClientReqDTO updateOrderCustomerOnCancelOrderEventForClientReqDTO) {

        String orderCustomerCode = updateOrderCustomerOnCancelOrderEventForClientReqDTO.getOrderCustomerCode();
        //??????
        GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO = new GetOrderCustomerByOrderCustomerCodeReqDTO();
        getOrderCustomerByOrderCustomerCodeReqDTO.setOrderCustomerCode(orderCustomerCode);
        OrderCustomerRespDTO orderCustomerRespDTO = orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO).getData();
        if (orderCustomerRespDTO == null) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_NOT_EXISTS);
        }

        //?????????
        if (orderCustomerRespDTO.getStatus() == OrderCustomerStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_CUSTOMER_STATUS_DISABLE);
        }

        OrderCustomer afterOrderCustomer = ModelMapperUtil.map(orderCustomerRespDTO, OrderCustomer.class);

        //??????????????????????????????
        afterOrderCustomer.setOrderTodayCancelCount(orderCustomerRespDTO.getOrderTodayCancelCount() + 1);

        //??????????????????????????????
        afterOrderCustomer.setOrderLastCancelTime(updateOrderCustomerOnCancelOrderEventForClientReqDTO.getCancelTime());
        //??????????????????
        orderCustomerService.update(afterOrderCustomer);

        return new Result<>(ResultCodeEnum.OK);
    }

}