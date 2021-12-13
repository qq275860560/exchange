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
    //TODO 可能要增加接单状态，未接单前，双方可免费取消，接单后未付款前取消则增加当天的取消次数

    /**
     * 分页搜索订单
     *
     * @param pageOrderReqDTO
     * @return
     */
    Result<PageRespDTO<OrderRespDTO>> pageOrder(PageOrderReqDTO pageOrderReqDTO);

    /**
     * 管理员分页搜索订单
     *
     * @param pageOrderForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<OrderRespDTO>> pageOrderForAdmin(PageOrderForAdminReqDTO pageOrderForAdminReqDTO);

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
     * 微服务客户端判断是否存在下单状态的订单
     *
     * @param existsUnPayOrderForClientReqDTO
     * @return
     */
    Result<Boolean> existsUnPayOrderForClient(ExistsAddStatusOrderForClientReqDTO existsUnPayOrderForClientReqDTO);

    /**
     * 新建订单
     *
     * @param addOrderReqDTO
     * @return
     */
    Result<Void> addOrder(AddOrderReqDTO addOrderReqDTO);

    /**
     * 取消订单,比如用户下单后，买币方在未付款前可以取消，买币方可以取消订单
     *
     * @param cancelOrderReqDTO
     * @return
     */
    Result<Void> cancelOrder(CancelOrderReqDTO cancelOrderReqDTO);

    /**
     * 管理员取消订单,比如用户下单后，买币方无法自行取消订单时，可联系管理员协助取消订单
     *
     * @param cancelOrderForAdminReqDTO
     * @return
     */
    Result<Void> cancelOrderForAdmin(CancelOrderForAdminReqDTO cancelOrderForAdminReqDTO);

    /**
     * 微服务客户端取消订单,比如用户下单后，买币方超时未付款，定时器或时间触发器可强行取消订单
     *
     * @param cancelOrderForClientReqDTO
     * @return
     */
    Result<Void> cancelOrderForClient(CancelOrderForClientReqDTO cancelOrderForClientReqDTO);

    //TODO 上传付款证据

    /**
     * 付款订单，此接口为买币方调用，但并不代表买币方一定付款了
     *
     * @param payOrderReqDTO
     * @return
     */
    Result<Void> payOrder(PayOrderReqDTO payOrderReqDTO);
    /**
     * 管理员付款订单,比如买币方付款后，买币无法点击付款按钮时，可联系管理员协助付款订单
     *
     * @param
     * @return
     */

    /**
     * 放行订单，此接口为卖币方调用
     *
     * @param releaseOrderReqDTO
     * @return
     */
    Result<Void> releaseOrder(ReleaseOrderReqDTO releaseOrderReqDTO);
    /**
     * 管理员放行订单,比如买币方付款后，卖币方无法点击放行按钮时，可联系管理员协助放行订单
     *
     * @param
     * @return
     */

    //
    //买币方点击付款后,卖币方发起未付款申诉，管理员审核不通过，订单模块监听到则回滚为已下单状态

    //买币方点击付款后,卖币方发起未付款申诉，管理员审核通过，订单模块监听到则回滚为已下单状态

    /**
     * 微服务客户端同意未付款订单申诉
     *
     * @param agreeUnPayOrderAppealForClientReqDTO
     * @return
     */
    Result<Void> agreeUnPayOrderAppealForClient(AgreeUnPayOrderAppealForClientReqDTO agreeUnPayOrderAppealForClientReqDTO);

    //买币方点击付款后,超过一定期限后买币方发起未放行申诉，管理员审核通过，订单模块监听到则放行订单

    /**
     * 微服务客户端同意未放行订单申诉
     *
     * @param agreeUnReleaseOrderAppealForClientReqDTO
     * @return
     */
    Result<Void> agreeUnReleaseOrderAppealForClient(AgreeUnReleaseOrderAppealForClientReqDTO agreeUnReleaseOrderAppealForClientReqDTO);

    //买币方点击付款后,超过一定期限后买币方发起未放行申诉，管理员审核不通过，订单模块监听到则回滚为已下单状态

    /**
     * 微服务客户端更新订单状态
     *
     * @param updateOrderStatusForClientReqDTO
     * @return
     */
    Result<Void> updateOrderStatusForClient(UpdateOrderStatusForClientReqDTO updateOrderStatusForClientReqDTO);

    /**
     * 管理员删除所有订单(测试环境使用)
     *
     * @param
     * @return
     */
    Result<Void> deleteAllOrderForAdmin();

}