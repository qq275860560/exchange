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
import com.ghf.exchange.otc.ordermessage.dto.AddOrderMessageReqDTO;
import com.ghf.exchange.otc.ordermessage.dto.GetOrderMessageByOrderMessageCodeReqDTO;
import com.ghf.exchange.otc.ordermessage.dto.OrderMessageRespDTO;
import com.ghf.exchange.otc.ordermessage.dto.PageOrderMessageReqDTO;
import com.ghf.exchange.otc.ordermessage.entity.OrderMessage;
import com.ghf.exchange.otc.ordermessage.entity.OrderMessageWithMongo;
import com.ghf.exchange.otc.ordermessage.event.AddOrderMessageEvent;
import com.ghf.exchange.otc.ordermessage.repository.OrderMessageRepository;
import com.ghf.exchange.otc.ordermessage.service.OrderMessageService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public OrderMessageServiceImpl(OrderMessageRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessage(PageOrderMessageReqDTO pageOrderMessageReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

  /*mysql方式
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrderMessage.orderMessage.orderMessageReceiverUsername.eq(username).or(QOrderMessage.orderMessage.orderMessageSenderUsername.eq(username)));

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
        query.addCriteria(new Criteria().orOperator(Criteria.where("order_message_sender_username").is(username), Criteria.where("order_message_receiver_username").is(username)));
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
        List<OrderMessageRespDTO> list = AutoMapUtils.mapForList(mongoTemplate.find(query, OrderMessageWithMongo.class), OrderMessageRespDTO.class);

        PageRespDTO<OrderMessageRespDTO> pageRespDTO = new PageRespDTO<OrderMessageRespDTO>(pageOrderMessageReqDTO.getPageNum(), pageOrderMessageReqDTO.getPageSize(), (int) total, list);

        //refactor page[1,max] 超过当前页边界，返回边界页的列表数据
        if (pageOrderMessageReqDTO.getPageNum() > pageRespDTO.getPages()) {
            pageOrderMessageReqDTO.setPageNum(pageRespDTO.getPages());
            query.skip((pageOrderMessageReqDTO.getPageNum() - 1) * pageOrderMessageReqDTO.getPageSize());
            list = AutoMapUtils.mapForList(mongoTemplate.find(query, OrderMessageWithMongo.class), OrderMessageRespDTO.class);
        }
        pageRespDTO = new PageRespDTO<OrderMessageRespDTO>(pageOrderMessageReqDTO.getPageNum(), pageOrderMessageReqDTO.getPageSize(), (int) total, list);

        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<OrderMessageRespDTO> getOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {

        String orderMessageCode = getOrderMessageByOrderMessageCodeReqDTO.getOrderMessageCode();

          /*mysql方式
          Predicate predicate = QOrderMessage.orderMessage.orderMessageCode.eq(orderMessageCode);
         OrderMessage orderMessage = orderMessageService.get(predicate);
         OrderMessageRespDTO orderMessageRespDTO = AutoMapUtils.map(orderMessage, OrderMessageRespDTO.class);
*/
        Query query = Query.query(Criteria.where("order_message_code").is(orderMessageCode));
        OrderMessageWithMongo orderMessageWithMongo = mongoTemplate.find(query, OrderMessageWithMongo.class).get(0);
        OrderMessageRespDTO orderMessageRespDTO = AutoMapUtils.map(orderMessageWithMongo, OrderMessageRespDTO.class);

        return new Result<>(orderMessageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {

        String orderMessageCode = getOrderMessageByOrderMessageCodeReqDTO.getOrderMessageCode();
       /*mysql方式
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
        OrderMessage orderMessage = AutoMapUtils.map(addOrderMessageReqDTO, OrderMessage.class);

        //初始化id
        orderMessage.setId(IdUtil.generateLongId());
        //判断消息编号
        if (!ObjectUtils.isEmpty(orderMessage.getOrderMessageCode())) {
            //判断唯一性
            String orderMessageCode = addOrderMessageReqDTO.getOrderMessageCode();
            GetOrderMessageByOrderMessageCodeReqDTO getAppealByAppealCodeReqDTO = new GetOrderMessageByOrderMessageCodeReqDTO();
            getAppealByAppealCodeReqDTO.setOrderMessageCode(orderMessageCode);
            boolean b = orderMessageService.existsOrderMessageByOrderMessageCode(getAppealByAppealCodeReqDTO).getData();
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

        if (username.equals(orderRespDTO.getOrderCustomerUsername())) {
            //订单顾客,有权限消息
            flag = true;
            orderMessage.setOrderMessageReceiverUsername(orderRespDTO.getAdvertiseBusinessUsername());
        } else if (username.equals(orderRespDTO.getAdvertiseBusinessUsername())) {
            //广告商家,有权限消息
            flag = true;
            orderMessage.setOrderMessageReceiverUsername(orderRespDTO.getOrderCustomerUsername());
        }
        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        orderMessage.setOrderMessageSenderUsername(username);
        orderMessage.setOrderCode(orderRespDTO.getOrderCode());

        orderMessage.setOrderMessageContent(addOrderMessageReqDTO.getOrderMessageContent());

        orderMessage.setCreateTime(new Date());

        //持久化到数据库
          /*mysql方式
          orderMessageService.add(orderMessage);
           */
        OrderMessageWithMongo orderMessageWithMongo = AutoMapUtils.map(orderMessage, OrderMessageWithMongo.class);
        mongoTemplate.save(orderMessageWithMongo);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new AddOrderMessageEvent(orderMessage));

        return new Result<>(ResultCodeEnum.OK);
    }

}