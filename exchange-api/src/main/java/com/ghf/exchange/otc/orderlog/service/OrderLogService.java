package com.ghf.exchange.otc.orderlog.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.orderlog.dto.AddOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.dto.GetOrderLogByOrderLogCodeReqDTO;
import com.ghf.exchange.otc.orderlog.dto.OrderLogRespDTO;
import com.ghf.exchange.otc.orderlog.dto.PageOrderLogReqDTO;
import com.ghf.exchange.otc.orderlog.entity.OrderLog;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrderLogService extends BaseService<OrderLog, Long> {
    /**
     * 分页搜索订单日志
     *
     * @param pageOrderLogReqDTO
     * @return
     */
    Result<PageRespDTO<OrderLogRespDTO>> pageOrderLog(PageOrderLogReqDTO pageOrderLogReqDTO);

    /**
     * 根据订单日志编码获取订单日志详情
     *
     * @param getOrderLogByOrderLogCodeReqDTO
     * @return
     */
    Result<OrderLogRespDTO> getOrderLogByOrderLogCode(GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO);

    /**
     * 根据订单日志编码判断订单日志是否存在
     *
     * @param getOrderLogByOrderLogCodeReqDTO
     * @return
     */
    Result<Boolean> existsOrderLogByOrderLogCode(GetOrderLogByOrderLogCodeReqDTO getOrderLogByOrderLogCodeReqDTO);

    /**
     * 新建订单日志
     *
     * @param addOrderLogReqDTO
     * @return
     */
    Result<Void> addOrderLog(AddOrderLogReqDTO addOrderLogReqDTO);

}