package com.ghf.exchange.otc.ordermessage.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.ordermessage.dto.*;
import com.ghf.exchange.otc.ordermessage.service.OrderMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "订单消息接口", tags = {"订单消息接口"})
@RestController
@Lazy
@Slf4j
public class OrderMessageController {

    @Lazy
    @Resource
    private OrderMessageService orderMessageService;

    @ApiOperation(value = "分页搜索消息", notes = "<p>买卖双方查看申诉信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/ordermessage/pageOrderMessage")
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessage(@RequestBody PageOrderMessageReqDTO pageOrderMessageReqDTO) {
        return orderMessageService.pageOrderMessage(pageOrderMessageReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索消息", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/ordermessage/pageOrderMessageForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessageForAdmin(@RequestBody PageOrderMessageForAdminReqDTO pageOrderMessageForAdminReqDTO) {
        return orderMessageService.pageOrderMessageForAdmin(pageOrderMessageForAdminReqDTO);
    }

    @ApiOperation(value = "根据消息编号获取消息详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/ordermessage/getOrderMessageByOrderMessageCode")
    @SneakyThrows
    public Result<OrderMessageRespDTO> getOrderMessageByOrderMessageCode(@RequestBody GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {
        return orderMessageService.getOrderMessageByOrderMessageCode(getOrderMessageByOrderMessageCodeReqDTO);
    }

    @ApiOperation(value = "根据消息编号判断消息是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/ordermessage/existsOrderMessageByOrderMessageCode")
    @SneakyThrows
    public Result<Boolean> existsOrderMessageByOrderMessageCode(@RequestBody GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO) {
        return orderMessageService.existsOrderMessageByOrderMessageCode(getOrderMessageByOrderMessageCodeReqDTO);
    }

    @ApiOperation(value = "websocket-stomp方式发送消息", notes = "<p>调用示例源码参考'STOMP实时聊天发送和接收示例.html',示例中包括:\n1.如何连接websocket-stomp\n2.连接成功后，如何拉取最新10条消息\n3.连接成功后，如何实时接收其他人发给自己的消息\n4.连接成功后，如何发送消息给其他人\n</p>", httpMethod = "POST")
    @MessageMapping("/message")
    public void message(OAuth2Authentication oAuth2Authentication, AddOrderMessageReqDTO addOrderMessageReqDTO) {
        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
        orderMessageService.addOrderMessage(addOrderMessageReqDTO);

    }
}
