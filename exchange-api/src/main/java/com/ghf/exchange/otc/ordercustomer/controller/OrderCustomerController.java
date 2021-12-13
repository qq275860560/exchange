package com.ghf.exchange.otc.ordercustomer.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.ordercustomer.dto.*;
import com.ghf.exchange.otc.ordercustomer.service.OrderCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "订单顾客接口", tags = {"订单顾客接口"})
@RestController
@Lazy
@Slf4j
public class OrderCustomerController {

    @Lazy
    @Resource
    private OrderCustomerService orderCustomerService;

    @ApiOperation(value = "分页搜索订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/pageOrderCustomer")
    @SneakyThrows
    public Result<PageRespDTO<OrderCustomerRespDTO>> pageOrderCustomer(@RequestBody PageOrderCustomerReqDTO pageOrderCustomerReqDTO) {
        return orderCustomerService.pageOrderCustomer(pageOrderCustomerReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/pageOrderCustomerForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<OrderCustomerRespDTO>> pageOrderCustomerForAdmin(@RequestBody PageOrderCustomerForAdminReqDTO pageOrderCustomerForAdminReqDTO) {
        return orderCustomerService.pageOrderCustomerForAdmin(pageOrderCustomerForAdminReqDTO);
    }

    @ApiOperation(value = "列出订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/listOrderCustomer")
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomer(@RequestBody ListOrderCustomerReqDTO listOrderCustomerReqDTO) {
        return orderCustomerService.listOrderCustomer(listOrderCustomerReqDTO);
    }

    @ApiOperation(value = "管理员列出订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/listOrderCustomerForAdmin")
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomerForAdmin(@RequestBody ListOrderCustomerForAdminReqDTO listOrderCustomerForAdminReqDTO) {
        return orderCustomerService.listOrderCustomerForAdmin(listOrderCustomerForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端列出订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/listOrderCustomerForClient")
    @SneakyThrows
    public Result<List<OrderCustomerRespDTO>> listOrderCustomerForClient(@RequestBody ListOrderCustomerForClientReqDTO listOrderCustomerForClientReqDTO) {
        return orderCustomerService.listOrderCustomerForClient(listOrderCustomerForClientReqDTO);
    }

    @ApiOperation(value = "根据订单顾客编号获取订单顾客详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/getOrderCustomerByOrderCustomerCode")
    @SneakyThrows
    public Result<OrderCustomerRespDTO> getOrderCustomerByOrderCustomerCode(@RequestBody GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        return orderCustomerService.getOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO);
    }

    @ApiOperation(value = "根据订单顾客编号判断订单顾客是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/existsOrderCustomerByOrderCustomerCode")
    @SneakyThrows
    public Result<Boolean> existsOrderCustomerByOrderCustomerCode(@RequestBody GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        return orderCustomerService.existsOrderCustomerByOrderCustomerCode(getOrderCustomerByOrderCustomerCodeReqDTO);
    }

    @ApiOperation(value = "微服务客户端新建订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/addOrderCustomerForClient")
    @SneakyThrows
    public Result<Void> addOrderCustomerForClient(@RequestBody AddOrderCustomerForClientReqDTO addOrderCustomerForClientReqDTO) {
        return orderCustomerService.addOrderCustomerForClient(addOrderCustomerForClientReqDTO);
    }

    @ApiOperation(value = "管理员更新订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/updateOrderCustomerByOrderCustomerForAdminCode")
    @SneakyThrows
    public Result<Void> updateOrderCustomerByOrderCustomerForAdminCode(@RequestBody UpdateOrderCustomerByOrderCustomerCodeForAdminReqDTO updateOrderCustomerByOrderCustomerCodeForAdminReqDTO) {
        return orderCustomerService.updateOrderCustomerByOrderCustomerForAdminCode(updateOrderCustomerByOrderCustomerCodeForAdminReqDTO);
    }

    @ApiOperation(value = "管理员启用订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/enableOrderCustomerForAdmin")
    @SneakyThrows
    public Result<Void> enableOrderCustomerForAdmin(@RequestBody GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        return orderCustomerService.enableOrderCustomerForAdmin(getOrderCustomerByOrderCustomerCodeReqDTO);
    }

    @ApiOperation(value = "管理员停用订单顾客", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/disableOrderCustomerForAdmin")
    @SneakyThrows
    public Result<Void> disableOrderCustomerForAdmin(@RequestBody GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO) {
        return orderCustomerService.disableOrderCustomerForAdmin(getOrderCustomerByOrderCustomerCodeReqDTO);
    }

    @ApiOperation(value = "客户端更新订单顾客（下单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/updateOrderCustomerOnAddOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnAddOrderEventForClient(@RequestBody UpdateOrderCustomerOnAddOrderEventForClientReqDTO updateOrderCustomerOnAddOrderEventForClientReqDTO) {
        return orderCustomerService.updateOrderCustomerOnAddOrderEventForClient(updateOrderCustomerOnAddOrderEventForClientReqDTO);
    }

    @ApiOperation(value = "客户端更新订单顾客（放行订单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/updateOrderCustomerOnReleaseOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnReleaseOrderEventForClient(@RequestBody UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO updateOrderCustomerOnReleaseOrderEventForClientReqDTO) {
        return orderCustomerService.updateOrderCustomerOnReleaseOrderEventForClient(updateOrderCustomerOnReleaseOrderEventForClientReqDTO);
    }

    @ApiOperation(value = "客户端更新订单顾客（申诉订单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/updateOrderCustomerOnAppealOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnAppealOrderEventForClient(@RequestBody UpdateOrderCustomerOnAppealOrderEventForClientReqDTO updateOrderCustomerOnAppealOrderEventForClientReqDTO) {
        return orderCustomerService.updateOrderCustomerOnAppealOrderEventForClient(updateOrderCustomerOnAppealOrderEventForClientReqDTO);
    }

    @ApiOperation(value = "客户端更新订单顾客（取消订单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderCustomer/updateOrderCustomerOnCancelOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateOrderCustomerOnCancelOrderEventForClient(@RequestBody UpdateOrderCustomerOnCancelOrderEventForClientReqDTO updateOrderCustomerOnCancelOrderEventForClientReqDTO) {
        return orderCustomerService.updateOrderCustomerOnCancelOrderEventForClient(updateOrderCustomerOnCancelOrderEventForClientReqDTO);
    }

}
