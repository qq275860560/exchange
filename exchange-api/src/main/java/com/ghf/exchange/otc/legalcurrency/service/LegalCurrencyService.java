package com.ghf.exchange.otc.legalcurrency.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.legalcurrency.dto.*;
import com.ghf.exchange.otc.legalcurrency.entity.LegalCurrency;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface LegalCurrencyService extends BaseService<LegalCurrency, Long> {
    /**
     * 分页搜索法币
     *
     * @param pageLegalCurrencyReqDTO
     * @return
     */
    Result<PageRespDTO<LegalCurrencyRespDTO>> pageLegalCurrency(PageLegalCurrencyReqDTO pageLegalCurrencyReqDTO);

    /**
     * 管理员分页搜索法币
     *
     * @param pageLegalCurrencyForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<LegalCurrencyRespDTO>> pageLegalCurrencyForAdmin(PageLegalCurrencyForAdminReqDTO pageLegalCurrencyForAdminReqDTO);

    /**
     * 列出法币
     *
     * @param listLegalCurrencyReqDTO
     * @return
     */
    Result<List<LegalCurrencyRespDTO>> listLegalCurrency(ListLegalCurrencyReqDTO listLegalCurrencyReqDTO);

    /**
     * 管理员列出法币
     *
     * @param listLegalCurrencyForAdminReqDTO
     * @return
     */
    Result<List<LegalCurrencyRespDTO>> listLegalCurrencyForAdmin(ListLegalCurrencyForAdminReqDTO listLegalCurrencyForAdminReqDTO);

    /**
     * 微服务客户端列出法币
     *
     * @param listLegalCurrencyForClientReqDTO
     * @return
     */
    Result<List<LegalCurrencyRespDTO>> listLegalCurrencyForClient(ListLegalCurrencyForClientReqDTO listLegalCurrencyForClientReqDTO);

    /**
     * 根据法币所在国家编码获取法币详情
     *
     * @param getLegalCurrencyByLegalCurrencyCountryCodeReqDTO
     * @return
     */
    Result<LegalCurrencyRespDTO> getLegalCurrencyByLegalCurrencyCountryCode(GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO);

    /**
     * 根据法币编号获取法币详情
     *
     * @param getLegalCurrencyByLegalCurrencyCodeReqDTO
     * @return
     */
    Result<LegalCurrencyRespDTO> getLegalCurrencyByLegalCurrencyCode(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO);

    /**
     * 根据法币编号判断法币是否存在
     *
     * @param getLegalCurrencyByLegalCurrencyCodeReqDTO
     * @return
     */
    Result<Boolean> existsLegalCurrencyByLegalCurrencyCode(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO);

    /**
     * 管理员新建法币
     *
     * @param addLegalCurrencyForAdminReqDTO
     * @return
     */
    Result<Void> addLegalCurrencyForAdmin(AddLegalCurrencyForAdminReqDTO addLegalCurrencyForAdminReqDTO);

    /**
     * 管理员更新法币
     *
     * @param updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO
     * @return
     */
    Result<Void> updateLegalCurrencyByLegalCurrencyForAdminCode(UpdateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO);

    /**
     * 管理员启用法币
     *
     * @param getLegalCurrencyByLegalCurrencyCodeReqDTO
     * @return
     */
    Result<Void> enableLegalCurrencyForAdmin(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO);

    /**
     * 管理员停用法币
     *
     * @param getLegalCurrencyByLegalCurrencyCodeReqDTO
     * @return
     */
    Result<Void> disableLegalCurrencyForAdmin(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO);

}