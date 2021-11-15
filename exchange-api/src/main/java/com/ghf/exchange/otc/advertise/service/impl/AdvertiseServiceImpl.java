package com.ghf.exchange.otc.advertise.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.ClientRespDTO;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorication.user.util.IpUtil;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.exception.ResultCodeException;
import com.ghf.exchange.otc.account.dto.AccountRespDTO;
import com.ghf.exchange.otc.account.dto.FreezeBalanceReqDTO;
import com.ghf.exchange.otc.account.dto.GetAccountByUsernameAndCoinCodeReqDTO;
import com.ghf.exchange.otc.account.dto.UnFreezeBalanceReqDTO;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.entity.Advertise;
import com.ghf.exchange.otc.advertise.entity.QAdvertise;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBusinessPaymentTermTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseStatusEnum;
import com.ghf.exchange.otc.advertise.event.*;
import com.ghf.exchange.otc.advertise.repository.AdvertiseRepository;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.enums.AdvertiseLogTypeEnum;
import com.ghf.exchange.otc.advertiselog.service.AdvertiseLogService;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
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
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class AdvertiseServiceImpl extends BaseServiceImpl<Advertise, Long> implements AdvertiseService {

    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private AdvertiseService advertiseService;

    @Lazy
    @Resource
    private AdvertiseLogService advertiseLogService;

    @Lazy
    @Resource
    private AccountService accountService;

    @Lazy
    @Resource
    private CoinService coinService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AdvertiseServiceImpl(AdvertiseRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Advertise", key = "'pageAdvertise:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.advertiseBuySellType).concat(':').concat(#p0.advertiseCoinCode).concat(':').concat(#p0.status) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.advertiseBusinessUsername)      && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseRespDTO>> pageAdvertise(PageAdvertiseReqDTO pageAdvertiseReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageAdvertiseReqDTO.getAdvertiseCode())) {
            predicate.and(QAdvertise.advertise.advertiseCode.contains(pageAdvertiseReqDTO.getAdvertiseCode()));
        }

        if (pageAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.BUY.getCode()
                || pageAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()

        ) {
            predicate.and(QAdvertise.advertise.advertiseBuySellType.eq(pageAdvertiseReqDTO.getAdvertiseBuySellType()));
        }

        if (!ObjectUtils.isEmpty(pageAdvertiseReqDTO.getAdvertiseCoinCode())) {
            predicate.and(QAdvertise.advertise.advertiseCoinCode.contains(pageAdvertiseReqDTO.getAdvertiseCoinCode()));
        }

        if (!ObjectUtils.isEmpty(pageAdvertiseReqDTO.getAdvertiseBusinessUsername())) {
            predicate.and(QAdvertise.advertise.advertiseBusinessUsername.contains(pageAdvertiseReqDTO.getAdvertiseBusinessUsername()));
        }

        if (pageAdvertiseReqDTO.getStatus() == AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()
                || pageAdvertiseReqDTO.getStatus() == AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode()
                || pageAdvertiseReqDTO.getStatus() == AdvertiseStatusEnum.DELETED.getCode()

        ) {
            predicate.and(QAdvertise.advertise.status.eq(pageAdvertiseReqDTO.getStatus()));
        }

        PageRespDTO<AdvertiseRespDTO> pageResult = advertiseService.page(predicate, pageAdvertiseReqDTO, AdvertiseRespDTO.class);

        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "Advertise", key = "'getAdvertiseByAdvertiseCode:'+#p0.advertiseCode")
    @Override
    @SneakyThrows
    public Result<AdvertiseRespDTO> getAdvertiseByAdvertiseCode(GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO) {

        String code = getAdvertiseByAdvertiseCodeReqDTO.getAdvertiseCode();
        Predicate predicate = QAdvertise.advertise.advertiseCode.eq(code);
        Advertise advertis = advertiseService.get(predicate);

        //返回
        AdvertiseRespDTO advertisRespDTO = AutoMapUtils.map(advertis, AdvertiseRespDTO.class);

        return new Result<>(advertisRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAdvertiseByAdvertiseCode(GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO) {

        String code = getAdvertiseByAdvertiseCodeReqDTO.getAdvertiseCode();
        Predicate predicate = QAdvertise.advertise.advertiseCode.eq(code);
        boolean b = advertiseService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<AdvertiseRespDTO> getMatchAdvertise(GetMatchAdvertiseReqDTO getMatchAdvertiseReqDTO) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QAdvertise.advertise.advertiseCoinCode.eq(getMatchAdvertiseReqDTO.getAdvertiseCoinCode()));
        booleanBuilder.and(QAdvertise.advertise.advertiseBuySellType.eq(getMatchAdvertiseReqDTO.getAdvertiseBuySellType()));
        booleanBuilder.and(QAdvertise.advertise.advertiseAmount.goe(getMatchAdvertiseReqDTO.getAdvertiseAmount()));
        booleanBuilder.and(QAdvertise.advertise.status.eq(AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()));
        booleanBuilder.and(QAdvertise.advertise.advertiseFixedPrice.eq(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice()));
        booleanBuilder.and(QAdvertise.advertise.advertiseBusinessPaymentTermTypeArray.contains(getMatchAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeArray()));

        OrderSpecifier orderSpecifier = null;
        if (getMatchAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            orderSpecifier = QAdvertise.advertise.advertiseFixedPrice.asc();
        } else {
            orderSpecifier = QAdvertise.advertise.advertiseFixedPrice.desc();
        }

        Advertise advertis = advertiseService.getJpaQueryFactory().selectFrom(QAdvertise.advertise).where(booleanBuilder).orderBy(orderSpecifier).fetchFirst();

        //返回
        AdvertiseRespDTO advertisRespDTO = AutoMapUtils.map(advertis, AdvertiseRespDTO.class);

        return new Result<>(advertisRespDTO);
    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addAdvertise(AddAdvertiseReqDTO addAdvertiseReqDTO) {
        Advertise advertise = AutoMapUtils.map(addAdvertiseReqDTO, Advertise.class);
        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //初始化id
        advertise.setId(IdUtil.generateLongId());
        //判断广告编号
        if (!ObjectUtils.isEmpty(advertise.getAdvertiseCode())) {
            //判断唯一性
            String code = addAdvertiseReqDTO.getAdvertiseCode();
            GetAdvertiseByAdvertiseCodeReqDTO getRoleByRolenameReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getRoleByRolenameReqDTO.setAdvertiseCode(code);
            boolean b = advertiseService.existsAdvertiseByAdvertiseCode(getRoleByRolenameReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ADVERTISE_EXISTS);
            }
            advertise.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        } else {
            //自动生成广告编号
            advertise.setAdvertiseCode(advertise.getId() + "");
        }
        //买卖类型
        int buySellType = addAdvertiseReqDTO.getAdvertiseBuySellType();
        if (buySellType != AdvertiseBuySellTypeEnum.BUY.getCode() && buySellType != AdvertiseBuySellTypeEnum.SELL.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUY_SELL_TYPE_NOT_EXISTS);
        }
        //设置币种信息
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        String coinCode = addAdvertiseReqDTO.getAdvertiseCoinCode();
        getCoinByCoinCodeReqDTO.setCoinCode(coinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        advertise.setAdvertiseCoinName(coinRespDTO.getCoinName());
        advertise.setAdvertiseCoinUnit(coinRespDTO.getCoinUnit());
        advertise.setAdvertiseCoinRate(coinRespDTO.getCoinRate());

        if (addAdvertiseReqDTO.getAdvertiseAmount().compareTo(coinRespDTO.getPerMinAmount()) < 0 || addAdvertiseReqDTO.getAdvertiseAmount().compareTo(coinRespDTO.getPerMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AMOUNT_ERROR);
        }
        advertise.setAdvertiseAmount(addAdvertiseReqDTO.getAdvertiseAmount());
        advertise.setAdvertiseFrozenAmount(BigDecimal.ZERO);

        advertise.setAdvertisePerMaxAmount(coinRespDTO.getPerMaxAmount());
        advertise.setAdvertisePerMinAmount(coinRespDTO.getPerMinAmount());

        //初始化法币
        advertise.setAdvertiseLegalCurrencyCountryCode(addAdvertiseReqDTO.getAdvertiseLegalCurrencyCountryCode());
        advertise.setAdvertiseLegalCurrencySymbol(addAdvertiseReqDTO.getAdvertiseLegalCurrencySymbol());
        advertise.setAdvertiseLegalCurrencyUnit(addAdvertiseReqDTO.getAdvertiseLegalCurrencyUnit());
        //判断价格类型是固定价格还是浮动价格
        int priceType = addAdvertiseReqDTO.getAdvertisePriceType();
        if (priceType != AdvertisePriceTypeEnum.FIXED.getCode() && priceType != AdvertisePriceTypeEnum.PREMIUM.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_PRICE_TYPE_NOT_EXISTS);
        }
        advertise.setAdvertisePriceType(priceType);
        //固定价格时，初始化价格
        if (priceType == AdvertisePriceTypeEnum.FIXED.getCode()) {
            advertise.setAdvertiseFixedPrice(addAdvertiseReqDTO.getAdvertiseFixedPrice());
        } else {
            //浮动价格时，初始化浮动比例
            advertise.setAdvertisePremiumRate(addAdvertiseReqDTO.getAdvertisePremiumRate());
        }
        //判断支付期限
        int paymentTermTime = addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTime();
        if (paymentTermTime < coinRespDTO.getMinPaymentTermTime() || paymentTermTime > coinRespDTO.getMaxPaymentTermTime()) {
            //如果用户没有设置支付期限，或者设置了非法的支付期限，直接设置为系统默认的最大支付期限
            paymentTermTime = coinRespDTO.getMaxPaymentTermTime();
        }
        advertise.setAdvertiseBusinessPaymentTermTime(paymentTermTime);
        //设置收付款信息
        String paymentTermTypeArray = addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeArray();
        Set<String> paymentTermTypeSet = new HashSet<String>(Arrays.asList(paymentTermTypeArray.split(",")));
        if (paymentTermTypeSet.contains(AdvertiseBusinessPaymentTermTypeEnum.ALIPAY.getCode() + "")) {
            advertise.setAdvertiseBusinessPaymentTermTypeAlipayAccount(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeAlipayAccount());
            advertise.setAdvertiseBusinessPaymentTermTypeAlipayQrcode(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeAlipayQrcode());
        }
        if (paymentTermTypeSet.contains(AdvertiseBusinessPaymentTermTypeEnum.WECHAT.getCode() + "")) {
            advertise.setAdvertiseBusinessPaymentTermTypeWechatAccount(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeWechatAccount());
            advertise.setAdvertiseBusinessPaymentTermTypeWechatQrcode(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeWechatQrcode());
        }
        if (paymentTermTypeSet.contains(AdvertiseBusinessPaymentTermTypeEnum.BANK.getCode() + "")) {
            advertise.setAdvertiseBusinessPaymentTermTypeBankName(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeBankName());
            advertise.setAdvertiseBusinessPaymentTermTypeBankBranchName(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeBankBranchName());
            advertise.setAdvertiseBusinessPaymentTermTypeBankAccount(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeBankAccount());
            advertise.setAdvertiseBusinessPaymentTermTypeBankRealname(addAdvertiseReqDTO.getAdvertiseBusinessPaymentTermTypeBankRealname());
        }
        //设置收付款方式
        if (!paymentTermTypeSet.isEmpty()) {
            advertise.setAdvertiseBusinessPaymentTermTypeArray(paymentTermTypeArray);
        } else {
            return new Result<>(ResultCodeEnum.ADVERTISE_PAYMENT_TERM_TYPE_ARRAY_NOT_EMPTY);
        }

        //设置自动回复
        advertise.setAdvertiseAutoReplyContent(addAdvertiseReqDTO.getAdvertiseAutoReplyContent());
        //广告状态设置成下架状态
        advertise.setStatus(AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());
        //设置广告商家
        advertise.setAdvertiseBusinessUsername(currentLoginUser.getUsername());
        advertise.setAdvertiseBusinessNickname(currentLoginUser.getNickname());
        advertise.setAdvertiseBusinessRealname(currentLoginUser.getRealname());
        //设置广告创建时间
        advertise.setCreateTime(new Date());
        //持久化到数据库
        advertiseService.add(advertise);

        //发送到消息队列
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = new AddAdvertiseLogReqDTO();
        addAdvertiseLogReqDTO.setAdvertiseCode(advertise.getAdvertiseCode());
        addAdvertiseLogReqDTO.setAdvertiseLogType(AdvertiseLogTypeEnum.ADD_ADVERTISE.getCode());
        addAdvertiseLogReqDTO.setAdvertiseLogClientId(currentLoginClient.getClientId());
        addAdvertiseLogReqDTO.setAdvertiseLogUsername(currentLoginUser.getUsername());
        addAdvertiseLogReqDTO.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new AddAdvertiseEvent(addAdvertiseLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> putOnShelves(PutOnShelvesReqDTO putOnShelvesReqDTO) {
        //TODO 分布式锁，分布式事务
        String code = putOnShelvesReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(code);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Advertise afterAdvertise = AutoMapUtils.map(advertiseRespDTO, Advertise.class);

        //下架状态的广告才允许被上架
        if (afterAdvertise.getStatus() != AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_STATUS_IS_NOT_PUT_OFF_SHELVES);
        }

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //广告商家才能上架广告
        if (!username.equals(afterAdvertise.getAdvertiseBusinessUsername())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //买卖类型
        int buySellType = putOnShelvesReqDTO.getAdvertiseBuySellType();
        if (buySellType != AdvertiseBuySellTypeEnum.BUY.getCode() && buySellType != AdvertiseBuySellTypeEnum.SELL.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUY_SELL_TYPE_NOT_EXISTS);
        }
        //设置币种信息
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        String coinCode = putOnShelvesReqDTO.getAdvertiseCoinCode();
        getCoinByCoinCodeReqDTO.setCoinCode(coinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        afterAdvertise.setAdvertiseCoinName(coinRespDTO.getCoinName());
        afterAdvertise.setAdvertiseCoinUnit(coinRespDTO.getCoinUnit());
        afterAdvertise.setAdvertiseCoinRate(coinRespDTO.getCoinRate());

        if (putOnShelvesReqDTO.getAdvertiseAmount().compareTo(coinRespDTO.getPerMinAmount()) < 0 || putOnShelvesReqDTO.getAdvertiseAmount().compareTo(coinRespDTO.getPerMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AMOUNT_ERROR);
        }
        afterAdvertise.setAdvertiseAmount(putOnShelvesReqDTO.getAdvertiseAmount());
        afterAdvertise.setAdvertiseFrozenAmount(BigDecimal.ZERO);

        afterAdvertise.setAdvertisePerMaxAmount(coinRespDTO.getPerMaxAmount());
        afterAdvertise.setAdvertisePerMinAmount(coinRespDTO.getPerMinAmount());

        //设置库存
        if (putOnShelvesReqDTO.getAdvertiseAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
        }
        afterAdvertise.setAdvertiseAmount(putOnShelvesReqDTO.getAdvertiseAmount());

        if (buySellType == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            //判断库存必须小于等于当前账户余额
            GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
            getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
            getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(coinCode);
            AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();

            if (putOnShelvesReqDTO.getAdvertiseAmount().compareTo(accountRespDTO.getBalance()) > 0) {
                return new Result<>(ResultCodeEnum.ACCOUNT_BALANCE_NOT_ENOUGH);
            }
        }
        //初始化法币
        afterAdvertise.setAdvertiseLegalCurrencyCountryCode(putOnShelvesReqDTO.getAdvertiseLegalCurrencyCountryCode());
        afterAdvertise.setAdvertiseLegalCurrencySymbol(putOnShelvesReqDTO.getAdvertiseLegalCurrencySymbol());
        afterAdvertise.setAdvertiseLegalCurrencyUnit(putOnShelvesReqDTO.getAdvertiseLegalCurrencyUnit());
        //判断价格类型是固定价格还是浮动价格
        int priceType = putOnShelvesReqDTO.getAdvertisePriceType();
        if (priceType != AdvertisePriceTypeEnum.FIXED.getCode() && priceType != AdvertisePriceTypeEnum.PREMIUM.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_PRICE_TYPE_NOT_EXISTS);
        }
        afterAdvertise.setAdvertisePriceType(priceType);
        //固定价格时，初始化价格
        if (priceType == AdvertisePriceTypeEnum.FIXED.getCode()) {
            afterAdvertise.setAdvertiseFixedPrice(putOnShelvesReqDTO.getAdvertiseFixedPrice());
        } else {
            //浮动价格时，初始化浮动比例
            afterAdvertise.setAdvertisePremiumRate(putOnShelvesReqDTO.getAdvertisePremiumRate());
        }
        //判断支付期限
        int paymentTermTime = putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTime();
        if (paymentTermTime < coinRespDTO.getMinPaymentTermTime() || paymentTermTime > coinRespDTO.getMaxPaymentTermTime()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_PAYMENT_TERM_TIME_ERROR);
        } else {
            afterAdvertise.setAdvertiseBusinessPaymentTermTime(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTime());
        }
        //设置支付方式
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeAlipayAccount(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeAlipayAccount());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeAlipayQrcode(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeAlipayQrcode());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeWechatAccount(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeWechatAccount());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeWechatQrcode(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeWechatQrcode());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeBankName(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeBankName());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeBankBranchName(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeBankBranchName());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeBankAccount(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeBankAccount());
        afterAdvertise.setAdvertiseBusinessPaymentTermTypeBankRealname(putOnShelvesReqDTO.getAdvertiseBusinessPaymentTermTypeBankRealname());
        //设置自动回复
        afterAdvertise.setAdvertiseAutoReplyContent(putOnShelvesReqDTO.getAdvertiseAutoReplyContent());
        //广告状态设置成上架状态
        afterAdvertise.setStatus(AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        //设置广告商家
        afterAdvertise.setAdvertiseBusinessUsername(currentLoginUser.getUsername());
        afterAdvertise.setAdvertiseBusinessNickname(currentLoginUser.getNickname());
        //设置广告创建时间
        afterAdvertise.setCreateTime(new Date());
        //持久化到数据库
        advertiseService.update(afterAdvertise);

        if (buySellType == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            //冻结账户金额
            BigDecimal balance = putOnShelvesReqDTO.getAdvertiseAmount().multiply(BigDecimal.ONE.add(afterAdvertise.getAdvertiseCoinRate()));
            FreezeBalanceReqDTO freezeBalanceReqDTO = new FreezeBalanceReqDTO();
            freezeBalanceReqDTO.setUsername(username);
            freezeBalanceReqDTO.setCoinCode(coinCode);
            freezeBalanceReqDTO.setBalance(balance);
            freezeBalanceReqDTO.setRemark("卖币广告上架冻结广告商家账户余额");
            Result freezeBalanceResult = accountService.freezeBalance(freezeBalanceReqDTO);
            if (freezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
                throw new ResultCodeException(freezeBalanceResult.getCode(), freezeBalanceResult.getMsg());
            }
        }

        //发送到消息队列
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = new AddAdvertiseLogReqDTO();
        addAdvertiseLogReqDTO.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        addAdvertiseLogReqDTO.setAdvertiseLogType(AdvertiseLogTypeEnum.PUT_ON_SHELVES.getCode());
        addAdvertiseLogReqDTO.setAdvertiseLogClientId(currentLoginClient.getClientId());
        addAdvertiseLogReqDTO.setAdvertiseLogUsername(currentLoginUser.getUsername());
        addAdvertiseLogReqDTO.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new PutOnShelvesEvent(addAdvertiseLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> putOffShelves(PutOffShelvesReqDTO putOffShelvesReqDTO) {
        //TODO 分布式锁，分布式事务
        String code = putOffShelvesReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(code);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Advertise afterAdvertise = AutoMapUtils.map(advertiseRespDTO, Advertise.class);

        //上架状态的广告才允许被下架
        if (afterAdvertise.getStatus() != AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_STATUS_IS_NOT_PUT_ON_SHELVES);
        }
        //冻结数量为0的广告才允许被下架
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) != 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_IS_NOT_ZERO);
        }

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //广告商家才能下架广告
        if (!username.equals(afterAdvertise.getAdvertiseBusinessUsername())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //广告状态设置成下架状态
        afterAdvertise.setStatus(AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //持久化到数据库
        advertiseService.update(afterAdvertise);
        //解冻账户金额
        if (afterAdvertise.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            BigDecimal balance = afterAdvertise.getAdvertiseAmount().multiply(BigDecimal.ONE.add(afterAdvertise.getAdvertiseCoinRate()));
            UnFreezeBalanceReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceReqDTO();
            unFreezeBalanceReqDTO.setUsername(username);
            unFreezeBalanceReqDTO.setCoinCode(afterAdvertise.getAdvertiseCoinCode());
            unFreezeBalanceReqDTO.setBalance(balance);
            unFreezeBalanceReqDTO.setRemark("卖币广告下架架解冻广告商家账户余额");
            accountService.unFreezeBalance(unFreezeBalanceReqDTO);
        }

        //发送到消息队列
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = new AddAdvertiseLogReqDTO();
        addAdvertiseLogReqDTO.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        addAdvertiseLogReqDTO.setAdvertiseLogType(AdvertiseLogTypeEnum.PUT_OFF_SHELVES.getCode());
        addAdvertiseLogReqDTO.setAdvertiseLogClientId(currentLoginClient.getClientId());
        addAdvertiseLogReqDTO.setAdvertiseLogUsername(currentLoginUser.getUsername());
        addAdvertiseLogReqDTO.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new PutOffShelvesEvent(addAdvertiseLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> deleteAdvertise(DeleteAdvertiseReqDTO deleteAdvertiseReqDTO) {
        String code = deleteAdvertiseReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(code);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Advertise afterAdvertise = AutoMapUtils.map(advertiseRespDTO, Advertise.class);

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //广告商家才能下架广告
        if (!username.equals(afterAdvertise.getAdvertiseBusinessUsername())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //广告状态设置成删除状态
        afterAdvertise.setStatus(AdvertiseStatusEnum.DELETED.getCode());

        //持久化到数据库
        advertiseService.update(afterAdvertise);

        //发送到消息队列
        AddAdvertiseLogReqDTO addAdvertiseLogReqDTO = new AddAdvertiseLogReqDTO();
        addAdvertiseLogReqDTO.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        addAdvertiseLogReqDTO.setAdvertiseLogType(AdvertiseLogTypeEnum.DELETE_ADVERTISE.getCode());
        addAdvertiseLogReqDTO.setAdvertiseLogClientId(currentLoginClient.getClientId());
        addAdvertiseLogReqDTO.setAdvertiseLogUsername(currentLoginUser.getUsername());
        addAdvertiseLogReqDTO.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new DeleteAdvertiseEvent(addAdvertiseLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> freezeAdvertiseAmount(FreezeAdvertiseAmountReqDTO freezeAdvertiseAmountReqDTO) {
        String code = freezeAdvertiseAmountReqDTO.getAdvertiseCode();

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(code);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Advertise afterAdvertise = AutoMapUtils.map(advertiseRespDTO, Advertise.class);
        if (afterAdvertise.getAdvertiseAmount().compareTo(freezeAdvertiseAmountReqDTO.getAdvertiseAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AMOUNT_NOT_ENOUGH);

        }
        afterAdvertise.setAdvertiseAmount(afterAdvertise.getAdvertiseAmount().subtract(freezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        afterAdvertise.setAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount().add(freezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        //持久化到数据库
        advertiseService.update(afterAdvertise);
        //发送到消息队列
        applicationEventPublisher.publishEvent(new FreezeAdvertiseAmountEvent(freezeAdvertiseAmountReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> unFreezeAdvertiseAmount(UnFreezeAdvertiseAmountReqDTO unFreezeAdvertiseAmountReqDTO) {
        String code = unFreezeAdvertiseAmountReqDTO.getAdvertiseCode();

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(code);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Advertise afterAdvertise = AutoMapUtils.map(advertiseRespDTO, Advertise.class);
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_NOT_ENOUGH);

        }
        afterAdvertise.setAdvertiseAmount(afterAdvertise.getAdvertiseAmount().add(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        afterAdvertise.setAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount().subtract(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        //持久化到数据库
        advertiseService.update(afterAdvertise);
        //发送到消息队列
        applicationEventPublisher.publishEvent(new UnFreezeAdvertiseAmountEvent(unFreezeAdvertiseAmountReqDTO));
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> decAdvertiseFrozenAmount(DecAdvertiseFrozenAmountReqDTO decAdvertiseFrozenAmountReqDTO) {
        String code = decAdvertiseFrozenAmountReqDTO.getAdvertiseCode();

        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByCodeReqDTO.setAdvertiseCode(code);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByCodeReqDTO).getData();
        Advertise afterAdvertise = AutoMapUtils.map(advertiseRespDTO, Advertise.class);
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(decAdvertiseFrozenAmountReqDTO.getAdvertiseAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_NOT_ENOUGH);

        }
        afterAdvertise.setAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount().subtract(decAdvertiseFrozenAmountReqDTO.getAdvertiseAmount()));
        //持久化到数据库
        advertiseService.update(afterAdvertise);
        //发送到消息队列
        applicationEventPublisher.publishEvent(new DecAdvertiseFrozenAmountEvent(decAdvertiseFrozenAmountReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

}