package com.ghf.exchange.otc.coin.service;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.coin.dto.*;
import com.ghf.exchange.otc.coin.entity.Coin;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface CoinService extends BaseService<Coin, Long> {
    /**
     * 分页搜索数字货币
     *
     * @param pageCoinReqDTO
     * @return
     */
    Result<PageRespDTO<CoinRespDTO>> pageCoin(PageCoinReqDTO pageCoinReqDTO);

    /**
     * 管理员分页搜索数字货币
     *
     * @param pageCoinForAdminReqDTO
     * @return
     */
    Result<PageRespDTO<CoinRespDTO>> pageCoinForAdmin(PageCoinForAdminReqDTO pageCoinForAdminReqDTO);

    /**
     * 列出数字货币
     *
     * @param listCoinReqDTO
     * @return
     */
    Result<List<CoinRespDTO>> listCoin(ListCoinReqDTO listCoinReqDTO);

    /**
     * 管理员列出数字货币
     *
     * @param listCoinForAdminReqDTO
     * @return
     */
    Result<List<CoinRespDTO>> listCoinForAdmin(ListCoinForAdminReqDTO listCoinForAdminReqDTO);

    /**
     * 微服务客户端列出数字货币
     *
     * @param listCoinForClientReqDTO
     * @return
     */
    Result<List<CoinRespDTO>> listCoinForClient(ListCoinForClientReqDTO listCoinForClientReqDTO);

    /**
     * 根据数字货币编号获取数字货币详情
     *
     * @param getCoinByCoinCodeReqDTO
     * @return
     */
    Result<CoinRespDTO> getCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO);

    /**
     * 根据数字货币编号判断数字货币是否存在
     *
     * @param getCoinByCoinCodeReqDTO
     * @return
     */
    Result<Boolean> existsCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO);

    /**
     * 管理员新建数字货币
     *
     * @param addCoinForAdminReqDTO
     * @return
     */
    Result<Void> addCoinForAdmin(AddCoinForAdminReqDTO addCoinForAdminReqDTO);

    /**
     * 管理员更新数字货币
     *
     * @param updateCoinByCoinCodeForAdminReqDTO
     * @return
     */
    Result<Void> updateCoinByCoinForAdminCode(UpdateCoinByCoinCodeForAdminReqDTO updateCoinByCoinCodeForAdminReqDTO);

    /**
     * 管理员启用数字货币
     *
     * @param getCoinByCoinCodeReqDTO
     * @return
     */
    Result<Void> enableCoinForAdmin(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO);

    /**
     * 管理员停用数字货币
     *
     * @param getCoinByCoinCodeReqDTO
     * @return
     */
    Result<Void> disableCoinForAdmin(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO);

}