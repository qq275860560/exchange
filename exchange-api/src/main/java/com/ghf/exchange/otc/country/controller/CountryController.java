package com.ghf.exchange.otc.country.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.country.dto.*;
import com.ghf.exchange.otc.country.service.CountryService;
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
@Api(value = "国家接口", tags = {"国家接口"})
@RestController
@Lazy
@Slf4j
public class CountryController {

    @Lazy
    @Resource
    private CountryService coinService;

    @ApiOperation(value = "分页搜索国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/pageCountry")
    @SneakyThrows
    public Result<PageRespDTO<CountryRespDTO>> pageCountry(@RequestBody PageCountryReqDTO pageCountryReqDTO) {
        return coinService.pageCountry(pageCountryReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/pageCountryForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<CountryRespDTO>> pageCountryForAdmin(@RequestBody PageCountryForAdminReqDTO pageCountryForAdminReqDTO) {
        return coinService.pageCountryForAdmin(pageCountryForAdminReqDTO);
    }

    @ApiOperation(value = "列出国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/listCountry")
    @SneakyThrows
    public Result<List<CountryRespDTO>> listCountry(@RequestBody ListCountryReqDTO listCountryReqDTO) {
        return coinService.listCountry(listCountryReqDTO);
    }

    @ApiOperation(value = "管理员列出国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/listCountryForAdmin")
    @SneakyThrows
    public Result<List<CountryRespDTO>> listCountryForAdmin(@RequestBody ListCountryForAdminReqDTO listCountryForAdminReqDTO) {
        return coinService.listCountryForAdmin(listCountryForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端列出国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/listCountryForClient")
    @SneakyThrows
    public Result<List<CountryRespDTO>> listCountryForClient(@RequestBody ListCountryForClientReqDTO listCountryForClientReqDTO) {
        return coinService.listCountryForClient(listCountryForClientReqDTO);
    }

    @ApiOperation(value = "根据国家编号获取账户详情", notes = "<p> 查询所有 coin 挂单配置信息 \n 查询所有可用 coin 信息 </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/getCountryByCountryCode")
    @SneakyThrows
    public Result<CountryRespDTO> getCountryByCountryCode(@RequestBody GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {
        return coinService.getCountryByCountryCode(getCountryByCountryCodeReqDTO);
    }

    @ApiOperation(value = "根据国家编号判断国家是否存在", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/existsCountryByCountryCode")
    @SneakyThrows
    public Result<Boolean> existsCountryByCountryCode(@RequestBody GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {
        return coinService.existsCountryByCountryCode(getCountryByCountryCodeReqDTO);
    }

    @ApiOperation(value = "管理员新建国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/addCountryForAdmin")
    @SneakyThrows
    public Result<Void> addCountryForAdmin(@RequestBody AddCountryForAdminReqDTO addCountryForAdminReqDTO) {
        return coinService.addCountryForAdmin(addCountryForAdminReqDTO);
    }

    @ApiOperation(value = "管理员更新国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/updateCountryByCountryForAdminCode")
    @SneakyThrows
    public Result<Void> updateCountryByCountryForAdminCode(@RequestBody UpdateCountryByCountryCodeForAdminReqDTO updateCountryByCountryCodeForAdminReqDTO) {
        return coinService.updateCountryByCountryForAdminCode(updateCountryByCountryCodeForAdminReqDTO);
    }

    @ApiOperation(value = "管理员启用国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/enableCountryForAdmin")
    @SneakyThrows
    public Result<Void> enableCountryForAdmin(@RequestBody GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {
        return coinService.enableCountryForAdmin(getCountryByCountryCodeReqDTO);
    }

    @ApiOperation(value = "管理员停用国家", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/coin/disableCountryForAdmin")
    @SneakyThrows
    public Result<Void> disableCountryForAdmin(@RequestBody GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {
        return coinService.disableCountryForAdmin(getCountryByCountryCodeReqDTO);
    }

}
