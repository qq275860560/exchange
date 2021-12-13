package com.ghf.exchange.otc.advertisebusiness.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertisebusiness.dto.*;
import com.ghf.exchange.otc.advertisebusiness.entity.AdvertiseBusiness;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface AdvertiseBusinessService extends BaseService<AdvertiseBusiness, Long> {

    /**
     * 分页搜索广告商家
     *
     * @param pageAdvertiseBusinessReqDTO
     * @return
     */
    Result<PageRespDTO<AdvertiseBusinessRespDTO>> pageAdvertiseBusiness(PageAdvertiseBusinessReqDTO pageAdvertiseBusinessReqDTO);

    /**
     * 管理员分页搜索广告商家
     *
     * @param pageAdvertiseBusinessForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<AdvertiseBusinessRespDTO>> pageAdvertiseBusinessForAdmin(PageAdvertiseBusinessForAdminReqDTO pageAdvertiseBusinessForAdminReqDTO);

    /**
     * 列出广告商家
     *
     * @param listAdvertiseBusinessReqDTO
     * @return
     */
    Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusiness(ListAdvertiseBusinessReqDTO listAdvertiseBusinessReqDTO);

    /**
     * 管理员列出广告商家
     *
     * @param listAdvertiseBusinessForAdminReqDTO
     * @return
     */
    Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusinessForAdmin(ListAdvertiseBusinessForAdminReqDTO listAdvertiseBusinessForAdminReqDTO);

    /**
     * 微服务客户端列出广告商家
     *
     * @param listAdvertiseBusinessForClientReqDTO
     * @return
     */
    Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusinessForClient(ListAdvertiseBusinessForClientReqDTO listAdvertiseBusinessForClientReqDTO);

    /**
     * 根据广告商家编号获取广告商家详情
     *
     * @param getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO
     * @return
     */
    Result<AdvertiseBusinessRespDTO> getAdvertiseBusinessByAdvertiseBusinessCode(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    /**
     * 根据广告商家编号判断广告商家是否存在
     *
     * @param getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO
     * @return
     */
    Result<Boolean> existsAdvertiseBusinessByAdvertiseBusinessCode(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    /**
     * 微服务客户端新建广告商家
     *
     * @param addAdvertiseBusinessForClientReqDTO
     * @return
     */
    Result<Void> addAdvertiseBusinessForClient(AddAdvertiseBusinessForClientReqDTO addAdvertiseBusinessForClientReqDTO);

    /**
     * 管理员更新广告商家
     *
     * @param updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO
     * @return
     */
    Result<Void> updateAdvertiseBusinessByAdvertiseBusinessForAdminCode(UpdateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO);

    /**
     * 管理员启用广告商家
     *
     * @param getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO
     * @return
     */
    Result<Void> enableAdvertiseBusinessForAdmin(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    /**
     * 管理员停用广告商家
     *
     * @param getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO
     * @return
     */
    Result<Void> disableAdvertiseBusinessForAdmin(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    /**
     * 客户端更新广告商家（上架广告时）
     *
     * @param getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO
     * @return
     */
    Result<Void> updateAdvertiseBusinessOnPutOnShelvesForClient(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    /**
     * 客户端更新广告商家（下架广告时）
     *
     * @param getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO
     * @return
     */
    Result<Void> updateAdvertiseBusinessOnPutOffShelvesForClient(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);

    /**
     * 客户端更新广告商家（下单时）
     *
     * @param updateAdvertiseBusinessOnAddOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateAdvertiseBusinessOnAddOrderEventForClient(UpdateAdvertiseBusinessOnAddOrderEventForClientReqDTO updateAdvertiseBusinessOnAddOrderEventForClientReqDTO);

    /**
     * 客户端更新广告商家（放行订单时）
     *
     * @param updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateAdvertiseBusinessOnReleaseOrderEventForClient(UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO);

    /**
     * 客户端更新广告商家（申诉订单时）
     *
     * @param updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO
     * @return
     */
    Result<Void> updateAdvertiseBusinessOnAppealOrderEventForClient(UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO);

}