package com.ghf.exchange.otc.legalcurrency.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.legalcurrency.dto.*;
import com.ghf.exchange.otc.legalcurrency.service.LegalCurrencyService;
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
@Api(value = "法币接口", tags = {"法币接口"})
@RestController
@Lazy
@Slf4j
public class LegalCurrencyController {

    @Lazy
    @Resource
    private LegalCurrencyService legalCurrencyService;

    @ApiOperation(value = "分页搜索法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/pageLegalCurrency")
    @SneakyThrows
    public Result<PageRespDTO<LegalCurrencyRespDTO>> pageLegalCurrency(@RequestBody PageLegalCurrencyReqDTO pageLegalCurrencyReqDTO) {
        return legalCurrencyService.pageLegalCurrency(pageLegalCurrencyReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/pageLegalCurrencyForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<LegalCurrencyRespDTO>> pageLegalCurrencyForAdmin(@RequestBody PageLegalCurrencyForAdminReqDTO pageLegalCurrencyForAdminReqDTO) {
        return legalCurrencyService.pageLegalCurrencyForAdmin(pageLegalCurrencyForAdminReqDTO);
    }

    @ApiOperation(value = "列出法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/listLegalCurrency")
    @SneakyThrows
    public Result<List<LegalCurrencyRespDTO>> listLegalCurrency(@RequestBody ListLegalCurrencyReqDTO listLegalCurrencyReqDTO) {
        return legalCurrencyService.listLegalCurrency(listLegalCurrencyReqDTO);
    }

    @ApiOperation(value = "管理员列出法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/listLegalCurrencyForAdmin")
    @SneakyThrows
    public Result<List<LegalCurrencyRespDTO>> listLegalCurrencyForAdmin(@RequestBody ListLegalCurrencyForAdminReqDTO listLegalCurrencyForAdminReqDTO) {
        return legalCurrencyService.listLegalCurrencyForAdmin(listLegalCurrencyForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端列出法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/listLegalCurrencyForClient")
    @SneakyThrows
    public Result<List<LegalCurrencyRespDTO>> listLegalCurrencyForClient(@RequestBody ListLegalCurrencyForClientReqDTO listLegalCurrencyForClientReqDTO) {
        return legalCurrencyService.listLegalCurrencyForClient(listLegalCurrencyForClientReqDTO);
    }

    @ApiOperation(value = "根据法币编号获取账户详情", notes = "<p> 查询所有 legalCurrency 挂单配置信息 \n 查询所有可用 legalCurrency 信息 </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/getLegalCurrencyByLegalCurrencyCode")
    @SneakyThrows
    public Result<LegalCurrencyRespDTO> getLegalCurrencyByLegalCurrencyCode(@RequestBody GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {
        return legalCurrencyService.getLegalCurrencyByLegalCurrencyCode(getLegalCurrencyByLegalCurrencyCodeReqDTO);
    }

    @ApiOperation(value = "根据法币编号判断法币是否存在", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/existsLegalCurrencyByLegalCurrencyCode")
    @SneakyThrows
    public Result<Boolean> existsLegalCurrencyByLegalCurrencyCode(@RequestBody GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {
        return legalCurrencyService.existsLegalCurrencyByLegalCurrencyCode(getLegalCurrencyByLegalCurrencyCodeReqDTO);
    }

    @ApiOperation(value = "管理员新建法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/addLegalCurrencyForAdmin")
    @SneakyThrows
    public Result<Void> addLegalCurrencyForAdmin(@RequestBody AddLegalCurrencyForAdminReqDTO addLegalCurrencyForAdminReqDTO) {
        return legalCurrencyService.addLegalCurrencyForAdmin(addLegalCurrencyForAdminReqDTO);
    }

    @ApiOperation(value = "管理员更新法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/updateLegalCurrencyByLegalCurrencyForAdminCode")
    @SneakyThrows
    public Result<Void> updateLegalCurrencyByLegalCurrencyForAdminCode(@RequestBody UpdateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO) {
        return legalCurrencyService.updateLegalCurrencyByLegalCurrencyForAdminCode(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO);
    }

    @ApiOperation(value = "管理员启用法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/enableLegalCurrencyForAdmin")
    @SneakyThrows
    public Result<Void> enableLegalCurrencyForAdmin(@RequestBody GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {
        return legalCurrencyService.enableLegalCurrencyForAdmin(getLegalCurrencyByLegalCurrencyCodeReqDTO);
    }

    @ApiOperation(value = "管理员停用法币", notes = "<p> </p>", httpMethod = "POST")
    @PostMapping(value = "/api/legalCurrency/disableLegalCurrencyForAdmin")
    @SneakyThrows
    public Result<Void> disableLegalCurrencyForAdmin(@RequestBody GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {
        return legalCurrencyService.disableLegalCurrencyForAdmin(getLegalCurrencyByLegalCurrencyCodeReqDTO);
    }

}
