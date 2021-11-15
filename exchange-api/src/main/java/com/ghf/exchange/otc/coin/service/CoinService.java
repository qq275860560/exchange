package com.ghf.exchange.otc.coin.service;

import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.coin.dto.AddCoinReqDTO;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.entity.Coin;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface CoinService extends BaseService<Coin, Long> {

    /**
     * 根据币种编号获取账户详情
     *
     * @param getCoinByCoinCodeReqDTO
     * @return
     */
    Result<CoinRespDTO> getCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO);

    /**
     * 根据币种编号判断账户是否存在
     *
     * @param getCoinByCoinCodeReqDTO
     * @return
     */
    Result<Boolean> existsCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO);

    /**
     * 新建账户
     *
     * @param addCoinReqDTO
     * @return
     */
    Result<Void> addCoin(AddCoinReqDTO addCoinReqDTO);

}