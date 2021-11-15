package com.ghf.exchange.otc.advertiselog.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.dto.AdvertiseLogRespDTO;
import com.ghf.exchange.otc.advertiselog.dto.GetAdvertiseLogByAdvertiseLogCodeReqDTO;
import com.ghf.exchange.otc.advertiselog.dto.PageAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.entity.AdvertiseLog;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface AdvertiseLogService extends BaseService<AdvertiseLog, Long> {
    /**
     * 分页搜索广告日志
     *
     * @param pageAdvertiseLogReqDTO
     * @return
     */
    Result<PageRespDTO<AdvertiseLogRespDTO>> pageAdvertiseLog(PageAdvertiseLogReqDTO pageAdvertiseLogReqDTO);

    /**
     * 根据广告日志编码获取广告日志详情
     *
     * @param getAdvertiseLogByAdvertiseLogCodeReqDTO
     * @return
     */
    Result<AdvertiseLogRespDTO> getAdvertiseLogByAdvertiseLogCode(GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO);

    /**
     * 根据广告日志编码判断广告日志是否存在
     *
     * @param getAdvertiseLogByAdvertiseLogCodeReqDTO
     * @return
     */
    Result<Boolean> existsAdvertiseLogByAdvertiseLogCode(GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO);

    /**
     * 新建广告日志
     *
     * @param addAdvertiseLogReqDTO
     * @return
     */
    Result<Void> addAdvertiseLog(AddAdvertiseLogReqDTO addAdvertiseLogReqDTO);

}