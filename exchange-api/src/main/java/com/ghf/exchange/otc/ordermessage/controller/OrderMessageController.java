package com.ghf.exchange.otc.ordermessage.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.ordermessage.dto.AddOrderMessageReqDTO;
import com.ghf.exchange.otc.ordermessage.dto.GetOrderMessageByOrderMessageCodeReqDTO;
import com.ghf.exchange.otc.ordermessage.dto.OrderMessageRespDTO;
import com.ghf.exchange.otc.ordermessage.dto.PageOrderMessageReqDTO;
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

    @ApiOperation(value = "分页搜索消息", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/ordermessage/pageOrderMessage")
    @SneakyThrows
    public Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessage(@RequestBody PageOrderMessageReqDTO pageOrderMessageReqDTO) {
        return orderMessageService.pageOrderMessage(pageOrderMessageReqDTO);
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

    @MessageMapping("/message")
    public void message(OAuth2Authentication oAuth2Authentication, AddOrderMessageReqDTO addOrderMessageReqDTO) {
        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
        orderMessageService.addOrderMessage(addOrderMessageReqDTO);

    }
}
