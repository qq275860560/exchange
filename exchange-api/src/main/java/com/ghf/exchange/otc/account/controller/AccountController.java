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

    @ApiOperation(value = "对账", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/checkAccount")
    @SneakyThrows
    public Result<Boolean> checkAccount() {
        return accountService.checkAccount();
    }

    @ApiOperation(value = "新建账户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/addAccount")
    @SneakyThrows
    public Result<Void> addAccount(@RequestBody AddAccountReqDTO addAccountReqDTO) {
        return accountService.addAccount(addAccountReqDTO);
    }

    @ApiOperation(value = "冻结账户金额", notes = "<p>冻结账户金额，比如：广告上架时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/freezeBalance")
    @SneakyThrows
    public Result<Void> freezeBalance(@RequestBody FreezeBalanceReqDTO freezeBalanceReqDTO) {
        return accountService.freezeBalance(freezeBalanceReqDTO);
    }

    @ApiOperation(value = "解冻账户金额", notes = "<p>解冻账户金额，比如：广告下架时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/unFreezeBalance")
    @SneakyThrows
    public Result<Void> unFreezeBalance(@RequestBody UnFreezeBalanceReqDTO unFreezeBalanceReqDTO) {
        return accountService.unFreezeBalance(unFreezeBalanceReqDTO);
    }

    @ApiOperation(value = "扣减账户冻结余额", notes = "<p>扣减账户冻结余额，比如：放行订单时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/decFrozenBalance")
    @SneakyThrows
    public Result<Void> decFrozenBalance(@RequestBody DecFrozenBalanceReqDTO decFrozenBalanceReqDTO) {
        return accountService.decFrozenBalance(decFrozenBalanceReqDTO);
    }

    @ApiOperation(value = "增加账户余额", notes = "<p>增加账户余额，比如：放行订单抽取手续费时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/incBalance")
    @SneakyThrows
    public Result<Void> incBalance(@RequestBody IncBalanceReqDTO incBalanceReqDTO) {
        return accountService.incBalance(incBalanceReqDTO);
    }

    @ApiOperation(value = "根据用户名和币种编号删除账户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/account/deleteAccountByUsernameAndCoinCode")
    @SneakyThrows
    public Result<Void> deleteAccountByUsernameAndCoinCode(@RequestBody GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {
        return accountService.deleteAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO);
    }

}
