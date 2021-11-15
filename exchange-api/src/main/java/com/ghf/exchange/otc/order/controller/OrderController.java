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

    @ApiOperation(value = "根据订单编号获取订单详情", notes = "<p></p>", httpMethod = "POST")
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

    @ApiOperation(value = "新建订单", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/addOrder")
    @SneakyThrows
    public Result<Void> addOrder(@RequestBody AddOrderReqDTO addOrderReqDTO) {
        return orderService.addOrder(addOrderReqDTO);
    }

    @ApiOperation(value = "付款订单", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/payOrder")
    @SneakyThrows
    public Result<Void> payOrder(@RequestBody PayOrderReqDTO payOrderReqDTO) {
        return orderService.payOrder(payOrderReqDTO);
    }

    @ApiOperation(value = "放行订单", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/releaseOrder")
    @SneakyThrows
    public Result<Void> releaseOrder(@RequestBody ReleaseOrderReqDTO releaseOrderReqDTO) {
        return orderService.releaseOrder(releaseOrderReqDTO);
    }

    @ApiOperation(value = "取消订单", notes = "<p>取消订单,比如下单后买币方在未付款前可以取消，平台发现下单后超时未付款取消，管理员在处理申诉时发现已付款订单实际并未付款则取消</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/cancelOrder")
    @SneakyThrows
    public Result<Void> cancelOrder(@RequestBody CancelOrderReqDTO cancelOrderReqDTO) {
        return orderService.cancelOrder(cancelOrderReqDTO);
    }

    @ApiOperation(value = "恢复订单", notes = "<p>恢复订单,比如错误取消时，可以恢复为已下单状态</p>", httpMethod = "POST")
    @PostMapping(value = "/api/order/recoverOrder")
    @SneakyThrows
    public Result<Void> recoverOrder(@RequestBody RecoverOrderReqDTO recoverOrderReqDTO) {
        return orderService.recoverOrder(recoverOrderReqDTO);
    }

}
