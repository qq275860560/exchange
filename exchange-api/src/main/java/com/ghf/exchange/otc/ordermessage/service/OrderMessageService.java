package com.ghf.exchange.otc.ordermessage.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.ordermessage.dto.*;
import com.ghf.exchange.otc.ordermessage.entity.OrderMessage;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrderMessageService extends BaseService<OrderMessage, Long> {

    /**
     * 分页搜索消息
     *
     * @param pageOrderMessageReqDTO
     * @return
     */
    Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessage(PageOrderMessageReqDTO pageOrderMessageReqDTO);

    /**
     * 管理员分页搜索消息
     *
     * @param pageOrderMessageForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<OrderMessageRespDTO>> pageOrderMessageForAdmin(PageOrderMessageForAdminReqDTO pageOrderMessageForAdminReqDTO);

    /**
     * 根据消息编号获取消息详情
     *
     * @param getOrderMessageByOrderMessageCodeReqDTO
     * @return
     */
    Result<OrderMessageRespDTO> getOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO);

    /**
     * 根据消息编号判断消息是否存在
     *
     * @param getOrderMessageByOrderMessageCodeReqDTO
     * @return
     */
    Result<Boolean> existsOrderMessageByOrderMessageCode(GetOrderMessageByOrderMessageCodeReqDTO getOrderMessageByOrderMessageCodeReqDTO);

    /**
     * 新建消息
     *
     * @param addOrderMessageReqDTO
     * @return
     */
    Result<Void> addOrderMessage(AddOrderMessageReqDTO addOrderMessageReqDTO);

}