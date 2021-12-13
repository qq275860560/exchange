package com.ghf.exchange.otc.coin.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.coin.dto.*;
import com.ghf.exchange.otc.coin.entity.Coin;
import com.ghf.exchange.otc.coin.entity.QCoin;
import com.ghf.exchange.otc.coin.enums.CoinStatusEnum;
import com.ghf.exchange.otc.coin.repository.CoinRepository;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
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
import java.util.Date;
import java.util.List;

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

    @Cacheable(cacheNames = "Coin", key = "'pageCoin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.coinCode).concat(':').concat(#p0.coinName)", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")

    @Override
    @SneakyThrows
    public Result<PageRespDTO<CoinRespDTO>> pageCoin(PageCoinReqDTO pageCoinReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageCoinReqDTO.getCoinCode())) {
            predicate.and(QCoin.coin.coinCode.eq(pageCoinReqDTO.getCoinCode()));
        }

        if (!ObjectUtils.isEmpty(pageCoinReqDTO.getCoinName())) {
            predicate.and(QCoin.coin.coinName.eq(pageCoinReqDTO.getCoinName()));
        }

        PageRespDTO<CoinRespDTO> pageRespDTO = coinService.page(predicate, pageCoinReqDTO, CoinRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Coin", key = "'pageCoinForAdmin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.coinCode).concat(':').concat(#p0.coinName) ", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<CoinRespDTO>> pageCoinForAdmin(PageCoinForAdminReqDTO pageCoinForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageCoinForAdminReqDTO.getCoinCode())) {
            predicate.and(QCoin.coin.coinCode.eq(pageCoinForAdminReqDTO.getCoinCode()));
        }

        if (!ObjectUtils.isEmpty(pageCoinForAdminReqDTO.getCoinName())) {
            predicate.and(QCoin.coin.coinName.eq(pageCoinForAdminReqDTO.getCoinName()));
        }

        PageRespDTO<CoinRespDTO> pageRespDTO = coinService.page(predicate, pageCoinForAdminReqDTO, CoinRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Coin", key = "'listCoin:'.concat(':').concat(#p0.coinCode).concat(':').concat(#p0.coinName) ")
    @Override
    @SneakyThrows
    public Result<List<CoinRespDTO>> listCoin(ListCoinReqDTO listCoinReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listCoinReqDTO.getCoinCode())) {
            predicate.and(QCoin.coin.coinCode.eq(listCoinReqDTO.getCoinCode()));
        }

        if (!ObjectUtils.isEmpty(listCoinReqDTO.getCoinName())) {
            predicate.and(QCoin.coin.coinName.eq(listCoinReqDTO.getCoinName()));
        }

        predicate.and(QCoin.coin.status.eq(CoinStatusEnum.ENABLE.getCode()));

        List<CoinRespDTO> list = coinService.list(predicate, CoinRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Coin", key = "'listCoinForAdmin:'.concat(':').concat(#p0.coinCode).concat(':').concat(#p0.coinName)")
    @Override
    @SneakyThrows
    public Result<List<CoinRespDTO>> listCoinForAdmin(ListCoinForAdminReqDTO listCoinForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(listCoinForAdminReqDTO.getCoinCode())) {
            predicate.and(QCoin.coin.coinCode.eq(listCoinForAdminReqDTO.getCoinCode()));
        }

        if (!ObjectUtils.isEmpty(listCoinForAdminReqDTO.getCoinName())) {
            predicate.and(QCoin.coin.coinName.eq(listCoinForAdminReqDTO.getCoinName()));
        }
        predicate.and(QCoin.coin.status.eq(CoinStatusEnum.ENABLE.getCode()));

        List<CoinRespDTO> list = coinService.list(predicate, CoinRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Coin", key = "'listCoinForClient:'.concat(':').concat(#p0.coinCode).concat(':').concat(#p0.coinName)")
    @Override
    @SneakyThrows
    public Result<List<CoinRespDTO>> listCoinForClient(ListCoinForClientReqDTO listCoinForClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listCoinForClientReqDTO.getCoinCode())) {
            predicate.and(QCoin.coin.coinCode.eq(listCoinForClientReqDTO.getCoinCode()));
        }

        if (!ObjectUtils.isEmpty(listCoinForClientReqDTO.getCoinName())) {
            predicate.and(QCoin.coin.coinName.eq(listCoinForClientReqDTO.getCoinName()));
        }
        predicate.and(QCoin.coin.status.eq(CoinStatusEnum.ENABLE.getCode()));

        List<CoinRespDTO> list = coinService.list(predicate, CoinRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Coin", key = "'getCoinByCoinCode:' +':'+#p0.coinCode")
    @Override
    @SneakyThrows
    public Result<CoinRespDTO> getCoinByCoinCode(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {

        String coinCode = getCoinByCoinCodeReqDTO.getCoinCode();
        Predicate predicate = QCoin.coin.coinCode.eq(coinCode);
        Coin coin = coinService.get(predicate);

        //返回
        CoinRespDTO coinRespDTO = ModelMapperUtil.map(coin, CoinRespDTO.class);

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
    public Result<Void> addCoinForAdmin(AddCoinForAdminReqDTO addCoinForAdminReqDTO) {
        Coin coin = new Coin();

        //初始化id
        coin.setId(IdUtil.generateLongId());
        //判断编号
        if (!ObjectUtils.isEmpty(addCoinForAdminReqDTO.getCoinCode())) {
            //判断唯一性
            String coinCode = addCoinForAdminReqDTO.getCoinCode();
            GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
            getCoinByCoinCodeReqDTO.setCoinCode(coinCode);
            boolean b = coinService.existsCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.PAYMENT_EXISTS);
            }
            coin.setCoinCode(addCoinForAdminReqDTO.getCoinCode());
        } else {
            //自动生成编号
            coin.setCoinCode(coin.getId() + "");
        }

        coin.setCoinName(addCoinForAdminReqDTO.getCoinName());
        coin.setCoinUnit(addCoinForAdminReqDTO.getCoinUnit());
        coin.setMarketPrice(addCoinForAdminReqDTO.getMarketPrice());
        coin.setCoinRate(addCoinForAdminReqDTO.getCoinRate());
        coin.setPerAdvertiseMinAmount(addCoinForAdminReqDTO.getPerAdvertiseMinAmount());
        coin.setPerAdvertiseMaxAmount(addCoinForAdminReqDTO.getPerAdvertiseMaxAmount());
        coin.setPerOrderMinAmount(addCoinForAdminReqDTO.getPerOrderMinAmount());
        coin.setPerOrderMaxAmount(addCoinForAdminReqDTO.getPerOrderMaxAmount());
        //设置状态
        coin.setStatus(CoinStatusEnum.ENABLE.getCode());

        coin.setCreateTime(new Date());

        //持久化到数据库
        coinService.add(coin);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Coin", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateCoinByCoinForAdminCode(UpdateCoinByCoinCodeForAdminReqDTO updateCoinByCoinCodeForAdminReqDTO) {
        String coinCode = updateCoinByCoinCodeForAdminReqDTO.getCoinCode();
        //加载
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode(coinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        if (coinRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }
        //已禁用状态无权限
        if (coinRespDTO.getStatus() == CoinStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_DISABLE);
        }

        Coin afterCoin = ModelMapperUtil.map(coinRespDTO, Coin.class);

        afterCoin.setCoinName(updateCoinByCoinCodeForAdminReqDTO.getCoinName());
        afterCoin.setCoinUnit(updateCoinByCoinCodeForAdminReqDTO.getCoinUnit());
        afterCoin.setMarketPrice(updateCoinByCoinCodeForAdminReqDTO.getMarketPrice());
        afterCoin.setCoinRate(updateCoinByCoinCodeForAdminReqDTO.getCoinRate());
        afterCoin.setPerAdvertiseMinAmount(updateCoinByCoinCodeForAdminReqDTO.getPerAdvertiseMinAmount());
        afterCoin.setPerAdvertiseMaxAmount(updateCoinByCoinCodeForAdminReqDTO.getPerAdvertiseMaxAmount());
        afterCoin.setPerOrderMinAmount(updateCoinByCoinCodeForAdminReqDTO.getPerOrderMinAmount());
        afterCoin.setPerOrderMaxAmount(updateCoinByCoinCodeForAdminReqDTO.getPerOrderMaxAmount());
        //设置状态
        afterCoin.setStatus(CoinStatusEnum.ENABLE.getCode());
        //更新到数据库
        coinService.update(afterCoin);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Coin", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableCoinForAdmin(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        String coinCode = getCoinByCoinCodeReqDTO.getCoinCode();
        //加载
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        if (coinRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }

        //已启用状态无法启用
        if (coinRespDTO.getStatus() == CoinStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_ENABLE);
        }

        Coin afterCoin = ModelMapperUtil.map(coinRespDTO, Coin.class);

        //设置状态
        afterCoin.setStatus(CoinStatusEnum.ENABLE.getCode());

        //更新到数据库
        coinService.update(afterCoin);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Coin", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableCoinForAdmin(GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO) {
        String coinCode = getCoinByCoinCodeReqDTO.getCoinCode();
        //加载
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        if (coinRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }

        //已禁用状态无法禁用
        if (coinRespDTO.getStatus() == CoinStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_DISABLE);
        }

        Coin afterCoin = ModelMapperUtil.map(coinRespDTO, Coin.class);

        //设置状态
        afterCoin.setStatus(CoinStatusEnum.DISABLE.getCode());

        //更新到数据库
        coinService.update(afterCoin);

        return new Result<>(ResultCodeEnum.OK);
    }

    //TODO 调用接口获取币种的最新美元价格，再调用接口获取其他法币价格
    //TODO 真正手续费=固定手续费+比例手续费*amount

}