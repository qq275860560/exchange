package com.ghf.exchange.otc.orderappeal.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.orderappeal.dto.*;
import com.ghf.exchange.otc.orderappeal.entity.OrderAppeal;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrderAppealService extends BaseService<OrderAppeal, Long> {

    /**
     * 分页搜索申诉
     *
     * @param pageOrderAppealReqDTO
     * @return
     */
    Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppeal(PageOrderAppealReqDTO pageOrderAppealReqDTO);

    /**
     * 管理员分页搜索申诉
     *
     * @param pageOrderAppealForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<OrderAppealRespDTO>> pageOrderAppealForAdmin(PageOrderAppealForAdminReqDTO pageOrderAppealForAdminReqDTO);

    /**
     * 根据申诉编号获取申诉详情
     *
     * @param getOrderAppealByOrderAppealCodeReqDTO
     * @return
     */
    Result<OrderAppealRespDTO> getOrderAppealByOrderAppealCode(GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO);

    /**
     * 根据申诉编号判断申诉是否存在
     *
     * @param getOrderAppealByOrderAppealCodeReqDTO
     * @return
     */
    Result<Boolean> existsOrderAppealByOrderAppealCode(GetOrderAppealByOrderAppealCodeReqDTO getOrderAppealByOrderAppealCodeReqDTO);

    /**
     * 新建申诉
     *
     * @param addOrderAppealReqDTO
     * @return
     */
    Result<Void> addOrderAppeal(AddOrderAppealReqDTO addOrderAppealReqDTO);

    /**
     * 管理员审核申诉
     *
     * @param auditOrderAppealForAdminReqDTO
     * @return
     */
    Result<Void> auditOrderAppealForAdmin(AuditOrderAppealForAdminReqDTO auditOrderAppealForAdminReqDTO);

    /**
     * 取消申诉
     *
     * @param cancelOrderAppealReqDTO
     * @return
     */
    Result<Void> cancelOrderAppeal(CancelOrderAppealReqDTO cancelOrderAppealReqDTO);

}