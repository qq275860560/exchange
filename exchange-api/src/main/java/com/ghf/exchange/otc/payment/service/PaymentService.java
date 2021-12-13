package com.ghf.exchange.otc.payment.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.payment.dto.*;
import com.ghf.exchange.otc.payment.entity.Payment;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface PaymentService extends BaseService<Payment, Long> {
    /**
     * 分页搜索收款方式
     *
     * @param pagePaymentReqDTO
     * @return
     */
    Result<PageRespDTO<PaymentRespDTO>> pagePayment(PagePaymentReqDTO pagePaymentReqDTO);

    /**
     * 管理员分页搜索收款方式
     *
     * @param pagePaymentForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<PaymentRespDTO>> pagePaymentForAdmin(PagePaymentForAdminReqDTO pagePaymentForAdminReqDTO);

    /**
     * 列出收款方式
     *
     * @param listPaymentReqDTO
     * @return
     */
    Result<List<PaymentRespDTO>> listPayment(ListPaymentReqDTO listPaymentReqDTO);

    /**
     * 管理员列出收款方式
     *
     * @param listPaymentForAdminReqDTO
     * @return
     */
    Result<List<PaymentRespDTO>> listPaymentForAdmin(ListPaymentForAdminReqDTO listPaymentForAdminReqDTO);

    /**
     * 微服务客户端列出收款方式
     *
     * @param listPaymentForClientReqDTO
     * @return
     */
    Result<List<PaymentRespDTO>> listPaymentForClient(ListPaymentForClientReqDTO listPaymentForClientReqDTO);

    /**
     * 根据收款方式编码获取收款方式详情
     *
     * @param getPaymentByPaymentCodeReqDTO
     * @return
     */
    Result<PaymentRespDTO> getPaymentByPaymentCode(GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO);

    /**
     * 微服务客户端根据收款方式编码获取收款方式详情
     *
     * @param getPaymentByPaymentTypeForClientReqDTO
     * @return
     */
    Result<PaymentRespDTO> getPaymentByPaymentTypeForClient(GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO);

    /**
     * 根据收款方式编码判断收款方式是否存在
     *
     * @param paymentByPaymentCodeReqDTO
     * @return
     */
    Result<Boolean> existsPaymentByPaymentCode(GetPaymentByPaymentCodeReqDTO paymentByPaymentCodeReqDTO);

    /**
     * 微服务客户端根据收款方式类型判断收款方式是否存在
     *
     * @param getPaymentByPaymentTypeForClientReqDTO
     * @return
     */
    Result<Boolean> existsPaymentByPaymentTypeForClient(GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO);

    /**
     * 新建收款方式
     *
     * @param addPaymentReqDTO
     * @return
     */
    Result<?> addPayment(AddPaymentReqDTO addPaymentReqDTO);

    /**
     * 更新收款方式
     *
     * @param updatePaymentByPaymentCodeReqDTO
     * @return
     */
    Result<Void> updatePaymentByPaymentCode(UpdatePaymentByPaymentCodeReqDTO updatePaymentByPaymentCodeReqDTO);

    /**
     * 启用收款方式
     *
     * @param getPaymentByPaymentCodeReqDTO
     * @return
     */
    Result<Void> enablePayment(GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO);

    /**
     * 停用收款方式
     *
     * @param getPaymentByPaymentCodeReqDTO
     * @return
     */
    Result<Void> disablePayment(GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO);

}