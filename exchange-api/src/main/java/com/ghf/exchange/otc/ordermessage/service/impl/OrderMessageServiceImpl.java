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
import com.ghf.exchange.otc.ordermessage.entity.OrderMessageWithMongo;
import com.ghf.exchange.otc.ordermessage.enums.OrderMessageTypeEnum;
import com.ghf.exchange.otc.ordermessage.event.AddOrderMessageEvent;
import com.ghf.exchange.otc.ordermessage.repository.OrderMessageRepository;
import com.ghf.exchange.otc.ordermessage.service.OrderMessageService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrderMessageServiceImpl extends BaseServiceImpl<OrderMessage, Long> implements OrderMessageService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private OrderMessageService orderMessageService;

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

    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessage(PageOrderMessageReqDTO pageOrderMessageReqDTO) {

        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();


        /*mysql??????
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrderMessage.orderMessage.orderMessageReceiverUsername.eq(currentLoginUser.getUsername()).or(QOrderMessage.orderMessage.orderMessageSenderUsername.eq(currentLoginUser.getUsername())));

        if (!ObjectUtils.isEmpty(pageOrderMessageReqDTO.getOrderCode())) {
            predicate.and(QOrderMessage.orderMessage.orderCode.eq(pageOrderMessageReqDTO.getOrderCode()));
        }
        if (pageOrderMessageReqDTO.getBeginCreateTime() != null) {
            predicate.and(QOrderMessage.orderMessage.createTime.goe(pageOrderMessageReqDTO.getBeginCreateTime()));
        }
        if (pageOrderMessageReqDTO.getEndCreateTime() != null) {
            predicate.and(QOrderMessage.orderMessage.createTime.loe(pageOrderMessageReqDTO.getEndCreateTime()));
        }


        PageRespDTO<OrderMessageRespDTO> pageRespDTO = orderMessageService.page(predicate, pageOrderMessageReqDTO, OrderMessageRespDTO.class);
*/

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("order_message_sender_username").is(currentLoginUser.getUsername()), Criteria.where("order_message_receiver_username").is(currentLoginUser.getUsername())));
        if (!ObjectUtils.isEmpty(pageOrderMessageReqDTO.getOrderCode())) {
            query.addCriteria(Criteria.where("order_code").is(pageOrderMessageReqDTO.getOrderCode()));

        }

        if (pageOrderMessageReqDTO.getBeginCreateTime() != null && pageOrderMessageReqDTO.getEndCreateTime() != null) {
            query.addCriteria(Criteria.where("create_time").lte(pageOrderMessageReqDTO.getEndCreateTime()).andOperator(Criteria.where("create_time").gte(pageOrderMessageReqDTO.getBeginCreateTime())));
        } else if (pageOrderMessageReqDTO.getBeginCreateTime() == null && pageOrderMessageReqDTO.getEndCreateTime() != null) {
            query.addCriteria(Criteria.where("create_time").lte(pageOrderMessageReqDTO.getEndCreateTime()));
        } else if (pageOrderMessageReqDTO.getBeginCreateTime() != null && pageOrderMessageReqDTO.getEndCreateTime() == null) {
            query.addCriteria(Criteria.where("create_time").gte(pageOrderMessageReqDTO.getBeginCreateTime()));
        }

        long total = mongoTemplate.count(query, OrderMessageWithMongo.class);

        Sort sort = null;
        if (pageOrderMessageReqDTO.getSort() == null || pageOrderMessageReqDTO.getSort().isEmpty()) {
            sort = Sort.unsorted();
        } else {
            List<Sort.Order> orders = pageOrderMessageReqDTO.getSort().stream().map(e ->
                    new Sort.Order(Sort.Direction.fromString(e.getDirection()), e.getProperty())
            ).collect(Collectors.toList());
            sort = Sort.by(orders);
        }
        query.with(sort);

        query.skip((pageOrderMessageReqDTO.getPageNum() - 1) * pageOrderMessageReqDTO.getPageSize());
        query.limit(pageOrderMessageReqDTO.getPageSize());
        List<OrderMessageRespDTO> list = mongoTemplate.find(query, OrderMessageWithMongo.class).stream().map(e -> ModelMapperUtil.map(e, OrderMessageRespDTO.class)).collect(Collectors.toList());

        PageRespDTO<OrderMessageRespDTO> pageRespDTO = new PageRespDTO<OrderMessageRespDTO>(pageOrderMessageReqDTO.getPageNum(), pageOrderMessageReqDTO.getPageSize(), (int) total, list);

        //refactor page[1,max] ??????????????????????????????????????????????????????
        if (pageOrderMessageReqDTO.getPageNum() > pageRespDTO.getPages()) {
            pageOrderMessageReqDTO.setPageNum(pageRespDTO.getPages());
            query.skip((pageOrderMessageReqDTO.getPageNum() - 1) * pageOrderMessageReqDTO.getPageSize());
            list = mongoTemplate.find(query, OrderMessageWithMongo.class).stream().map(e -> ModelMapperUtil.map(e, OrderMessageRespDTO.class)).collect(Collectors.toList());
        }
        pageRespDTO = new PageRespDTO<OrderMessageRespDTO>(pageOrderMessageReqDTO.getPageNum(), pageOrderMessageReqDTO.getPageSize(), (int) total, list);

        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessageForAdmin(PageOrderMessageForAdminReqDTO pageOrderMessageForAdminReqDTO) {

        Query query = new Query();
        if (!ObjectUtils.isEmpty(pageOrderMessageForAdminReqDTO.getUsername())) {
            query.addCriteria(new Criteria().orOperator(Criteria.where("order_message_sender_username").is(pageOrderMessageForAdminReqDTO.getUsername()), Criteria.where("order_message_receiver_username").is(pageOrderMessageForAdminReqDTO.getUsername())));
        }
        if (!ObjectUtils.isEmpty(pageOrderMessageForAdminReqDTO.getOrderCode())) {
            query.addCriteria(Criteria.where("order_code").is(pageOrderMessageForAdminReqDTO.getOrderCode()));

        }

        if (pageOrderMessageForAdminReqDTO.getBeginCreateTime() != null && pageOrderMessageForAdminReqDTO.getEndCreateTime() != null) {
            query.addCriteria(Criteria.where("create_time").lte(pageOrderMessageForAdminReqDTO.getEndCreateTime()).andOperator(Criteria.where("create_time").gte(pageOrderMessageForAdminReqDTO.getBeginCreateTime())));
        } else if (pageOrderMessageForAdminReqDTO.getBeginCreateTime() == null && pageOrderMessageForAdminReqDTO.getEndCreateTime() != null) {
            query.addCriteria(Criteria.where("create_time").lte(pageOrderMessageForAdminReqDTO.getEndCreateTime()));
        } else if (pageOrderMessageForAdminReqDTO.getBeginCreateTime() != null && pageOrderMessageForAdminReqDTO.getEndCreateTime() == null) {
            query.addCriteria(Criteria.where("create_time").gte(pageOrderMessageForAdminReqDTO.getBeginCreateTime()));
        }

        long total = mongoTemplate.count(query, OrderMessageWithMongo.class);

        Sort sort = null;
        if (pageOrderMessageForAdminReqDTO.getSort() == null || pageOrderMessageForAdminReqDTO.getSort().isEmpty()) {
            sort = Sort.unsorted();
        } else {
            List<Sort.Order> orders = pageOrderMessageForAdminReqDTO.getSort().stream().map(e ->
                    new Sort.Order(Sort.Direction.fromString(e.getDirection()), e.getProperty())
            ).collect(Collectors.toList());
            sort = Sort.by(orders);
        }
        query.with(sort);

        query.skip((pageOrderMessageForAdminReqDTO.getPageNum() - 1) * pageOrderMessageForAdminReqDTO.getPageSize());
        query.limit(pageOrderMessageForAdminReqDTO.getPageSize());
        List<OrderMessageRespDTO> list = mongoTemplate.find(query, OrderMessageWithMongo.class).stream().map(e -> ModelMapperUtil.map(e, OrderMessageRespDTO.class)).collect(Collectors.toList());

        PageRespDTO<OrderMessageRespDTO> pageRespDTO = new PageRespDTO<OrderMessageRespDTO>(pageOrderMessageForAdminReqDTO.getPageNum(), pageOrderMessageForAdminReqDTO.getPageSize(), (int) total, list);

        //refactor page[1,max] ??????????????????????????????????????????????????????
        if (pageOrderMessageForAdminReqDTO.getPageNum() > pageRespDTO.getPages()) {
            pageOrderMessageForAdminReqDTO.setPageNum(pageRespDTO.getPages());
            query.skip((pageOrderMessageForAdminReqDTO.getPageNum() - 1) * pageOrderMessageForAdminReqDTO.getPageSize());
            list = mongoTemplate.find(query, OrderMessageWithMongo.class).stream().map(e -> ModelMapperUtil.map(e, OrderMessageRespDTO.class)).collect(Collectors.toList());
        }
        pageRespDTO = new PageRespDTO<OrderMessageRespDTO>(pageOrderMessageForAdminReqDTO.getPageNum(), pageOrderMessageForAdminReqDTO.getPageSize(), (int) total, list);

        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<OrderMessageRespDTO> getOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {

        String orderMessageCode = getOrderMessageByOrderMessageCodeReqDTO.getOrderMessageCode();

          /*mysql??????
          Predicate predicate = QOrderMessage.orderMessage.orderMessageCode.eq(orderMessageCode);
         OrderMessage orderMessage = orderMessageService.get(predicate);
         OrderMessageRespDTO orderMessageRespDTO = ModelMapperUtil.map(orderMessage, OrderMessageRespDTO.class);
*/
        Query query = Query.query(Criteria.where("order_message_code").is(orderMessageCode));
        OrderMessageWithMongo orderMessageWithMongo = mongoTemplate.find(query, OrderMessageWithMongo.class).get(0);
        OrderMessageRespDTO orderMessageRespDTO = ModelMapperUtil.map(orderMessageWithMongo, OrderMessageRespDTO.class);

        return new Result<>(orderMessageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {

        String orderMessageCode = getOrderMessageByOrderMessageCodeReqDTO.getOrderMessageCode();
       /*mysql??????
        Predicate predicate = QOrderMessage.orderMessage.orderMessageCode.eq(orderMessageCode);
        boolean b = orderMessageService.exists(predicate);
*/
        Query query = Query.query(Criteria.where("order_message_code").is(orderMessageCode));
        boolean b = mongoTemplate.exists(query, OrderMessageWithMongo.class);

        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Void> addOrderMessage(AddOrderMessageReqDTO addOrderMessageReqDTO) {
        OrderMessage orderMessage = ModelMapperUtil.map(addOrderMessageReqDTO, OrderMessage.class);

        //?????????id
        orderMessage.setId(IdUtil.generateLongId());
        //??????????????????
        if (!ObjectUtils.isEmpty(orderMessage.getOrderMessageCode())) {
            //???????????????
            String orderMessageCode = addOrderMessageReqDTO.getOrderMessageCode();
            GetOrderMessageByOrderMessageCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderMessageByOrderMessageCodeReqDTO();
            getAppealByAppealCodeReqDTO.setOrderMessageCode(orderMessageCode);
            boolean b = orderMessageService.existsOrderMessageByOrderMessageCode(getAppealByAppealCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ORDER_MESSAGE_EXISTS);
            }
            orderMessage.setOrderMessageCode(addOrderMessageReqDTO.getOrderMessageCode());
        } else {
            //????????????????????????
            orderMessage.setOrderMessageCode(orderMessage.getId() + "");
        }

        GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO = new GetOrderByOrderCodeReqDTO();
        getOrderByOrderCodeReqDTO.setOrderCode(addOrderMessageReqDTO.getOrderCode());
        OrderRespDTO orderRespDTO = orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO).getData();

        //???????????????
        boolean flag = false;
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        if (username.equals(orderRespDTO.getOrderCustomerUsername())) {
            //????????????,?????????
            flag = true;
            orderMessage.setOrderMessageReceiverUsername(orderRespDTO.getAdvertiseBusinessUsername());
        } else if (username.equals(orderRespDTO.getAdvertiseBusinessUsername())) {
            //????????????,?????????
            flag = true;
            orderMessage.setOrderMessageReceiverUsername(orderRespDTO.getOrderCustomerUsername());
        }
        if (!flag) {
            //????????????????????????403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        orderMessage.setOrderMessageSenderUsername(username);
        orderMessage.setOrderCode(orderRespDTO.getOrderCode());

        //????????????????????????
        if (addOrderMessageReqDTO.getOrderMessageType() != OrderMessageTypeEnum.TEXT.getCode() && addOrderMessageReqDTO.getOrderMessageType() != OrderMessageTypeEnum.PICTURE.getCode()) {
            return new Result<>(ResultCodeEnum.ORDER_AMESSAGE_TYPE_NOT_EXISTS);
        }
        //????????????????????????
        orderMessage.setOrderMessageType(addOrderMessageReqDTO.getOrderMessageType());

        //????????????????????????
        orderMessage.setOrderMessageContent(addOrderMessageReqDTO.getOrderMessageContent());

        //????????????????????????
        orderMessage.setCreateTime(new Date());

        //?????????????????????
          /*mysql??????
          orderMessageService.add(orderMessage);
           */
        OrderMessageWithMongo orderMessageWithMongo = ModelMapperUtil.map(orderMessage, OrderMessageWithMongo.class);
        mongoTemplate.save(orderMessageWithMongo);

        //?????????????????????
        AddOrderMessageEvent addOrderMessageEvent = ModelMapperUtil.map(orderMessageWithMongo, AddOrderMessageEvent.class);

        applicationEventPublisher.publishEvent(addOrderMessageEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    //TODO ?????????
    //TODO ????????????????????????????????????????????????,???????????????????????????????????????????????????????????????????????????,????????????????????????????????????????????????,????????????

}
