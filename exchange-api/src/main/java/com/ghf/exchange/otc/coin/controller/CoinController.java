package com.ghf.exchange.otc.coin.controller;

import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.coin.dto.AddCoinReqDTO;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "币种接口", tags = {"币种接口"})
@RestController
@Lazy
@Slf4j
public class CoinController {

    @Lazy
    @Resource
    private CoinService coinService;

    @ApiOperation(value = "根据币种编号获取账户详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/getCoinByCoinCode")
    @SneakyThrows
    public Result<CoinRespDTO> getCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        return coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO);
    }

    @ApiOperation(value = "根据币种编号判断账户是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/existsCoinByCoinCode")
    @SneakyThrows
    public Result<Boolean> existsCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        return coinService.existsCoinByCoinCode(getCoinByCoinCodeReqDTO);
    }

    @ApiOperation(value = "新建账户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/addCoin")
    @SneakyThrows
    public Result<Void> addCoin(AddCoinReqDTO addCoinReqDTO) {
        return coinService.addCoin(addCoinReqDTO);
    }

}
