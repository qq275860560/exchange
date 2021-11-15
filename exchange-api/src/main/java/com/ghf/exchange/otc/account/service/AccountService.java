package com.ghf.exchange.otc.account.service;

import com.ghf.exchange.boss.common.dict.entity.Account;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.account.dto.*;
import com.ghf.exchange.service.BaseService;

/**
 * @author jiangyuanlin@163.com
 */

public interface AccountService extends BaseService<Account, Long> {

    /**
     * 根据用户名和币种编号获取账户详情
     *
     * @param getAccountByUsernameAndCoinCodeReqDTO
     * @return
     */
    Result<AccountRespDTO> getAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO);

    /**
     * 根据用户名和币种编号判断账户是否存在
     *
     * @param getAccountByUsernameAndCoinCodeReqDTO
     * @return
     */
    Result<Boolean> existsAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO);

    /**
     * 对账
     *
     * @param
     * @return
     */
    Result<Boolean> checkAccount();

    /**
     * 新建账户
     *
     * @param addAccountReqDTO
     * @return
     */
    Result<Void> addAccount(AddAccountReqDTO addAccountReqDTO);

    /**
     * 冻结账户金额，比如：广告上架时
     *
     * @param freezeBalanceReqDTO
     * @return
     */
    Result<Void> freezeBalance(FreezeBalanceReqDTO freezeBalanceReqDTO);

    /**
     * 解冻账户金额，比如：广告下架时
     *
     * @param unFreezeBalanceReqDTO
     * @return
     */
    Result<Void> unFreezeBalance(UnFreezeBalanceReqDTO unFreezeBalanceReqDTO);

    /**
     * 扣减账户冻结余额，比如：放行订单时
     *
     * @param decFrozenBalanceReqDTO
     * @return
     */
    Result<Void> decFrozenBalance(DecFrozenBalanceReqDTO decFrozenBalanceReqDTO);

    /**
     * 增加账户余额，比如：放行订单抽取手续费时
     *
     * @param incBalanceReqDTO
     * @return
     */
    Result<Void> incBalance(IncBalanceReqDTO incBalanceReqDTO);

    /**
     * 根据用户名和币种编号删除账户
     *
     * @param getAccountByUsernameAndCoinCodeReqDTO
     * @return
     */
    Result<Void> deleteAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO);

}