package com.ghf.exchange.otc.account.controller;

import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.account.dto.*;
import com.ghf.exchange.otc.account.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "账户接口", tags = {"账户接口"})
@RestController
@Lazy
@Slf4j
public class AccountController {

    @Lazy
    @Resource
    private AccountService accountService;

    @ApiOperation(value = "根据用户名和币种编号获取账户详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/getAccountByUsernameAndCoinCode")
    @SneakyThrows
    public Result<AccountRespDTO> getAccountByUsernameAndCoinCode(@RequestBody GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {
        return accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO);
    }

    @ApiOperation(value = "根据用户名和币种编号判断账户是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/existsAccountByUsernameAndCoinCode")
    @SneakyThrows
    public Result<Boolean> existsAccountByUsernameAndCoinCode(@RequestBody GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {
        return accountService.existsAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO);
    }

    @ApiOperation(value = "微服务客户端对账", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/checkAccountForClient")
    @SneakyThrows
    public Result<Boolean> checkAccountForClient() {
        return accountService.checkAccountForClientForClient();
    }

    @ApiOperation(value = "微服务客户端新建账户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/addAccountForClient")
    @SneakyThrows
    public Result<Void> addAccountForClient(@RequestBody AddAccountForClientReqDTO addAccountForClientReqDTO) {
        return accountService.addAccountForClient(addAccountForClientReqDTO);
    }

    @ApiOperation(value = "微服务客户端冻结账户金额", notes = "<p>冻结账户金额，比如：广告上架时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/freezeBalanceForClient")
    @SneakyThrows
    public Result<Void> freezeBalanceForClient(@RequestBody FreezeBalanceForClientReqDTO freezeBalanceForClientReqDTO) {
        return accountService.freezeBalanceForClient(freezeBalanceForClientReqDTO);
    }

    @ApiOperation(value = "微服务客户端解冻账户金额", notes = "<p>解冻账户金额，比如：广告下架时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/unFreezeBalanceForClient")
    @SneakyThrows
    public Result<Void> unFreezeBalanceForClient(@RequestBody UnFreezeBalanceForClientReqDTO unFreezeBalanceForClientReqDTO) {
        return accountService.unFreezeBalanceForClient(unFreezeBalanceForClientReqDTO);
    }

    @ApiOperation(value = "微服务客户端扣减账户冻结余额", notes = "<p>扣减账户冻结余额，比如：放行订单时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/decFrozenBalanceForClient")
    @SneakyThrows
    public Result<Void> decFrozenBalanceForClient(@RequestBody DecFrozenBalanceForClientReqDTO decFrozenBalanceForClientReqDTO) {
        return accountService.decFrozenBalanceForClient(decFrozenBalanceForClientReqDTO);
    }

    @ApiOperation(value = "微服务客户端增加账户余额", notes = "<p>增加账户余额，比如：放行订单抽取手续费时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/incBalanceForClient")
    @SneakyThrows
    public Result<Void> incBalanceForClient(@RequestBody IncBalanceForClientReqDTO incBalanceForClientReqDTO) {
        return accountService.incBalanceForClient(incBalanceForClientReqDTO);
    }

    @ApiOperation(value = "微服务客户端根据用户名和币种编号删除账户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/deleteAccountByUsernameAndCoinCodeForClient")
    @SneakyThrows
    public Result<Void> deleteAccountByUsernameAndCoinCodeForClient(@RequestBody GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {
        return accountService.deleteAccountByUsernameAndCoinCodeForClient(getAccountByUsernameAndCoinCodeReqDTO);
    }

}
