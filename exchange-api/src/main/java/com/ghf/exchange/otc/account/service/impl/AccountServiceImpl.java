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
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
        AccountRespDTO accountRespDTO = ModelMapperUtil.map(account, AccountRespDTO.class);

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
    public Result<Boolean> checkAccountForClientForClient() {
        List<Account> list = accountService.getRepository().findAll();
        BigDecimal bigDecimal = list.stream().map(e -> e.getAvailableBalance().add(e.getFrozenBalance())).reduce((x, y) -> x.add(y)).get();
        boolean b = bigDecimal.compareTo(BigDecimal.ZERO) == 0;

        BigDecimal bigDecimal2 = list.stream().map(e -> e.getTotalBalance()).reduce((x, y) -> x.add(y)).get();
        boolean b2 = bigDecimal.compareTo(BigDecimal.ZERO) == 0;

        return new Result<>(b && b2);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addAccountForClient(AddAccountForClientReqDTO addAccountForClientReqDTO) {
        Account account = ModelMapperUtil.map(addAccountForClientReqDTO, Account.class);
        account.setCreateTime(new Date());
        //持久化到数据库
        accountService.add(account);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> freezeBalanceForClient(FreezeBalanceForClientReqDTO freezeBalanceForClientReqDTO) {
        //TODO 分布式锁
        String username = freezeBalanceForClientReqDTO.getUsername();
        if (ObjectUtils.isEmpty(username)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_USERNAME_CAN_NOT_EMPTY);
        }
        String coinCode = freezeBalanceForClientReqDTO.getCoinCode();
        if (ObjectUtils.isEmpty(coinCode)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_COINCODE_CAN_NOT_EMPTY);
        }
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = ModelMapperUtil.map(accountRespDTO, Account.class);
        if (afterAccount.getAvailableBalance().compareTo(freezeBalanceForClientReqDTO.getBalance()) < 0) {
            return new Result<>(ResultCodeEnum.ACCOUNT_BALANCE_NOT_ENOUGH);

        }

        afterAccount.setAvailableBalance(afterAccount.getAvailableBalance().subtract(freezeBalanceForClientReqDTO.getBalance()));
        afterAccount.setFrozenBalance(afterAccount.getFrozenBalance().add(freezeBalanceForClientReqDTO.getBalance()));
        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> unFreezeBalanceForClient(UnFreezeBalanceForClientReqDTO unFreezeBalanceForClientReqDTO) {
        //TODO 分布式锁
        String username = unFreezeBalanceForClientReqDTO.getUsername();
        if (ObjectUtils.isEmpty(username)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_USERNAME_CAN_NOT_EMPTY);
        }
        String coinCode = unFreezeBalanceForClientReqDTO.getCoinCode();
        if (ObjectUtils.isEmpty(coinCode)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_COINCODE_CAN_NOT_EMPTY);
        }
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = ModelMapperUtil.map(accountRespDTO, Account.class);
        if (afterAccount.getFrozenBalance().compareTo(unFreezeBalanceForClientReqDTO.getBalance()) < 0) {
            return new Result<>(ResultCodeEnum.ACCOUNT_FROZEN_BALANCE_NOT_ENOUGH);

        }

        afterAccount.setAvailableBalance(afterAccount.getAvailableBalance().add(unFreezeBalanceForClientReqDTO.getBalance()));
        afterAccount.setFrozenBalance(afterAccount.getFrozenBalance().subtract(unFreezeBalanceForClientReqDTO.getBalance()));
        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> decFrozenBalanceForClient(DecFrozenBalanceForClientReqDTO decFrozenBalanceForClientReqDTO) {
        //TODO 分布式锁
        String username = decFrozenBalanceForClientReqDTO.getUsername();
        if (ObjectUtils.isEmpty(username)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_USERNAME_CAN_NOT_EMPTY);
        }
        String coinCode = decFrozenBalanceForClientReqDTO.getCoinCode();
        if (ObjectUtils.isEmpty(coinCode)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_COINCODE_CAN_NOT_EMPTY);
        }
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = ModelMapperUtil.map(accountRespDTO, Account.class);
        if (afterAccount.getFrozenBalance().compareTo(decFrozenBalanceForClientReqDTO.getBalance()) < 0) {
            return new Result<>(ResultCodeEnum.ACCOUNT_FROZEN_BALANCE_NOT_ENOUGH);

        }
        afterAccount.setTotalBalance(afterAccount.getTotalBalance().subtract(decFrozenBalanceForClientReqDTO.getBalance()));
        afterAccount.setFrozenBalance(afterAccount.getFrozenBalance().subtract(decFrozenBalanceForClientReqDTO.getBalance()));
        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> incBalanceForClient(IncBalanceForClientReqDTO incBalanceForClientReqDTO) {
        //TODO 分布式锁
        String username = incBalanceForClientReqDTO.getUsername();
        if (ObjectUtils.isEmpty(username)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_USERNAME_CAN_NOT_EMPTY);
        }
        String coinCode = incBalanceForClientReqDTO.getCoinCode();
        if (ObjectUtils.isEmpty(coinCode)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_COINCODE_CAN_NOT_EMPTY);
        }
        GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
        getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
        getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
        AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();
        Account afterAccount = ModelMapperUtil.map(accountRespDTO, Account.class);

        afterAccount.setTotalBalance(afterAccount.getTotalBalance().add(incBalanceForClientReqDTO.getBalance()));
        afterAccount.setAvailableBalance(afterAccount.getAvailableBalance().add(incBalanceForClientReqDTO.getBalance()));

        //持久化到数据库
        accountService.update(afterAccount);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Account", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> deleteAccountByUsernameAndCoinCodeForClient(GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO) {

        String username = getAccountByUsernameAndCoinCodeReqDTO.getUsername();
        if (ObjectUtils.isEmpty(username)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_USERNAME_CAN_NOT_EMPTY);
        }
        String coinCode = getAccountByUsernameAndCoinCodeReqDTO.getCoinCode();
        if (ObjectUtils.isEmpty(coinCode)) {
            return new Result<>(ResultCodeEnum.ACCOUNT_COINCODE_CAN_NOT_EMPTY);
        }
        Predicate predicate = QAccount.account.username.eq(username).and(QAccount.account.coinCode.eq(coinCode));
        accountService.delete(predicate);
        return new Result<>(ResultCodeEnum.OK);
    }

}