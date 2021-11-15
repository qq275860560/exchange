package com.ghf.exchange.otc.orderlog.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.orderlog.dto.AddOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.dto.GetOrderLogByOrderLogCodeReqDTO;
import com.ghf.exchange.otc.orderlog.dto.OrderLogRespDTO;
import com.ghf.exchange.otc.orderlog.dto.PageOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.service.OrderLogService;
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
@Api(value = "订单日志接口", tags = {"订单日志接口"})
@RestController
@Lazy
@Slf4j
public class OrderLogController {

    @Lazy
    @Resource
    private OrderLogService orderLogService;

    @ApiOperation(value = "分页搜索订单日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderlog/pageOrderLog")
    @SneakyThrows
    public Result<PageRespDTO<OrderLogRespDTO>> pageOrderLog(@RequestBody PageOrderLogReqDTO pageOrderLogReqDTO) {
        return orderLogService.pageOrderLog(pageOrderLogReqDTO);
    }

    @ApiOperation(value = "根据订单日志编码获取订单日志详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderlog/getOrderLogByOrderLogCode")
    @SneakyThrows
    public Result<OrderLogRespDTO> getOrderLogByOrderLogCode(@RequestBody GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO) {
        return orderLogService.getOrderLogByOrderLogCode(getOrderLogByOrderLogCodeReqDTO);
    }

    @ApiOperation(value = "根据订单日志编码判断订单日志是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderlog/existsOrderLogByOrderLogCode")
    @SneakyThrows
    public Result<Boolean> existsOrderLogByOrderLogCode(@RequestBody GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO) {
        return orderLogService.existsOrderLogByOrderLogCode(getOrderLogByOrderLogCodeReqDTO);
    }

    @ApiOperation(value = "新建订单日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orderlog/addOrderLog")
    @SneakyThrows
    public Result<Void> addOrderLog(@RequestBody AddOrderLogReqDTO addOrderLogReqDTO) {
        return orderLogService.addOrderLog(addOrderLogReqDTO);
    }

}
