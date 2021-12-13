package com.ghf.exchange.otc.advertiseamountlog.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertiseamountlog.dto.AddAdvertiseAmountLogForClientReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.AdvertiseAmountLogRespDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.PageAdvertiseAmountLogForAdminReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.entity.AdvertiseAmountLog;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface AdvertiseAmountLogService extends BaseService<AdvertiseAmountLog, Long> {
    /**
     * 管理员分页搜索广告库存数量日志
     *
     * @param pageAdvertiseAmountLogForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<AdvertiseAmountLogRespDTO>> pageAdvertiseAmountLogForAdmin(PageAdvertiseAmountLogForAdminReqDTO pageAdvertiseAmountLogForAdminReqDTO);

    /**
     * 根据广告库存数量日志编码获取广告库存数量日志详情
     *
     * @param getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO
     * @return
     */
    Result<AdvertiseAmountLogRespDTO> getAdvertiseAmountLogByAdvertiseAmountLogCode(GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO);

    /**
     * 根据广告库存数量日志编码判断广告库存数量日志是否存在
     *
     * @param getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO
     * @return
     */
    Result<Boolean> existsAdvertiseAmountLogByAdvertiseAmountLogCode(GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO);

    /**
     * 微服务客户端新建广告库存数量日志
     *
     * @param addAdvertiseAmountLogForClientReqDTO
     * @return
     */
    Result<Void> addAdvertiseAmountLogForClient(AddAdvertiseAmountLogForClientReqDTO addAdvertiseAmountLogForClientReqDTO);

}