package com.ghf.exchange.otc.order.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.order.dto.*;
import com.ghf.exchange.otc.order.entity.Order;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrderService extends BaseService<Order, Long> {
    /**
     * 分页搜索订单
     *
     * @param pageOrderReqDTO
     * @return
     */
    Result<PageRespDTO<OrderRespDTO>> pageOrder(PageOrderReqDTO pageOrderReqDTO);

    /**
     * 根据订单编号获取订单详情
     *
     * @param getOrderByOrderCodeReqDTO
     * @return
     */
    Result<OrderRespDTO> getOrderByOrderCode(GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO);

    /**
     * 根据订单编号判断订单是否存在
     *
     * @param getOrderByOrderCodeReqDTO
     * @return
     */
    Result<Boolean> existsOrderByOrderCode(GetOrderByOrderCodeReqDTO getOrderByOrderCodeReqDTO);

    /**
     * 新建订单
     *
     * @param addOrderReqDTO
     * @return
     */
    Result<Void> addOrder(AddOrderReqDTO addOrderReqDTO);

    /**
     * 付款订单
     *
     * @param payOrderReqDTO
     * @return
     */
    Result<Void> payOrder(PayOrderReqDTO payOrderReqDTO);

    /**
     * 放行订单
     *
     * @param releaseOrderReqDTO
     * @return
     */
    Result<Void> releaseOrder(ReleaseOrderReqDTO releaseOrderReqDTO);

    /**
     * 取消订单,比如下单后买币方在未付款前可以取消，平台发现下单后超时未付款取消，管理员在处理申诉时发现已付款订单实际并未付款则取消
     *
     * @param cancelOrderReqDTO
     * @return
     */
    Result<Void> cancelOrder(CancelOrderReqDTO cancelOrderReqDTO);

    /**
     * 恢复订单,比如错误取消时，可以恢复为已下单状态
     *
     * @param recoverOrderReqDTO
     * @return
     */
    Result<Void> recoverOrder(RecoverOrderReqDTO recoverOrderReqDTO);

}