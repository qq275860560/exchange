package com.ghf.exchange.otc.country.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.country.dto.*;
import com.ghf.exchange.otc.country.entity.Country;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface CountryService extends BaseService<Country, Long> {
    /**
     * 分页搜索国家
     *
     * @param pageCountryReqDTO
     * @return
     */
    Result<PageRespDTO<CountryRespDTO>> pageCountry(PageCountryReqDTO pageCountryReqDTO);

    /**
     * 管理员分页搜索国家
     *
     * @param pageCountryForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<CountryRespDTO>> pageCountryForAdmin(PageCountryForAdminReqDTO pageCountryForAdminReqDTO);

    /**
     * 列出国家
     *
     * @param listCountryReqDTO
     * @return
     */
    Result<List<CountryRespDTO>> listCountry(ListCountryReqDTO listCountryReqDTO);

    /**
     * 管理员列出国家
     *
     * @param listCountryForAdminReqDTO
     * @return
     */
    Result<List<CountryRespDTO>> listCountryForAdmin(ListCountryForAdminReqDTO listCountryForAdminReqDTO);

    /**
     * 微服务客户端列出国家
     *
     * @param listCountryForClientReqDTO
     * @return
     */
    Result<List<CountryRespDTO>> listCountryForClient(ListCountryForClientReqDTO listCountryForClientReqDTO);

    /**
     * 根据国家编号获取国家详情
     *
     * @param getCountryByCountryCodeReqDTO
     * @return
     */
    Result<CountryRespDTO> getCountryByCountryCode(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO);

    /**
     * 根据国家编号判断国家是否存在
     *
     * @param getCountryByCountryCodeReqDTO
     * @return
     */
    Result<Boolean> existsCountryByCountryCode(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO);

    /**
     * 管理员新建国家
     *
     * @param addCountryForAdminReqDTO
     * @return
     */
    Result<Void> addCountryForAdmin(AddCountryForAdminReqDTO addCountryForAdminReqDTO);

    /**
     * 管理员更新国家
     *
     * @param updateCountryByCountryCodeForAdminReqDTO
     * @return
     */
    Result<Void> updateCountryByCountryForAdminCode(UpdateCountryByCountryCodeForAdminReqDTO updateCountryByCountryCodeForAdminReqDTO);

    /**
     * 管理员启用国家
     *
     * @param getCountryByCountryCodeReqDTO
     * @return
     */
    Result<Void> enableCountryForAdmin(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO);

    /**
     * 管理员停用国家
     *
     * @param getCountryByCountryCodeReqDTO
     * @return
     */
    Result<Void> disableCountryForAdmin(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO);

}