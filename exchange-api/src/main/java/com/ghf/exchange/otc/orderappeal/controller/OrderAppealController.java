package com.ghf.exchange.otc.orderappeal.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.orderappeal.dto.*;
import com.ghf.exchange.otc.orderappeal.service.OrderAppealService;
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
@Api(value = "订单申诉接口", tags = {"订单申诉接口"})
@RestController
@Lazy
@Slf4j
public class OrderAppealController {

    @Lazy
    @Resource
    private OrderAppealService orderAppealService;

    @ApiOperation(value = "分页搜索申诉", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/pageOrderAppeal")
    @SneakyThrows
    public Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppeal(@RequestBody PageOrderAppealReqDTO pageOrderAppealReqDTO) {
        return orderAppealService.pageOrderAppeal(pageOrderAppealReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索申诉", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/pageOrderAppealForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppealForAdmin(@RequestBody PageOrderAppealForAdminReqDTO pageOrderAppealForAdminReqDTO) {
        return orderAppealService.pageOrderAppealForAdmin(pageOrderAppealForAdminReqDTO);
    }

    @ApiOperation(value = "根据申诉编号获取申诉详情", notes = "<p>买卖双方查看申诉信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/getOrderAppealByOrderAppealCode")
    @SneakyThrows
    public Result<OrderAppealRespDTO> getOrderAppealByOrderAppealCode(@RequestBody GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO) {
        return orderAppealService.getOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO);
    }

    @ApiOperation(value = "根据申诉编号判断申诉是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/existsOrderAppealByOrderAppealCode")
    @SneakyThrows
    public Result<Boolean> existsOrderAppealByOrderAppealCode(@RequestBody GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO) {
        return orderAppealService.existsOrderAppealByOrderAppealCode(getOrderAppealByOrderAppealCodeReqDTO);
    }

    @ApiOperation(value = "新建申诉", notes = "<p>提起申诉</p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/addOrderAppeal")
    @SneakyThrows
    public Result<Void> addOrderAppeal(@RequestBody AddOrderAppealReqDTO addOrderAppealReqDTO) {
        return orderAppealService.addOrderAppeal(addOrderAppealReqDTO);
    }

    @ApiOperation(value = "管理员审核申诉", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/auditOrderAppealForAdmin")
    @SneakyThrows
    public Result<Void> auditOrderAppealForAdmin(@RequestBody AuditOrderAppealForAdminReqDTO auditOrderAppealForAdminReqDTO) {
        return orderAppealService.auditOrderAppealForAdmin(auditOrderAppealForAdminReqDTO);
    }

    @ApiOperation(value = "取消申诉", notes = "<p>取消申诉</p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderappeal/cancelOrderAppeal")
    @SneakyThrows
    public Result<Void> cancelOrderAppeal(@RequestBody CancelOrderAppealReqDTO cancelOrderAppealReqDTO) {
        return orderAppealService.cancelOrderAppeal(cancelOrderAppealReqDTO);
    }

}
