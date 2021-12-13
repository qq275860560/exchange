package com.ghf.exchange.otc.order.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.order.dto.*;
import com.ghf.exchange.otc.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "订单接口", tags = {"订单接口"})
@RestController
@Lazy
@Slf4j
public class OrderController {

    @Lazy
    @Resource
    private OrderService orderService;

    @ApiOperation(value = "分页搜索订单", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/pageOrder")
    @SneakyThrows
    public Result<PageRespDTO<OrderRespDTO>> pageOrder(@RequestBody PageOrderReqDTO pageOrderReqDTO) {
        return orderService.pageOrder(pageOrderReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索订单", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/pageOrderForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<OrderRespDTO>> pageOrderForAdmin(@RequestBody PageOrderForAdminReqDTO pageOrderForAdminReqDTO) {
        return orderService.pageOrderForAdmin(pageOrderForAdminReqDTO);
    }

    @ApiOperation(value = "根据订单编号获取订单详情", notes = "<p>买家或卖家查询 otc 订单详情 \n 查询用户详细的付款信息 （未完成时） \n 订单交易完成买卖双方查看订单信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/getOrderByOrderCode")
    @SneakyThrows
    public Result<OrderRespDTO> getOrderByOrderCode(@RequestBody GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO) {
        return orderService.getOrderByOrderCode(getOrderByOrderCodeReqDTO);
    }

    @ApiOperation(value = "根据订单编号判断订单是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/existsOrderByOrderCode")
    @SneakyThrows
    public Result<Boolean> existsOrderByOrderCode(@RequestBody GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO) {
        return orderService.existsOrderByOrderCode(getOrderByOrderCodeReqDTO);
    }

    @ApiOperation(value = "微服务客户端判断是否存在下单状态的订单", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/existsUnPayOrderForClient")
    @SneakyThrows
    public Result<Boolean> existsUnPayOrderForClient(@RequestBody ExistsAddStatusOrderForClientReqDTO existsUnPayOrderForClientReqDTO) {
        return orderService.existsUnPayOrderForClient(existsUnPayOrderForClientReqDTO);
    }

    @ApiOperation(value = "新建订单", notes = "<p>用户下单</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/addOrder")
    @SneakyThrows
    public Result<Void> addOrder(@RequestBody AddOrderReqDTO addOrderReqDTO) {
        return orderService.addOrder(addOrderReqDTO);
    }

    @ApiOperation(value = "取消订单", notes = "<p>比如下单后买币方在未付款前可以取消</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/cancelOrder")
    @SneakyThrows
    public Result<Void> cancelOrder(@RequestBody CancelOrderReqDTO cancelOrderReqDTO) {
        return orderService.cancelOrder(cancelOrderReqDTO);
    }

    @ApiOperation(value = "管理员取消订单", notes = "<p>比如管理员在处理申诉时发现已付款订单实际并未付款则取消</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/cancelOrderForAdmin")
    @SneakyThrows
    public Result<Void> cancelOrderForAdmin(@RequestBody CancelOrderForAdminReqDTO cancelOrderForAdminReqDTO) {
        return orderService.cancelOrderForAdmin(cancelOrderForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端取消订单", notes = "<p>比如平台发现下单后超时未付款取消</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/cancelOrderForClient")
    @SneakyThrows
    public Result<Void> cancelOrderForClient(@RequestBody CancelOrderForClientReqDTO cancelOrderForClientReqDTO) {
        return orderService.cancelOrderForClient(cancelOrderForClientReqDTO);
    }





    @ApiOperation(value = "付款订单", notes = "<p>订单已付款</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/payOrder")
    @SneakyThrows
    public Result<Void> payOrder(@RequestBody PayOrderReqDTO payOrderReqDTO) {
        return orderService.payOrder(payOrderReqDTO);
    }

    @ApiOperation(value = "放行订单", notes = "<p>放币</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/releaseOrder")
    @SneakyThrows
    public Result<Void> releaseOrder(@RequestBody ReleaseOrderReqDTO releaseOrderReqDTO) {
        return orderService.releaseOrder(releaseOrderReqDTO);
    }



    @ApiOperation(value = "微服务客户端同意未付款订单申诉", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/agreeUnPayOrderAppealForClient")
    @SneakyThrows
    public Result<Void> agreeUnPayOrderAppealForClient(@RequestBody AgreeUnPayOrderAppealForClientReqDTO agreeUnPayOrderAppealForClientReqDTO) {
        return orderService.agreeUnPayOrderAppealForClient(agreeUnPayOrderAppealForClientReqDTO);
    }
    @ApiOperation(value = "微服务客户端同意未放行订单申诉", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/agreeUnReleaseOrderAppealForClient")
    @SneakyThrows
    public Result<Void> agreeUnReleaseOrderAppealForClient(@RequestBody AgreeUnReleaseOrderAppealForClientReqDTO agreeUnReleaseOrderAppealForClientReqDTO) {
        return orderService.agreeUnReleaseOrderAppealForClient(agreeUnReleaseOrderAppealForClientReqDTO);
    }


    @ApiOperation(value = "微服务客户端更新订单状态", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/updateOrderStatusForClient")
    @SneakyThrows
    public Result<Void> updateOrderStatusForClient(@RequestBody UpdateOrderStatusForClientReqDTO updateOrderStatusForClientReqDTO) {
        return orderService.updateOrderStatusForClient(updateOrderStatusForClientReqDTO);
    }

}
