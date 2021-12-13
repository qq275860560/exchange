package com.ghf.exchange.otc.payment.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.payment.dto.*;
import com.ghf.exchange.otc.payment.service.PaymentService;
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
@Api(value = "收款方式接口", tags = {"收款方式接口"})
@RestController
@Lazy
@Slf4j
public class PaymentController {

    @Lazy
    @Resource
    private PaymentService paymentService;

    @ApiOperation(value = "分页搜索收款方式", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/pagePayment")
    @SneakyThrows
    public Result<PageRespDTO<PaymentRespDTO>> pagePayment(@RequestBody PagePaymentReqDTO pagePaymentReqDTO) {
        return paymentService.pagePayment(pagePaymentReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索收款方式", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/pagePaymentForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<PaymentRespDTO>> pagePaymentForAdmin(@RequestBody PagePaymentForAdminReqDTO pagePaymentForAdminReqDTO) {
        return paymentService.pagePaymentForAdmin(pagePaymentForAdminReqDTO);
    }

    @ApiOperation(value = "列出收款方式", notes = "<p>获取 银行列表 信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/listPayment")
    @SneakyThrows
    public Result<List<PaymentRespDTO>> listPayment(@RequestBody ListPaymentReqDTO listPaymentReqDTO) {
        return paymentService.listPayment(listPaymentReqDTO);
    }

    @ApiOperation(value = "管理员列出收款方式", notes = "<p>获取 银行列表 信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/listPaymentForAdmin")
    @SneakyThrows
    public Result<List<PaymentRespDTO>> listPaymentForAdmin(@RequestBody ListPaymentForAdminReqDTO listPaymentForAdminReqDTO) {
        return paymentService.listPaymentForAdmin(listPaymentForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端列出收款方式", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/listPaymentForClient")
    @SneakyThrows
    public Result<List<PaymentRespDTO>> listPaymentForClient(@RequestBody ListPaymentForClientReqDTO listPaymentForClientReqDTO) {
        return paymentService.listPaymentForClient(listPaymentForClientReqDTO);
    }

    @ApiOperation(value = "根据收款方式名称获取收款方式详情", notes = "<p>获取用户支付信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/getPaymentByPaymentCode")
    @SneakyThrows
    public Result<PaymentRespDTO> getPaymentByPaymentCode(@RequestBody GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {
        return paymentService.getPaymentByPaymentCode(getPaymentByPaymentCodeReqDTO);
    }


    @ApiOperation(value = "微服务客户端根据收款方式编码获取收款方式详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/getPaymentByPaymentTypeForClient")
    @SneakyThrows
    public Result<PaymentRespDTO> getPaymentByPaymentTypeForClient(@RequestBody GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO) {
        return paymentService.getPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO);
    }



    @ApiOperation(value = "根据收款方式名称判断收款方式是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/existsPaymentByPaymentCode")
    @SneakyThrows
    public Result<Boolean> existsPaymentByPaymentCode(@RequestBody GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {
        return paymentService.existsPaymentByPaymentCode(getPaymentByPaymentCodeReqDTO);
    }



    @ApiOperation(value = "微服务客户端根据收款方式类型判断收款方式是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/existsPaymentByPaymentTypeForClient")
    @SneakyThrows
    public Result<Boolean> existsPaymentByPaymentTypeForClient(@RequestBody GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO) {
        return paymentService.existsPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO);
    }




    @ApiOperation(value = "新建收款方式", notes = "<p>新增 网络账号 \n 新增用户 银行卡</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/addPayment")
    @SneakyThrows
    public Result<?> addPayment(@RequestBody AddPaymentReqDTO addPaymentReqDTO) {
        return paymentService.addPayment(addPaymentReqDTO);
    }

    @ApiOperation(value = "更新收款方式", notes = "<p>修改 网络账号 信息 \n 修改用户 银行卡 信息</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/updatePaymentByPaymentCode")
    @SneakyThrows
    public Result<Void> updatePaymentByPaymentCode(@RequestBody UpdatePaymentByPaymentCodeReqDTO updatePaymentByPaymentCodeReqDTO) {
        return paymentService.updatePaymentByPaymentCode(updatePaymentByPaymentCodeReqDTO);
    }

    @ApiOperation(value = "启用收款方式", notes = "<p>修改 网络账号 状态 \n 修改用户 银行卡 状态</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/enablePayment")
    @SneakyThrows
    public Result<Void> enablePayment(@RequestBody GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {
        return paymentService.enablePayment(getPaymentByPaymentCodeReqDTO);
    }

    @ApiOperation(value = "停用收款方式", notes = "<p>修改 网络账号 状态 \n 修改用户 银行卡 状态</p>", httpMethod = "POST")
    @PostMapping(value = "/api/payment/disablePayment")
    @SneakyThrows
    public Result<Void> disablePayment(@RequestBody GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {
        return paymentService.disablePayment(getPaymentByPaymentCodeReqDTO);
    }

}
