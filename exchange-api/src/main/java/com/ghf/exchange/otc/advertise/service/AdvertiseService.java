package com.ghf.exchange.otc.advertise.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.entity.Advertise;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface AdvertiseService extends BaseService<Advertise, Long> {
    /**
     * 分页搜索广告
     *
     * @param pageAdvertiseReqDTO
     * @return
     */
    Result<PageRespDTO<AdvertiseRespDTO>> pageAdvertise(PageAdvertiseReqDTO pageAdvertiseReqDTO);

    /**
     * 根据广告编号获取广告详情
     *
     * @param getAdvertiseByAdvertiseCodeReqDTO
     * @return
     */
    Result<AdvertiseRespDTO> getAdvertiseByAdvertiseCode(GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO);

    /**
     * 根据广告编号判断广告是否存在
     *
     * @param getAdvertiseByAdvertiseCodeReqDTO
     * @return
     */
    Result<Boolean> existsAdvertiseByAdvertiseCode(GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO);

    /**
     * 获取匹配广告详情
     *
     * @param getMatchAdvertiseReqDTO
     * @return
     */
    Result<AdvertiseRespDTO> getMatchAdvertise(GetMatchAdvertiseReqDTO getMatchAdvertiseReqDTO);

    /**
     * 发布广告
     *
     * @param addAdvertiseReqDTO
     * @return
     */
    Result<Void> addAdvertise(AddAdvertiseReqDTO addAdvertiseReqDTO);

    /**
     * 上架广告
     *
     * @param putOnShelvesReqDTO
     * @return
     */
    Result<Void> putOnShelves(PutOnShelvesReqDTO putOnShelvesReqDTO);

    /**
     * 下架广告
     *
     * @param putOffShelvesReqDTO
     * @return
     */
    Result<Void> putOffShelves(PutOffShelvesReqDTO putOffShelvesReqDTO);

    /**
     * 删除广告
     *
     * @param deleteAdvertiseReqDTO
     * @return
     */
    Result<Void> deleteAdvertise(DeleteAdvertiseReqDTO deleteAdvertiseReqDTO);

    /**
     * 冻结广告数量，比如：新建买币订单时
     *
     * @param freezeAdvertiseAmountReqDTO
     * @return
     */
    Result<Void> freezeAdvertiseAmount(FreezeAdvertiseAmountReqDTO freezeAdvertiseAmountReqDTO);

    /**
     * 解冻广告数量，比如：取消买币订单时
     *
     * @param unFreezeAdvertiseAmountReqDTO
     * @return
     */
    Result<Void> unFreezeAdvertiseAmount(UnFreezeAdvertiseAmountReqDTO unFreezeAdvertiseAmountReqDTO);

    /**
     * 扣减广告冻结数量，比如：放行订单时
     *
     * @param decAdvertiseFrozenAmountReqDTO
     * @return
     */
    Result<Void> decAdvertiseFrozenAmount(DecAdvertiseFrozenAmountReqDTO decAdvertiseFrozenAmountReqDTO);

}