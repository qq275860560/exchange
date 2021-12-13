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
     * 根据用户名和数字货币编号获取账户详情
     *
     * @param getAccountByUsernameAndCoinCodeReqDTO
     * @return
     */
    Result<AccountRespDTO> getAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO);

    /**
     * 根据用户名和数字货币编号判断账户是否存在
     *
     * @param getAccountByUsernameAndCoinCodeReqDTO
     * @return
     */
    Result<Boolean> existsAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO);

    /**
     * 微服务客户端对账(测试环境)
     *
     * @param
     * @return
     */
    Result<Boolean> checkAccountForClientForClient();

    //TODO 创建用户时，批量创建数字货币账户

    /**
     * 微服务客户端新建账户
     *
     * @param addAccountForClientReqDTO
     * @return
     */
    Result<Void> addAccountForClient(AddAccountForClientReqDTO addAccountForClientReqDTO);

    /**
     * 微服务客户端冻结账户金额，比如：广告上架时
     *
     * @param freezeBalanceForClientReqDTO
     * @return
     */
    Result<Void> freezeBalanceForClient(FreezeBalanceForClientReqDTO freezeBalanceForClientReqDTO);

    /**
     * 微服务客户端解冻账户金额，比如：广告下架时
     *
     * @param unFreezeBalanceForClientReqDTO
     * @return
     */
    Result<Void> unFreezeBalanceForClient(UnFreezeBalanceForClientReqDTO unFreezeBalanceForClientReqDTO);

    /**
     * 微服务客户端扣减账户冻结余额，比如：放行订单时
     *
     * @param decFrozenBalanceForClientReqDTO
     * @return
     */
    Result<Void> decFrozenBalanceForClient(DecFrozenBalanceForClientReqDTO decFrozenBalanceForClientReqDTO);

    /**
     * 微服务客户端增加账户余额，比如：放行订单抽取手续费时
     *
     * @param incBalanceForClientReqDTO
     * @return
     */
    Result<Void> incBalanceForClient(IncBalanceForClientReqDTO incBalanceForClientReqDTO);

    /**
     * 微服务客户端根据用户名和数字货币编号删除账户
     *
     * @param getAccountByUsernameAndCoinCodeReqDTO
     * @return
     */
    Result<Void> deleteAccountByUsernameAndCoinCodeForClient(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO);

}
