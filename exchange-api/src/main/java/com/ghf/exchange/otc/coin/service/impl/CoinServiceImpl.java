package com.ghf.exchange.otc.coin.service.impl;

import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.coin.dto.AddCoinReqDTO;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.entity.Coin;
import com.ghf.exchange.otc.coin.entity.QCoin;
import com.ghf.exchange.otc.coin.repository.CoinRepository;
import com.ghf.exchange.otc.coin.service.CoinService;
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
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class CoinServiceImpl extends BaseServiceImpl<Coin, Long> implements CoinService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private CoinService coinService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public CoinServiceImpl(CoinRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Coin", key = "'getCoinByCoinCode:' +':'+#p0.coinCode")
    @Override
    @SneakyThrows
    public Result<CoinRespDTO> getCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {

        String coinCode = getCoinByCoinCodeReqDTO.getCoinCode();
        Predicate predicate = QCoin.coin.coinCode.eq(coinCode);
        Coin coin = coinService.get(predicate);

        //返回
        CoinRespDTO coinRespDTO = AutoMapUtils.map(coin, CoinRespDTO.class);

        return new Result<>(coinRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {

        String coinCode = getCoinByCoinCodeReqDTO.getCoinCode();
        Predicate predicate = QCoin.coin.coinCode.eq(coinCode);
        boolean b = coinService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Coin", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addCoin(AddCoinReqDTO addCoinReqDTO) {
        Coin coin = AutoMapUtils.map(addCoinReqDTO, Coin.class);
        coin.setCreateTime(new Date());

        //持久化到数据库
        coinService.add(coin);

        return new Result<>(ResultCodeEnum.OK);
    }

}