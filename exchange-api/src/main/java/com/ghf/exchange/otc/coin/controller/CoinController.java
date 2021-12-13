package com.ghf.exchange.otc.coin.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.coin.dto.*;
import com.ghf.exchange.otc.coin.service.CoinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "数字货币接口", tags = {"数字货币接口"})
@RestController
@Lazy
@Slf4j
public class CoinController {

    @Lazy
    @Resource
    private CoinService coinService;

    @ApiOperation(value = "分页搜索数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/pageCoin")
    @SneakyThrows
    public Result<PageRespDTO<CoinRespDTO>> pageCoin(@RequestBody PageCoinReqDTO pageCoinReqDTO) {
        return coinService.pageCoin(pageCoinReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/pageCoinForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<CoinRespDTO>> pageCoinForAdmin(@RequestBody PageCoinForAdminReqDTO pageCoinForAdminReqDTO) {
        return coinService.pageCoinForAdmin(pageCoinForAdminReqDTO);
    }

    @ApiOperation(value = "列出数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/listCoin")
    @SneakyThrows
    public Result<List<CoinRespDTO>> listCoin(@RequestBody ListCoinReqDTO listCoinReqDTO) {
        return coinService.listCoin(listCoinReqDTO);
    }

    @ApiOperation(value = "管理员列出数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/listCoinForAdmin")
    @SneakyThrows
    public Result<List<CoinRespDTO>> listCoinForAdmin(@RequestBody ListCoinForAdminReqDTO listCoinForAdminReqDTO) {
        return coinService.listCoinForAdmin(listCoinForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端列出数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/listCoinForClient")
    @SneakyThrows
    public Result<List<CoinRespDTO>> listCoinForClient(@RequestBody ListCoinForClientReqDTO listCoinForClientReqDTO) {
        return coinService.listCoinForClient(listCoinForClientReqDTO);
    }

    @ApiOperation(value = "根据数字货币编号获取账户详情", notes = "<p> 查询所有 coin 挂单配置信息 \n 查询所有可用 coin 信息 </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/getCoinByCoinCode")
    @SneakyThrows
    public Result<CoinRespDTO> getCoinByCoinCode(@RequestBody GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        return coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO);
    }

    @ApiOperation(value = "根据数字货币编号判断数字货币是否存在", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/existsCoinByCoinCode")
    @SneakyThrows
    public Result<Boolean> existsCoinByCoinCode(@RequestBody GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        return coinService.existsCoinByCoinCode(getCoinByCoinCodeReqDTO);
    }

    @ApiOperation(value = "管理员新建数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/addCoinForAdmin")
    @SneakyThrows
    public Result<Void> addCoinForAdmin(@RequestBody AddCoinForAdminReqDTO addCoinForAdminReqDTO) {
        return coinService.addCoinForAdmin(addCoinForAdminReqDTO);
    }

    @ApiOperation(value = "管理员更新数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/updateCoinByCoinForAdminCode")
    @SneakyThrows
    public Result<Void> updateCoinByCoinForAdminCode(@RequestBody UpdateCoinByCoinCodeForAdminReqDTO updateCoinByCoinCodeForAdminReqDTO) {
        return coinService.updateCoinByCoinForAdminCode(updateCoinByCoinCodeForAdminReqDTO);
    }

    @ApiOperation(value = "管理员启用数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/enableCoinForAdmin")
    @SneakyThrows
    public Result<Void> enableCoinForAdmin(@RequestBody GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        return coinService.enableCoinForAdmin(getCoinByCoinCodeReqDTO);
    }

    @ApiOperation(value = "管理员停用数字货币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/disableCoinForAdmin")
    @SneakyThrows
    public Result<Void> disableCoinForAdmin(@RequestBody GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        return coinService.disableCoinForAdmin(getCoinByCoinCodeReqDTO);
    }

}
