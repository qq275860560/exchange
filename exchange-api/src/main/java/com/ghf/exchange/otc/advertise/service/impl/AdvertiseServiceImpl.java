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
import com.ghf.exchange.otc.account.dto.FreezeBalanceForClientReqDTO;
import com.ghf.exchange.otc.account.dto.GetAccountByUsernameAndCoinCodeReqDTO;
import com.ghf.exchange.otc.account.dto.UnFreezeBalanceForClientReqDTO;
import com.ghf.exchange.otc.account.service.AccountService;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.entity.Advertise;
import com.ghf.exchange.otc.advertise.entity.QAdvertise;
import com.ghf.exchange.otc.advertise.enums.AdvertiseBuySellTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertisePriceTypeEnum;
import com.ghf.exchange.otc.advertise.enums.AdvertiseStatusEnum;
import com.ghf.exchange.otc.advertise.event.*;
import com.ghf.exchange.otc.advertise.repository.AdvertiseRepository;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
import com.ghf.exchange.otc.advertiseamountlog.enums.AdvertiseAmountLogTypeEnum;
import com.ghf.exchange.otc.advertisebusiness.dto.AdvertiseBusinessRespDTO;
import com.ghf.exchange.otc.advertisebusiness.dto.GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO;
import com.ghf.exchange.otc.advertisebusiness.service.AdvertiseBusinessService;
import com.ghf.exchange.otc.advertiselog.enums.AdvertiseLogTypeEnum;
import com.ghf.exchange.otc.advertiselog.service.AdvertiseLogService;
import com.ghf.exchange.otc.coin.dto.CoinRespDTO;
import com.ghf.exchange.otc.coin.dto.GetCoinByCoinCodeReqDTO;
import com.ghf.exchange.otc.coin.enums.CoinStatusEnum;
import com.ghf.exchange.otc.coin.service.CoinService;
import com.ghf.exchange.otc.payment.dto.ListPaymentForClientReqDTO;
import com.ghf.exchange.otc.payment.dto.PaymentRespDTO;
import com.ghf.exchange.otc.payment.service.PaymentService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private PaymentService paymentService;

    @Lazy
    @Resource
    private AdvertiseBusinessService advertiseBusinessService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AdvertiseServiceImpl(AdvertiseRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Advertise", key = "'pageAdvertise:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.advertiseCode).concat(':').concat(#p0.advertiseBuySellType).concat(':').concat(#p0.advertiseCoinCode).concat(':').concat(#p0.advertiseLegalCurrencySymbol).concat(':').concat(#p0.status) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.advertiseBusinessUsername)      && #p0.sort!=null && #p0.sort.size()==1   ")
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

        PageRespDTO<AdvertiseRespDTO> pageRespDTO = advertiseService.page(predicate, pageAdvertiseReqDTO, AdvertiseRespDTO.class);
        pageRespDTO.getList().forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getAdvertiseBusinessPaymentTypes())) {
                e.setAdvertiseBusinessPaymentTypeSet(Arrays.stream(e.getAdvertiseBusinessPaymentTypes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).map(Integer::parseInt).collect(Collectors.toSet()));
            }

        });

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Advertise", key = "'getAdvertiseByAdvertiseCode:'+#p0.advertiseCode")
    @Override
    @SneakyThrows
    public Result<AdvertiseRespDTO> getAdvertiseByAdvertiseCode(GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO) {

        String advertiseCode = getAdvertiseByAdvertiseCodeReqDTO.getAdvertiseCode();
        Predicate predicate = QAdvertise.advertise.advertiseCode.eq(advertiseCode);
        Advertise advertis = advertiseService.get(predicate);

        //??????
        AdvertiseRespDTO advertisRespDTO = ModelMapperUtil.map(advertis, AdvertiseRespDTO.class);

        if (!ObjectUtils.isEmpty(advertisRespDTO.getAdvertiseBusinessPaymentTypes())) {
            advertisRespDTO.setAdvertiseBusinessPaymentTypeSet(Arrays.stream(advertisRespDTO.getAdvertiseBusinessPaymentTypes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).map(Integer::parseInt).collect(Collectors.toSet()));
        }

        return new Result<>(advertisRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAdvertiseByAdvertiseCode(GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO) {

        String advertiseCode = getAdvertiseByAdvertiseCodeReqDTO.getAdvertiseCode();
        Predicate predicate = QAdvertise.advertise.advertiseCode.eq(advertiseCode);
        boolean b = advertiseService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<AdvertiseRespDTO> getMatchAdvertise(GetMatchAdvertiseReqDTO getMatchAdvertiseReqDTO) {

        //???????????????????????????????????????????????????????????????????????????????????????????????????
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QAdvertise.advertise.advertiseCoinCode.eq(getMatchAdvertiseReqDTO.getAdvertiseCoinCode()));
        booleanBuilder.and(QAdvertise.advertise.advertiseBuySellType.eq(getMatchAdvertiseReqDTO.getAdvertiseBuySellType()));
        booleanBuilder.and(QAdvertise.advertise.advertiseAvailableAmount.goe(getMatchAdvertiseReqDTO.getAdvertiseAmount()));
        booleanBuilder.and(QAdvertise.advertise.status.eq(AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()));
        booleanBuilder.and(QAdvertise.advertise.advertiseBusinessPaymentTypes.contains(String.valueOf(getMatchAdvertiseReqDTO.getAdvertiseBusinessPaymentType())));

        booleanBuilder.and(QAdvertise.advertise.advertisePriceType.eq(AdvertisePriceTypeEnum.FIXED.getCode()));
        if (getMatchAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            //??????????????????????????????????????????????????????????????????
            booleanBuilder.and(QAdvertise.advertise.advertiseFixedPrice.loe(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice()));
        } else {
            //??????????????????????????????????????????????????????????????????
            booleanBuilder.and(QAdvertise.advertise.advertiseFixedPrice.goe(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice()));
        }

        OrderSpecifier orderSpecifier = null;
        if (getMatchAdvertiseReqDTO.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            //??????????????????????????????????????????????????????????????????
            orderSpecifier = QAdvertise.advertise.advertiseFixedPrice.asc();
        } else {
            //??????????????????????????????????????????????????????????????????
            orderSpecifier = QAdvertise.advertise.advertiseFixedPrice.desc();
        }

        Advertise advertis = advertiseService.getJpaQueryFactory().selectFrom(QAdvertise.advertise).where(booleanBuilder).orderBy(orderSpecifier).fetchFirst();

        //?????????????????????????????????????????????
        BooleanBuilder premiumBooleanBuilder = new BooleanBuilder();
        premiumBooleanBuilder.and(QAdvertise.advertise.advertiseCoinCode.eq(getMatchAdvertiseReqDTO.getAdvertiseCoinCode()));
        premiumBooleanBuilder.and(QAdvertise.advertise.advertiseBuySellType.eq(getMatchAdvertiseReqDTO.getAdvertiseBuySellType()));
        premiumBooleanBuilder.and(QAdvertise.advertise.advertiseAvailableAmount.goe(getMatchAdvertiseReqDTO.getAdvertiseAmount()));
        premiumBooleanBuilder.and(QAdvertise.advertise.status.eq(AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()));
        premiumBooleanBuilder.and(QAdvertise.advertise.advertiseBusinessPaymentTypes.contains(String.valueOf(getMatchAdvertiseReqDTO.getAdvertiseBusinessPaymentType())));
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (advertis != null) {

            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

            if (advertis.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode() && advertis.getAdvertiseFixedPrice().compareTo(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice()) < 0) {
                BigDecimal advertisePremiumRate = getMatchAdvertiseReqDTO.getAdvertiseFixedPrice().subtract(advertis.getAdvertiseFixedPrice()).divide(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice(), 8, BigDecimal.ROUND_HALF_UP);
                premiumBooleanBuilder.and(QAdvertise.advertise.advertisePremiumRate.goe(advertisePremiumRate));
            }
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

            else if (advertis.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.BUY.getCode() && advertis.getAdvertiseFixedPrice().compareTo(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice()) > 0) {
                BigDecimal advertisePremiumRate = advertis.getAdvertiseFixedPrice().subtract(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice()).divide(getMatchAdvertiseReqDTO.getAdvertiseFixedPrice(), 8, BigDecimal.ROUND_HALF_UP);
                premiumBooleanBuilder.and(QAdvertise.advertise.advertisePremiumRate.goe(advertisePremiumRate));

            }

        }
        OrderSpecifier premiumOrderSpecifier = QAdvertise.advertise.advertiseFixedPrice.desc();
        Advertise advertis2 = advertiseService.getJpaQueryFactory().selectFrom(QAdvertise.advertise).where(premiumBooleanBuilder).orderBy(premiumOrderSpecifier).fetchFirst();

        Advertise advertise3 = null;
        if (advertis2 != null) {
            //??????????????????????????????????????????????????????????????????
            advertise3 = advertis2;
        } else if (advertis != null) {
            //??????????????????????????????????????????????????????????????????????????????
            advertise3 = advertis;
        } else {
            advertise3 = null;
        }

        //??????
        AdvertiseRespDTO advertisRespDTO = ModelMapperUtil.map(advertise3, AdvertiseRespDTO.class);

        return new Result<>(advertisRespDTO);
    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addAdvertise(AddAdvertiseReqDTO addAdvertiseReqDTO) {
        Advertise advertise = ModelMapperUtil.map(addAdvertiseReqDTO, Advertise.class);
        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //?????????id
        advertise.setId(IdUtil.generateLongId());
        //??????????????????
        if (!ObjectUtils.isEmpty(advertise.getAdvertiseCode())) {
            //???????????????
            String advertiseCode = addAdvertiseReqDTO.getAdvertiseCode();
            GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
            getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
            boolean b = advertiseService.existsAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ADVERTISE_EXISTS);
            }
            advertise.setAdvertiseCode(addAdvertiseReqDTO.getAdvertiseCode());
        } else {
            //????????????????????????
            advertise.setAdvertiseCode(advertise.getId() + "");
        }
        //????????????
        int advertiseBuySellType = addAdvertiseReqDTO.getAdvertiseBuySellType();
        if (advertiseBuySellType != AdvertiseBuySellTypeEnum.BUY.getCode() && advertiseBuySellType != AdvertiseBuySellTypeEnum.SELL.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUY_SELL_TYPE_NOT_EXISTS);
        }

        //?????????????????????????????????
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(username);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        advertise.setAdvertiseLegalCurrencyCountryCode(advertiseBusinessRespDTO.getCountryCode());
        advertise.setAdvertiseLegalCurrencyCountryName(advertiseBusinessRespDTO.getCountryName());
        advertise.setAdvertiseLegalCurrencyCode(advertiseBusinessRespDTO.getLegalCurrencyCode());
        advertise.setAdvertiseLegalCurrencyName(advertiseBusinessRespDTO.getLegalCurrencyName());
        advertise.setAdvertiseLegalCurrencySymbol(advertiseBusinessRespDTO.getCountryCode());
        advertise.setAdvertiseLegalCurrencyUnit(advertiseBusinessRespDTO.getLegalCurrencyUnit());

        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        String advertiseCoinCode = addAdvertiseReqDTO.getAdvertiseCoinCode();
        getCoinByCoinCodeReqDTO.setCoinCode(advertiseCoinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        if (coinRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }
        if (coinRespDTO.getStatus() != CoinStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_NOT_ENABLE);
        }
        advertise.setAdvertiseCoinName(coinRespDTO.getCoinName());
        advertise.setAdvertiseCoinUnit(coinRespDTO.getCoinUnit());
        advertise.setAdvertiseCoinRate(coinRespDTO.getCoinRate());

        //?????????????????????
        if (addAdvertiseReqDTO.getAdvertiseAvailableAmount().compareTo(coinRespDTO.getPerAdvertiseMinAmount()) < 0 || addAdvertiseReqDTO.getAdvertiseAvailableAmount().compareTo(coinRespDTO.getPerAdvertiseMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
        }
        //???????????????????????????????????????????????????????????????
        advertise.setAdvertiseTotalAmount(addAdvertiseReqDTO.getAdvertiseAvailableAmount());
        advertise.setAdvertiseAvailableAmount(addAdvertiseReqDTO.getAdvertiseAvailableAmount());
        advertise.setAdvertiseFrozenAmount(BigDecimal.ZERO);

        //???????????????????????????????????????????????????
        int advertisePriceType = addAdvertiseReqDTO.getAdvertisePriceType();
        if (advertisePriceType != AdvertisePriceTypeEnum.FIXED.getCode() && advertisePriceType != AdvertisePriceTypeEnum.PREMIUM.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_PRICE_TYPE_NOT_EXISTS);
        }
        advertise.setAdvertisePriceType(advertisePriceType);
        //????????????????????????????????????,????????????????????????????????????
        if (advertisePriceType == AdvertisePriceTypeEnum.FIXED.getCode()) {
            if (addAdvertiseReqDTO.getAdvertiseFixedPrice() == null || addAdvertiseReqDTO.getAdvertiseFixedPrice().compareTo(BigDecimal.ZERO) < 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_FIXED_PRICE_ERROR);
            }
            advertise.setAdvertiseFixedPrice(addAdvertiseReqDTO.getAdvertiseFixedPrice());
        } else {
            if (addAdvertiseReqDTO.getAdvertisePremiumRate() == null || addAdvertiseReqDTO.getAdvertisePremiumRate().compareTo(BigDecimal.ZERO) < 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_PREMIUM_RATE_ERROR);
            }
            advertise.setAdvertisePremiumRate(addAdvertiseReqDTO.getAdvertisePremiumRate());
        }

        //?????????????????????
        ListPaymentForClientReqDTO listPaymentForClientReqDTO = new ListPaymentForClientReqDTO();
        listPaymentForClientReqDTO.setUsername(currentLoginUser.getUsername());
        List<PaymentRespDTO> list = paymentService.listPaymentForClient(listPaymentForClientReqDTO).getData();
        Set<PaymentRespDTO> set = list.stream()
                .filter(e -> addAdvertiseReqDTO.getAdvertiseBusinessPaymentTypeSet().contains(e.getPaymentType()))
                .collect(Collectors.toSet());
        if (set.isEmpty()) {
            return new Result<>(ResultCodeEnum.PAYMENT_TYPE_ARRAY_NOT_EMPTY);
        }
        //?????????????????????
        advertise.setAdvertiseBusinessPaymentCodes(set.stream().map(e -> e.getPaymentCode()).collect(Collectors.joining(",")));
        advertise.setAdvertiseBusinessPaymentTypes(set.stream().map(e -> e.getPaymentType()).collect(Collectors.toSet()).stream().map(e -> String.valueOf(e)).collect(Collectors.joining(",")));

        //??????????????????
        advertise.setAdvertiseAutoReplyContent(addAdvertiseReqDTO.getAdvertiseAutoReplyContent());
        //?????????????????????????????????
        advertise.setStatus(AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());
        //??????????????????
        advertise.setAdvertiseBusinessUsername(currentLoginUser.getUsername());
        advertise.setAdvertiseBusinessNickname(currentLoginUser.getNickname());
        advertise.setAdvertiseBusinessRealname(currentLoginUser.getRealname());
        //????????????????????????
        advertise.setCreateTime(new Date());
        //?????????????????????
        advertiseService.add(advertise);

        //?????????????????????
        AddAdvertiseEvent addAdvertiseEvent = new AddAdvertiseEvent();
        addAdvertiseEvent.setAdvertiseCode(advertise.getAdvertiseCode());
        addAdvertiseEvent.setAdvertiseLogType(AdvertiseLogTypeEnum.ADD_ADVERTISE.getCode());
        addAdvertiseEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        addAdvertiseEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        addAdvertiseEvent.setCreateTime(new Date());
        addAdvertiseEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(addAdvertiseEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    //TODO ??????????????????????????????????????????????????????????????????
    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> putOnShelves(PutOnShelvesReqDTO putOnShelvesReqDTO) {
        //TODO ??????????????????????????????
        String advertiseCode = putOnShelvesReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);

        //???????????????????????????????????????
        if (afterAdvertise.getStatus() != AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_STATUS_IS_NOT_PUT_OFF_SHELVES);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //??????????????????????????????
        if (!username.equals(afterAdvertise.getAdvertiseBusinessUsername())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //????????????
        int advertiseBuySellType = putOnShelvesReqDTO.getAdvertiseBuySellType();
        if (advertiseBuySellType != AdvertiseBuySellTypeEnum.BUY.getCode() && advertiseBuySellType != AdvertiseBuySellTypeEnum.SELL.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUY_SELL_TYPE_NOT_EXISTS);
        }
        //?????????????????????????????????
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(username);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        afterAdvertise.setAdvertiseLegalCurrencyCountryCode(advertiseBusinessRespDTO.getCountryCode());
        afterAdvertise.setAdvertiseLegalCurrencyCountryName(advertiseBusinessRespDTO.getCountryName());
        afterAdvertise.setAdvertiseLegalCurrencyCode(advertiseBusinessRespDTO.getLegalCurrencyCode());
        afterAdvertise.setAdvertiseLegalCurrencyName(advertiseBusinessRespDTO.getLegalCurrencyName());
        afterAdvertise.setAdvertiseLegalCurrencySymbol(advertiseBusinessRespDTO.getCountryCode());
        afterAdvertise.setAdvertiseLegalCurrencyUnit(advertiseBusinessRespDTO.getLegalCurrencyUnit());

        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        String advertiseCoinCode = putOnShelvesReqDTO.getAdvertiseCoinCode();
        getCoinByCoinCodeReqDTO.setCoinCode(advertiseCoinCode);
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();
        if (coinRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }
        if (coinRespDTO.getStatus() != CoinStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_NOT_ENABLE);
        }
        afterAdvertise.setAdvertiseCoinName(coinRespDTO.getCoinName());
        afterAdvertise.setAdvertiseCoinUnit(coinRespDTO.getCoinUnit());
        afterAdvertise.setAdvertiseCoinRate(coinRespDTO.getCoinRate());

        //????????????????????????????????????????????????
        if (putOnShelvesReqDTO.getAdvertiseAvailableAmount().compareTo(coinRespDTO.getPerAdvertiseMinAmount()) < 0 || putOnShelvesReqDTO.getAdvertiseAvailableAmount().compareTo(coinRespDTO.getPerAdvertiseMaxAmount()) > 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_ERROR);
        }
        //??????????????????????????????????????????????????????
        if (advertiseBuySellType == AdvertiseBuySellTypeEnum.SELL.getCode()) {

            GetAccountByUsernameAndCoinCodeReqDTO getAccountByUsernameAndCoinCodeReqDTO = new GetAccountByUsernameAndCoinCodeReqDTO();
            getAccountByUsernameAndCoinCodeReqDTO.setUsername(username);
            getAccountByUsernameAndCoinCodeReqDTO.setCoinCode(advertiseCoinCode);
            AccountRespDTO accountRespDTO = accountService.getAccountByUsernameAndCoinCode(getAccountByUsernameAndCoinCodeReqDTO).getData();

            if (putOnShelvesReqDTO.getAdvertiseAvailableAmount().compareTo(accountRespDTO.getAvailableBalance()) > 0) {
                return new Result<>(ResultCodeEnum.ACCOUNT_BALANCE_NOT_ENOUGH);
            }
        }

        //??????????????????
        FreezeBalanceForClientReqDTO freezeBalanceReqDTO = new FreezeBalanceForClientReqDTO();
        freezeBalanceReqDTO.setUsername(username);
        freezeBalanceReqDTO.setCoinCode(advertiseCoinCode);
        if (advertiseBuySellType == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            BigDecimal balance = putOnShelvesReqDTO.getAdvertiseAvailableAmount().multiply(BigDecimal.ONE.add(afterAdvertise.getAdvertiseCoinRate()));
            freezeBalanceReqDTO.setBalance(balance);
            freezeBalanceReqDTO.setRemark("???????????????????????????????????????????????????(??????????????????????????????)");
        } else {
            BigDecimal balance = putOnShelvesReqDTO.getAdvertiseAvailableAmount().multiply(afterAdvertise.getAdvertiseCoinRate());
            freezeBalanceReqDTO.setBalance(balance);
            freezeBalanceReqDTO.setRemark("???????????????????????????????????????????????????(?????????)");
        }
        Result freezeBalanceResult = accountService.freezeBalanceForClient(freezeBalanceReqDTO);
        if (freezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(freezeBalanceResult.getCode(), freezeBalanceResult.getMsg());
        }

        //???????????????????????????????????????????????????????????????
        afterAdvertise.setAdvertiseTotalAmount(putOnShelvesReqDTO.getAdvertiseAvailableAmount());
        afterAdvertise.setAdvertiseAvailableAmount(putOnShelvesReqDTO.getAdvertiseAvailableAmount());
        afterAdvertise.setAdvertiseFrozenAmount(BigDecimal.ZERO);

        //???????????????????????????????????????????????????
        int advertisePriceType = putOnShelvesReqDTO.getAdvertisePriceType();
        if (advertisePriceType != AdvertisePriceTypeEnum.FIXED.getCode() && advertisePriceType != AdvertisePriceTypeEnum.PREMIUM.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_PRICE_TYPE_NOT_EXISTS);
        }
        afterAdvertise.setAdvertisePriceType(advertisePriceType);
        //????????????????????????????????????,????????????????????????????????????
        if (advertisePriceType == AdvertisePriceTypeEnum.FIXED.getCode()) {
            if (putOnShelvesReqDTO.getAdvertiseFixedPrice() == null || putOnShelvesReqDTO.getAdvertiseFixedPrice().compareTo(BigDecimal.ZERO) < 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_FIXED_PRICE_ERROR);
            }
            afterAdvertise.setAdvertiseFixedPrice(putOnShelvesReqDTO.getAdvertiseFixedPrice());
        } else {
            if (putOnShelvesReqDTO.getAdvertisePremiumRate() == null || putOnShelvesReqDTO.getAdvertisePremiumRate().compareTo(BigDecimal.ZERO) < 0) {
                return new Result<>(ResultCodeEnum.ADVERTISE_PREMIUM_RATE_ERROR);
            }
            afterAdvertise.setAdvertisePremiumRate(putOnShelvesReqDTO.getAdvertisePremiumRate());
        }

        //?????????????????????
        ListPaymentForClientReqDTO listPaymentForClientReqDTO = new ListPaymentForClientReqDTO();
        listPaymentForClientReqDTO.setUsername(currentLoginUser.getUsername());
        List<PaymentRespDTO> list = paymentService.listPaymentForClient(listPaymentForClientReqDTO).getData();
        Set<PaymentRespDTO> set = list.stream()
                .filter(e -> putOnShelvesReqDTO.getAdvertiseBusinessPaymentTypeSet().contains(e.getPaymentType()))
                .collect(Collectors.toSet());
        if (set.isEmpty()) {
            return new Result<>(ResultCodeEnum.PAYMENT_TYPE_ARRAY_NOT_EMPTY);
        }
        //?????????????????????
        afterAdvertise.setAdvertiseBusinessPaymentCodes(set.stream().map(e -> e.getPaymentCode()).collect(Collectors.joining(",")));
        afterAdvertise.setAdvertiseBusinessPaymentTypes(set.stream().map(e -> e.getPaymentType()).collect(Collectors.toSet()).stream().map(e -> String.valueOf(e)).collect(Collectors.joining(",")));

        //??????????????????
        afterAdvertise.setAdvertiseAutoReplyContent(putOnShelvesReqDTO.getAdvertiseAutoReplyContent());
        //?????????????????????????????????
        afterAdvertise.setStatus(AdvertiseStatusEnum.PUT_ON_SHELVES.getCode());
        //??????????????????
        afterAdvertise.setAdvertiseBusinessUsername(currentLoginUser.getUsername());
        afterAdvertise.setAdvertiseBusinessNickname(currentLoginUser.getNickname());
        //????????????????????????
        afterAdvertise.setCreateTime(new Date());
        //?????????????????????
        advertiseService.update(afterAdvertise);

        //?????????????????????
        PutOnShelvesEvent putOnShelvesEvent = new PutOnShelvesEvent();
        putOnShelvesEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        putOnShelvesEvent.setAdvertiseLogType(AdvertiseLogTypeEnum.PUT_ON_SHELVES.getCode());
        putOnShelvesEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        putOnShelvesEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        putOnShelvesEvent.setCreateTime(new Date());
        putOnShelvesEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(putOnShelvesEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    //TODO ?????????????????????
    //TODO ??????????????????????????????????????????????????????????????????
    //TODO ?????????????????????????????????????????????????????????????????????????????????????????????????????????

    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> putOffShelves(PutOffShelvesReqDTO putOffShelvesReqDTO) {
        //TODO ??????????????????????????????
        String advertiseCode = putOffShelvesReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);

        //???????????????????????????????????????
        if (afterAdvertise.getStatus() != AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_STATUS_IS_NOT_PUT_ON_SHELVES);
        }
        //?????????????????????0???????????????????????????(????????????????????????????????????)
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) != 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_IS_NOT_ZERO);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //??????????????????????????????
        if (!username.equals(afterAdvertise.getAdvertiseBusinessUsername())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //?????????????????????????????????
        afterAdvertise.setStatus(AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //?????????????????????
        advertiseService.update(afterAdvertise);

        //??????????????????
        UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
        unFreezeBalanceReqDTO.setUsername(username);
        unFreezeBalanceReqDTO.setCoinCode(afterAdvertise.getAdvertiseCoinCode());
        if (afterAdvertise.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            BigDecimal balance = afterAdvertise.getAdvertiseTotalAmount().multiply(BigDecimal.ONE.add(afterAdvertise.getAdvertiseCoinRate()));
            unFreezeBalanceReqDTO.setBalance(balance);
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(???????????????????????????????????????)");
        } else {
            BigDecimal balance = afterAdvertise.getAdvertiseTotalAmount().multiply(afterAdvertise.getAdvertiseCoinRate());
            unFreezeBalanceReqDTO.setBalance(balance);
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(?????????)");
        }
        Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
        if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
        }

        //?????????????????????
        PutOffShelvesEvent putOffShelvesEvent = new PutOffShelvesEvent();
        putOffShelvesEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        putOffShelvesEvent.setAdvertiseLogType(AdvertiseLogTypeEnum.PUT_OFF_SHELVES.getCode());
        putOffShelvesEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        putOffShelvesEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        putOffShelvesEvent.setCreateTime(new Date());
        putOffShelvesEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(putOffShelvesEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> putOffShelvesForClient(PutOffShelvesForClientReqDTO putOffShelvesForClientReqDTO) {
        //TODO ??????????????????????????????
        String advertiseCode = putOffShelvesForClientReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);

        //???????????????????????????????????????
        if (afterAdvertise.getStatus() != AdvertiseStatusEnum.PUT_ON_SHELVES.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_STATUS_IS_NOT_PUT_ON_SHELVES);
        }
        //?????????????????????0???????????????????????????
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(BigDecimal.ZERO) != 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_IS_NOT_ZERO);
        }

        //????????????????????????
        GetCoinByCoinCodeReqDTO getCoinByCoinCodeReqDTO = new GetCoinByCoinCodeReqDTO();
        getCoinByCoinCodeReqDTO.setCoinCode(advertiseRespDTO.getAdvertiseCoinCode());
        CoinRespDTO coinRespDTO = coinService.getCoinByCoinCode(getCoinByCoinCodeReqDTO).getData();

        //????????????????????????????????????????????????????????????????????????
        if (afterAdvertise.getAdvertiseAvailableAmount().compareTo(coinRespDTO.getPerAdvertiseMinAmount()) >= 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AVAILABLE_AMOUNT_LEGAL);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        //?????????????????????????????????
        afterAdvertise.setStatus(AdvertiseStatusEnum.PUT_OFF_SHELVES.getCode());

        //?????????????????????
        advertiseService.update(afterAdvertise);

        //??????????????????
        UnFreezeBalanceForClientReqDTO unFreezeBalanceReqDTO = new UnFreezeBalanceForClientReqDTO();
        unFreezeBalanceReqDTO.setUsername(advertiseRespDTO.getAdvertiseBusinessUsername());
        unFreezeBalanceReqDTO.setCoinCode(afterAdvertise.getAdvertiseCoinCode());
        if (afterAdvertise.getAdvertiseBuySellType() == AdvertiseBuySellTypeEnum.SELL.getCode()) {
            BigDecimal balance = afterAdvertise.getAdvertiseTotalAmount().multiply(BigDecimal.ONE.add(afterAdvertise.getAdvertiseCoinRate()));
            unFreezeBalanceReqDTO.setBalance(balance);
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(???????????????????????????????????????)");
        } else {
            BigDecimal balance = afterAdvertise.getAdvertiseTotalAmount().multiply(afterAdvertise.getAdvertiseCoinRate());
            unFreezeBalanceReqDTO.setBalance(balance);
            unFreezeBalanceReqDTO.setRemark("??????????????????????????????????????????????????????(?????????)");
        }
        Result unFreezeBalanceResult = accountService.unFreezeBalanceForClient(unFreezeBalanceReqDTO);
        if (unFreezeBalanceResult.getCode() != ResultCodeEnum.OK.getCode()) {
            throw new ResultCodeException(unFreezeBalanceResult.getCode(), unFreezeBalanceResult.getMsg());
        }

        //?????????????????????
        PutOffShelvesEvent putOffShelvesEvent = new PutOffShelvesEvent();
        putOffShelvesEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        putOffShelvesEvent.setAdvertiseLogType(AdvertiseLogTypeEnum.PUT_OFF_SHELVES.getCode());
        putOffShelvesEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        putOffShelvesEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        putOffShelvesEvent.setCreateTime(new Date());
        putOffShelvesEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(putOffShelvesEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> deleteAdvertise(DeleteAdvertiseReqDTO deleteAdvertiseReqDTO) {
        String advertiseCode = deleteAdvertiseReqDTO.getAdvertiseCode();
        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        String username = currentLoginUser.getUsername();

        //??????????????????????????????
        if (!username.equals(afterAdvertise.getAdvertiseBusinessUsername())) {
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //?????????????????????????????????
        afterAdvertise.setStatus(AdvertiseStatusEnum.DELETED.getCode());

        //?????????????????????
        advertiseService.update(afterAdvertise);

        //?????????????????????

        //?????????????????????
        DeleteAdvertiseEvent deleteAdvertiseEvent = new DeleteAdvertiseEvent();
        deleteAdvertiseEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        deleteAdvertiseEvent.setAdvertiseLogType(AdvertiseLogTypeEnum.DELETE_ADVERTISE.getCode());
        deleteAdvertiseEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        deleteAdvertiseEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        deleteAdvertiseEvent.setCreateTime(new Date());
        deleteAdvertiseEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(deleteAdvertiseEvent);

        return new Result<>(ResultCodeEnum.OK);

    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> freezeAdvertiseAmount(FreezeAdvertiseAmountReqDTO freezeAdvertiseAmountReqDTO) {
        String advertiseCode = freezeAdvertiseAmountReqDTO.getAdvertiseCode();

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);
        if (afterAdvertise.getAdvertiseAvailableAmount().compareTo(freezeAdvertiseAmountReqDTO.getAdvertiseAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_AMOUNT_NOT_ENOUGH);

        }
        afterAdvertise.setAdvertiseAvailableAmount(afterAdvertise.getAdvertiseAvailableAmount().subtract(freezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        afterAdvertise.setAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount().add(freezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        //?????????????????????
        advertiseService.update(afterAdvertise);
        //?????????????????????
        FreezeAdvertiseAmountEvent freezeAdvertiseAmountEvent = new FreezeAdvertiseAmountEvent();
        freezeAdvertiseAmountEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        freezeAdvertiseAmountEvent.setAdvertiseAmountLogType(AdvertiseAmountLogTypeEnum.FREE_ADVERTISE_AMOUNT.getCode());

        freezeAdvertiseAmountEvent.setBeforeAdvertiseTotalAmount(advertiseRespDTO.getAdvertiseTotalAmount());
        freezeAdvertiseAmountEvent.setBeforeAdvertiseAvailableAmount(advertiseRespDTO.getAdvertiseAvailableAmount());
        freezeAdvertiseAmountEvent.setBeforeAdvertiseFrozenAmount(advertiseRespDTO.getAdvertiseFrozenAmount());
        freezeAdvertiseAmountEvent.setAfterAdvertiseTotalAmount(afterAdvertise.getAdvertiseTotalAmount());
        freezeAdvertiseAmountEvent.setAfterAdvertiseAvailableAmount(afterAdvertise.getAdvertiseAvailableAmount());
        freezeAdvertiseAmountEvent.setAfterAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount());
        freezeAdvertiseAmountEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        freezeAdvertiseAmountEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        freezeAdvertiseAmountEvent.setCreateTime(new Date());
        freezeAdvertiseAmountEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(freezeAdvertiseAmountEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> unFreezeAdvertiseAmount(UnFreezeAdvertiseAmountReqDTO unFreezeAdvertiseAmountReqDTO) {
        String advertiseCode = unFreezeAdvertiseAmountReqDTO.getAdvertiseCode();
        if (ObjectUtils.isEmpty(advertiseCode)) {
            return new Result<>(ResultCodeEnum.ADVERTISE_CODE_CAN_NOT_EMPTY);
        }

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_NOT_ENOUGH);

        }
        afterAdvertise.setAdvertiseAvailableAmount(afterAdvertise.getAdvertiseAvailableAmount().add(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        afterAdvertise.setAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount().subtract(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount()));
        //?????????????????????
        advertiseService.update(afterAdvertise);

        //?????????????????????
        UnFreezeAdvertiseAmountEvent unFreezeAdvertiseAmountEvent = new UnFreezeAdvertiseAmountEvent();
        unFreezeAdvertiseAmountEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        unFreezeAdvertiseAmountEvent.setAdvertiseAmountLogType(AdvertiseAmountLogTypeEnum.UN_FREE_ADVERTISE_AMOUNT.getCode());
        unFreezeAdvertiseAmountEvent.setAmount(unFreezeAdvertiseAmountReqDTO.getAdvertiseAmount());
        unFreezeAdvertiseAmountEvent.setBeforeAdvertiseTotalAmount(advertiseRespDTO.getAdvertiseTotalAmount());
        unFreezeAdvertiseAmountEvent.setBeforeAdvertiseAvailableAmount(advertiseRespDTO.getAdvertiseAvailableAmount());
        unFreezeAdvertiseAmountEvent.setBeforeAdvertiseFrozenAmount(advertiseRespDTO.getAdvertiseFrozenAmount());
        unFreezeAdvertiseAmountEvent.setAfterAdvertiseTotalAmount(afterAdvertise.getAdvertiseTotalAmount());
        unFreezeAdvertiseAmountEvent.setAfterAdvertiseAvailableAmount(afterAdvertise.getAdvertiseAvailableAmount());
        unFreezeAdvertiseAmountEvent.setAfterAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount());
        unFreezeAdvertiseAmountEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        unFreezeAdvertiseAmountEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        unFreezeAdvertiseAmountEvent.setCreateTime(new Date());
        unFreezeAdvertiseAmountEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(unFreezeAdvertiseAmountEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Advertise", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> decAdvertiseFrozenAmount(DecAdvertiseFrozenAmountReqDTO decAdvertiseFrozenAmountReqDTO) {
        String advertiseCode = decAdvertiseFrozenAmountReqDTO.getAdvertiseCode();

        //?????????????????????????????????
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();
        //??????????????????????????????
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO = new GetAdvertiseByAdvertiseCodeReqDTO();
        getAdvertiseByAdvertiseCodeReqDTO.setAdvertiseCode(advertiseCode);
        AdvertiseRespDTO advertiseRespDTO = advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO).getData();
        Advertise afterAdvertise = ModelMapperUtil.map(advertiseRespDTO, Advertise.class);
        if (afterAdvertise.getAdvertiseFrozenAmount().compareTo(decAdvertiseFrozenAmountReqDTO.getAdvertiseAmount()) < 0) {
            return new Result<>(ResultCodeEnum.ADVERTISE_FROZEN_AMOUNT_NOT_ENOUGH);

        }
        afterAdvertise.setAdvertiseTotalAmount(afterAdvertise.getAdvertiseTotalAmount().subtract(decAdvertiseFrozenAmountReqDTO.getAdvertiseAmount()));
        afterAdvertise.setAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount().subtract(decAdvertiseFrozenAmountReqDTO.getAdvertiseAmount()));
        //?????????????????????
        advertiseService.update(afterAdvertise);
        //?????????????????????
        DecAdvertiseFrozenAmountEvent freezeAdvertiseAmountEvent = new DecAdvertiseFrozenAmountEvent();
        freezeAdvertiseAmountEvent.setAdvertiseCode(afterAdvertise.getAdvertiseCode());
        freezeAdvertiseAmountEvent.setAdvertiseAmountLogType(AdvertiseAmountLogTypeEnum.DEC_ADVERTISE_FROZEN_AMOUNT.getCode());
        freezeAdvertiseAmountEvent.setAmount(decAdvertiseFrozenAmountReqDTO.getAdvertiseAmount());
        freezeAdvertiseAmountEvent.setBeforeAdvertiseTotalAmount(advertiseRespDTO.getAdvertiseTotalAmount());
        freezeAdvertiseAmountEvent.setBeforeAdvertiseAvailableAmount(advertiseRespDTO.getAdvertiseAvailableAmount());
        freezeAdvertiseAmountEvent.setBeforeAdvertiseFrozenAmount(advertiseRespDTO.getAdvertiseFrozenAmount());
        freezeAdvertiseAmountEvent.setAfterAdvertiseTotalAmount(afterAdvertise.getAdvertiseTotalAmount());
        freezeAdvertiseAmountEvent.setAfterAdvertiseAvailableAmount(afterAdvertise.getAdvertiseAvailableAmount());
        freezeAdvertiseAmountEvent.setAfterAdvertiseFrozenAmount(afterAdvertise.getAdvertiseFrozenAmount());
        freezeAdvertiseAmountEvent.setAdvertiseLogClientId(currentLoginClient.getClientId());
        freezeAdvertiseAmountEvent.setAdvertiseLogUsername(currentLoginUser.getUsername());
        freezeAdvertiseAmountEvent.setCreateTime(new Date());
        freezeAdvertiseAmountEvent.setAdvertiseLogIpAddr(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(freezeAdvertiseAmountEvent);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @CacheEvict(cacheNames = "Order", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> deleteAllAdvertiseForAdmin() {
        //TODO ??????????????????????????????
        //?????????????????????10??????
        advertiseService.getJpaQueryFactory()
                .delete(QAdvertise.advertise)
                .execute();
        return new Result<>(ResultCodeEnum.OK);
    }
}