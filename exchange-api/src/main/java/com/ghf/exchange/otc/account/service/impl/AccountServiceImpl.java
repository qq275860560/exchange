package com.ghf.exchange.otc.account.service.impl;

import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.dict.entity.Account;
import com.ghf.exchange.boss.common.dict.entity.QAccount;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.account.dto.*;
import com.ghf.exchange.otc.account.repository.AccountRepository;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class AccountServiceImpl extends BaseServiceImpl<Account, Long> implements AccountService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private AccountService accountService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AccountServiceImpl(AccountRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Account", key = "'getAccountByUsernameAndCoinCode:'+#p0.username+':'+#p0.coinCode")
    @Override
    @SneakyThrows
    public Result<AccountRespDTO> getAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {

        String username = getAccountByUsernameAndCoinCodeReqDTO.getUsername();
        String coinCode = getAccountByUsernameAndCoinCodeReqDTO.getCoinCode();
        Predicate predicate = QAccount.account.username.eq(username).and(QAccount.account.coinCode.eq(coinCode));
        Account account = accountService.get(predicate);

        //返回
        AccountRespDTO accountRespDTO = AutoMapUtils.map(account, AccountRespDTO.class);

        return new Result<>(accountRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {

        String username = getAccountByUsernameAndCoinCodeReqDTO.getUsername();
        String coinCode = getAccountByUsernameAndCoinCodeReqDTO.getCoinCode();
        Predicate predicate = QAccount.account.username.eq(username).and(QAccount.account.coinCode.eq(coinCode));
        boolean b = accountService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> checkAccount() {
        BigDecimal bigDecimal = accountService.getRepository().findAll().stream().map(e -> e.getBalance().add(e.getFrozenBalance())).reduce((x, y) -> x.add(y)).get();
        boolean b = bigDecimal.compareTo(BigDecimal.ZERO) == 0;
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addAccount(AddAccountReqDTO addAccountReqDTO) {
        Account account = AutoMapUtils.map(addAccountReqDTO, Account.class);
        account.setCreateTime(new Date());
        //持久化到数据库
        accountService.add(account);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> freezeBalance(FreezeBalanceReqDTO freezeBalanceReqDTO) {
        //TODO 分布式锁
        String username = freezeBalanceReqDTO.getUsername();
        String coinCode = freezeBalanceReqDTO.getCoinCode();
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = AutoMapUtils.map(accountRespDTO, Account.class);
        if (afterAccount.getBalance().compareTo(freezeBalanceReqDTO.getBalance()) < 0) {
            return new Result<>(ResultCodeEnum.ACCOUNT_BALANCE_NOT_ENOUGH);

        }

        afterAccount.setBalance(afterAccount.getBalance().subtract(freezeBalanceReqDTO.getBalance()));
        afterAccount.setFrozenBalance(afterAccount.getFrozenBalance().add(freezeBalanceReqDTO.getBalance()));
        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> unFreezeBalance(UnFreezeBalanceReqDTO unFreezeBalanceReqDTO) {
        //TODO 分布式锁
        String username = unFreezeBalanceReqDTO.getUsername();
        String coinCode = unFreezeBalanceReqDTO.getCoinCode();
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = AutoMapUtils.map(accountRespDTO, Account.class);
        if (afterAccount.getFrozenBalance().compareTo(unFreezeBalanceReqDTO.getBalance()) < 0) {
            return new Result<>(ResultCodeEnum.ACCOUNT_FROZEN_BALANCE_NOT_ENOUGH);

        }
        afterAccount.setBalance(afterAccount.getBalance().add(unFreezeBalanceReqDTO.getBalance()));
        afterAccount.setFrozenBalance(afterAccount.getFrozenBalance().subtract(unFreezeBalanceReqDTO.getBalance()));
        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> decFrozenBalance(DecFrozenBalanceReqDTO decFrozenBalanceReqDTO) {
        //TODO 分布式锁
        String username = decFrozenBalanceReqDTO.getUsername();
        String coinCode = decFrozenBalanceReqDTO.getCoinCode();
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = AutoMapUtils.map(accountRespDTO, Account.class);
        if (afterAccount.getFrozenBalance().compareTo(decFrozenBalanceReqDTO.getBalance()) < 0) {
            return new Result<>(ResultCodeEnum.ACCOUNT_FROZEN_BALANCE_NOT_ENOUGH);

        }
        afterAccount.setFrozenBalance(afterAccount.getFrozenBalance().subtract(decFrozenBalanceReqDTO.getBalance()));
        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> incBalance(IncBalanceReqDTO incBalanceReqDTO) {
        //TODO 分布式锁
        String username = incBalanceReqDTO.getUsername();
        String coinCode = incBalanceReqDTO.getCoinCode();
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = AutoMapUtils.map(accountRespDTO, Account.class);

        afterAccount.setBalance(afterAccount.getBalance().add(incBalanceReqDTO.getBalance()));

        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> deleteAccountByUsernameAndCoinCode(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {

        String username = getAccountByUsernameAndCoinCodeReqDTO.getUsername();
        String coinCode = getAccountByUsernameAndCoinCodeReqDTO.getCoinCode();
        Predicate predicate = QAccount.account.username.eq(username).and(QAccount.account.coinCode.eq(coinCode));
        accountService.delete(predicate);
        return new Result<>(ResultCodeEnum.OK);
    }

}