package com.ghf.exchange.otc.ordercustomer.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.ordercustomer.dto.*;
import com.ghf.exchange.otc.ordercustomer.entity.OrderCustomer;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrderCustomerService extends BaseService<OrderCustomer, Long> {

    /**
     * 分页搜索订单顾客
     *
     * @param pageOrderCustomerReqDTO
     * @return
     */
    Result<PageRespDTO<OrderCustomerRespDTO>> pageOrderCustomer(PageOrderCustomerReqDTO pageOrderCustomerReqDTO);

    /**
     * 管理员分页搜索订单顾客
     *
     * @param pageOrderCustomerForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<OrderCustomerRespDTO>> pageOrderCustomerForAdmin(PageOrderCustomerForAdminReqDTO pageOrderCustomerForAdminReqDTO);

    /**
     * 列出订单顾客
     *
     * @param listOrderCustomerReqDTO
     * @return
     */
    Result<List<OrderCustomerRespDTO>> listOrderCustomer(ListOrderCustomerReqDTO listOrderCustomerReqDTO);

    /**
     * 管理员列出订单顾客
     *
     * @param listOrderCustomerForAdminReqDTO
     * @return
     */
    Result<List<OrderCustomerRespDTO>> listOrderCustomerForAdmin(ListOrderCustomerForAdminReqDTO listOrderCustomerForAdminReqDTO);

    /**
     * 微服务客户端列出订单顾客
     *
     * @param listOrderCustomerForClientReqDTO
     * @return
     */
    Result<List<OrderCustomerRespDTO>> listOrderCustomerForClient(ListOrderCustomerForClientReqDTO listOrderCustomerForClientReqDTO);

    /**
     * 根据订单顾客编号获取订单顾客详情
     *
     * @param getOrderCustomerByOrderCustomerCodeReqDTO
     * @return
     */
    Result<OrderCustomerRespDTO> getOrderCustomerByOrderCustomerCode(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO);

    /**
     * 根据订单顾客编号判断订单顾客是否存在
     *
     * @param getOrderCustomerByOrderCustomerCodeReqDTO
     * @return
     */
    Result<Boolean> existsOrderCustomerByOrderCustomerCode(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO);

    /**
     * 微服务客户端新建订单顾客
     *
     * @param addOrderCustomerForClientReqDTO
     * @return
     */
    Result<Void> addOrderCustomerForClient(AddOrderCustomerForClientReqDTO addOrderCustomerForClientReqDTO);

    /**
     * 管理员更新订单顾客
     *
     * @param updateOrderCustomerByOrderCustomerCodeForAdminReqDTO
     * @return
     */
    Result<Void> updateOrderCustomerByOrderCustomerForAdminCode(UpdateOrderCustomerByOrderCustomerCodeForAdminReqDTO updateOrderCustomerByOrderCustomerCodeForAdminReqDTO);

    /**
     * 管理员启用订单顾客
     *
     * @param getOrderCustomerByOrderCustomerCodeReqDTO
     * @return
     */
    Result<Void> enableOrderCustomerForAdmin(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO);

    /**
     * 管理员停用订单顾客
     *
     * @param getOrderCustomerByOrderCustomerCodeReqDTO
     * @return
     */
    Result<Void> disableOrderCustomerForAdmin(GetOrderCustomerByOrderCustomerCodeReqDTO getOrderCustomerByOrderCustomerCodeReqDTO);

    /**
     * 客户端更新订单顾客（下单时）
     *
     * @param updateOrderCustomerOnAddOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateOrderCustomerOnAddOrderEventForClient(UpdateOrderCustomerOnAddOrderEventForClientReqDTO updateOrderCustomerOnAddOrderEventForClientReqDTO);

    /**
     * 客户端更新订单顾客（放行订单时）
     *
     * @param updateOrderCustomerOnReleaseOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateOrderCustomerOnReleaseOrderEventForClient(UpdateOrderCustomerOnReleaseOrderEventForClientReqDTO updateOrderCustomerOnReleaseOrderEventForClientReqDTO);

    /**
     * 客户端更新订单顾客（申诉订单时）
     *
     * @param updateOrderCustomerOnAppealOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateOrderCustomerOnAppealOrderEventForClient(UpdateOrderCustomerOnAppealOrderEventForClientReqDTO updateOrderCustomerOnAppealOrderEventForClientReqDTO);

    /**
     * 客户端更新订单顾客（取消订单时）
     *
     * @param updateOrderCustomerOnCancelOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateOrderCustomerOnCancelOrderEventForClient(UpdateOrderCustomerOnCancelOrderEventForClientReqDTO updateOrderCustomerOnCancelOrderEventForClientReqDTO);

}