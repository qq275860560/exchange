package com.ghf.exchange.otc.advertisebusiness.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.advertisebusiness.dto.*;
import com.ghf.exchange.otc.advertisebusiness.entity.AdvertiseBusiness;
import com.ghf.exchange.otc.advertisebusiness.entity.QAdvertiseBusiness;
import com.ghf.exchange.otc.advertisebusiness.enums.AdvertiseBusinessKycEnum;
import com.ghf.exchange.otc.advertisebusiness.enums.AdvertiseBusinessStatusEnum;
import com.ghf.exchange.otc.advertisebusiness.repository.AdvertiseBusinessRepository;
import com.ghf.exchange.otc.advertisebusiness.service.AdvertiseBusinessService;
import com.ghf.exchange.otc.legalcurrency.dto.GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO;
import com.ghf.exchange.otc.legalcurrency.dto.LegalCurrencyRespDTO;
import com.ghf.exchange.otc.legalcurrency.service.LegalCurrencyService;
import com.ghf.exchange.otc.order.enums.OrderBuySellTypeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class AdvertiseBusinessServiceImpl extends BaseServiceImpl<AdvertiseBusiness, Long> implements AdvertiseBusinessService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private AdvertiseBusinessService advertiseBusinessService;

    @Lazy
    @Resource
    private LegalCurrencyService legalCurrencyService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AdvertiseBusinessServiceImpl(AdvertiseBusinessRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseBusinessRespDTO>> pageAdvertiseBusiness(PageAdvertiseBusinessReqDTO pageAdvertiseBusinessReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageAdvertiseBusinessReqDTO.getAdvertiseBusinessCode())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(pageAdvertiseBusinessReqDTO.getAdvertiseBusinessCode()));
        }

        if (!ObjectUtils.isEmpty(pageAdvertiseBusinessReqDTO.getUsername())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.username.eq(pageAdvertiseBusinessReqDTO.getUsername()));
        }

        PageRespDTO<AdvertiseBusinessRespDTO> pageRespDTO = advertiseBusinessService.page(predicate, pageAdvertiseBusinessReqDTO, AdvertiseBusinessRespDTO.class);

        pageRespDTO.getList().forEach(advertiseBusinessRespDTO -> {
            if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月下单总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月放行总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //重置订单当日申诉次数为0
                advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
            }
        });

        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseBusinessRespDTO>> pageAdvertiseBusinessForAdmin(PageAdvertiseBusinessForAdminReqDTO pageAdvertiseBusinessForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(pageAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode()));
        }

        if (!ObjectUtils.isEmpty(pageAdvertiseBusinessForAdminReqDTO.getUsername())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.username.eq(pageAdvertiseBusinessForAdminReqDTO.getUsername()));
        }

        PageRespDTO<AdvertiseBusinessRespDTO> pageRespDTO = advertiseBusinessService.page(predicate, pageAdvertiseBusinessForAdminReqDTO, AdvertiseBusinessRespDTO.class);

        pageRespDTO.getList().forEach(advertiseBusinessRespDTO -> {
            if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月下单总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月放行总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //重置订单当日申诉次数为0
                advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
            }
        });
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusiness(ListAdvertiseBusinessReqDTO listAdvertiseBusinessReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listAdvertiseBusinessReqDTO.getAdvertiseBusinessCode())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(listAdvertiseBusinessReqDTO.getAdvertiseBusinessCode()));
        }

        if (!ObjectUtils.isEmpty(listAdvertiseBusinessReqDTO.getUsername())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.username.eq(listAdvertiseBusinessReqDTO.getUsername()));
        }

        predicate.and(QAdvertiseBusiness.advertiseBusiness.status.eq(AdvertiseBusinessStatusEnum.ENABLE.getCode()));

        List<AdvertiseBusinessRespDTO> list = advertiseBusinessService.list(predicate, AdvertiseBusinessRespDTO.class);

        list.forEach(advertiseBusinessRespDTO -> {
            if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月下单总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月放行总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //重置订单当日申诉次数为0
                advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
            }
        });

        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusinessForAdmin(ListAdvertiseBusinessForAdminReqDTO listAdvertiseBusinessForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(listAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(listAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode()));
        }

        if (!ObjectUtils.isEmpty(listAdvertiseBusinessForAdminReqDTO.getUsername())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.username.eq(listAdvertiseBusinessForAdminReqDTO.getUsername()));
        }
        predicate.and(QAdvertiseBusiness.advertiseBusiness.status.eq(AdvertiseBusinessStatusEnum.ENABLE.getCode()));

        List<AdvertiseBusinessRespDTO> list = advertiseBusinessService.list(predicate, AdvertiseBusinessRespDTO.class);

        list.forEach(advertiseBusinessRespDTO -> {
            if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月下单总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月放行总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //重置订单当日申诉次数为0
                advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
            }
        });
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusinessForClient(ListAdvertiseBusinessForClientReqDTO listAdvertiseBusinessForClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listAdvertiseBusinessForClientReqDTO.getAdvertiseBusinessCode())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(listAdvertiseBusinessForClientReqDTO.getAdvertiseBusinessCode()));
        }

        if (!ObjectUtils.isEmpty(listAdvertiseBusinessForClientReqDTO.getUsername())) {
            predicate.and(QAdvertiseBusiness.advertiseBusiness.username.eq(listAdvertiseBusinessForClientReqDTO.getUsername()));
        }
        predicate.and(QAdvertiseBusiness.advertiseBusiness.status.eq(AdvertiseBusinessStatusEnum.ENABLE.getCode()));

        List<AdvertiseBusinessRespDTO> list = advertiseBusinessService.list(predicate, AdvertiseBusinessRespDTO.class);

        list.forEach(advertiseBusinessRespDTO -> {
            if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月下单总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //重置订单当月放行总数为0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //重置订单当日申诉次数为0
                advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
            }
        });

        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<AdvertiseBusinessRespDTO> getAdvertiseBusinessByAdvertiseBusinessCode(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {

        String advertiseBusinessCode = getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode();
        Predicate predicate = QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(advertiseBusinessCode);
        AdvertiseBusiness advertiseBusiness = advertiseBusinessService.get(predicate);

        //返回
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = ModelMapperUtil.map(advertiseBusiness, AdvertiseBusinessRespDTO.class);

        if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
            //重置订单当月下单总数为0
            advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
        }

        if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
            //重置订单当月放行总数为0
            advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
        }

        if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
            //重置订单当日申诉次数为0
            advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
        }

        return new Result<>(advertiseBusinessRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAdvertiseBusinessByAdvertiseBusinessCode(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {

        String advertiseBusinessCode = getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode();
        Predicate predicate = QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(advertiseBusinessCode);
        boolean b = advertiseBusinessService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Void> addAdvertiseBusinessForClient(AddAdvertiseBusinessForClientReqDTO addAdvertiseBusinessForAdminReqDTO) {
        AdvertiseBusiness advertiseBusiness = new AdvertiseBusiness();

        //初始化id
        advertiseBusiness.setId(IdUtil.generateLongId());
        //判断编号
        if (!ObjectUtils.isEmpty(addAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode())) {
            //判断唯一性
            String advertiseBusinessCode = addAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode();
            GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
            getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
            boolean b = advertiseBusinessService.existsAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_EXISTS);
            }
            advertiseBusiness.setAdvertiseBusinessCode(addAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode());
        } else {
            //自动生成编号
            advertiseBusiness.setAdvertiseBusinessCode(addAdvertiseBusinessForAdminReqDTO.getUsername() + "");
        }

        if (ObjectUtils.isEmpty(addAdvertiseBusinessForAdminReqDTO.getUsername())) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_USERNAME_EXISTS);
        }
        advertiseBusiness.setUsername(addAdvertiseBusinessForAdminReqDTO.getUsername());

        advertiseBusiness.setPassword(addAdvertiseBusinessForAdminReqDTO.getPassword());
        advertiseBusiness.setNickname(addAdvertiseBusinessForAdminReqDTO.getNickname());
        advertiseBusiness.setRealname(addAdvertiseBusinessForAdminReqDTO.getRealname());
        advertiseBusiness.setMobile(addAdvertiseBusinessForAdminReqDTO.getMobile());
        advertiseBusiness.setEmail(addAdvertiseBusinessForAdminReqDTO.getEmail());
        //设置国家信息和法币信息
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(addAdvertiseBusinessForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        advertiseBusiness.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        advertiseBusiness.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        advertiseBusiness.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        advertiseBusiness.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        advertiseBusiness.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        advertiseBusiness.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());
        //设置保证金
        advertiseBusiness.setDeposit(addAdvertiseBusinessForAdminReqDTO.getDeposit());

        //判断kyc
        if (addAdvertiseBusinessForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_1.getCode() && addAdvertiseBusinessForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_2.getCode() && addAdvertiseBusinessForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_3.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_KYC_NOT_EXISTS);
        }
        //设置kyc
        advertiseBusiness.setKyc(addAdvertiseBusinessForAdminReqDTO.getKyc());
        advertiseBusiness.setAdvertisePermission(addAdvertiseBusinessForAdminReqDTO.getAdvertisePermission());
        //设置状态
        advertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.ENABLE.getCode());

        advertiseBusiness.setCreateTime(new Date());

        //持久化到数据库
        advertiseBusinessService.add(advertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessByAdvertiseBusinessForAdminCode(UpdateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO) {
        String advertiseBusinessCode = updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getAdvertiseBusinessCode();
        //加载
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }
        //已禁用状态无权限
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        if (ObjectUtils.isEmpty(advertiseBusinessRespDTO.getUsername())) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_USERNAME_EXISTS);
        }
        afterAdvertiseBusiness.setUsername(advertiseBusinessRespDTO.getUsername());

        afterAdvertiseBusiness.setPassword(advertiseBusinessRespDTO.getPassword());
        afterAdvertiseBusiness.setNickname(advertiseBusinessRespDTO.getNickname());
        afterAdvertiseBusiness.setRealname(advertiseBusinessRespDTO.getRealname());
        afterAdvertiseBusiness.setMobile(advertiseBusinessRespDTO.getMobile());
        afterAdvertiseBusiness.setEmail(advertiseBusinessRespDTO.getEmail());

        //设置国家信息和法币信息
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        afterAdvertiseBusiness.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterAdvertiseBusiness.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        afterAdvertiseBusiness.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterAdvertiseBusiness.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        afterAdvertiseBusiness.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        afterAdvertiseBusiness.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());

        //设置保证金
        afterAdvertiseBusiness.setDeposit(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getDeposit());
        //判断kyc
        if (updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_1.getCode() && updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_2.getCode() && updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_3.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_KYC_NOT_EXISTS);
        }
        //设置kyc
        afterAdvertiseBusiness.setKyc(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc());
        afterAdvertiseBusiness.setAdvertisePermission(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getAdvertisePermission());

        //设置状态
        afterAdvertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.ENABLE.getCode());
        //更新到数据库
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> enableAdvertiseBusinessForAdmin(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        String advertiseBusinessCode = getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode();
        //加载
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //已启用状态无法启用
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_ENABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //设置状态
        afterAdvertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.ENABLE.getCode());

        //更新到数据库
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> disableAdvertiseBusinessForAdmin(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        String advertiseBusinessCode = getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode();
        //加载
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //已禁用状态无法禁用
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //设置状态
        afterAdvertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.DISABLE.getCode());

        //更新到数据库
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnPutOnShelvesForClient(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        advertiseBusinessService.getJpaQueryFactory()
                .update(QAdvertiseBusiness.advertiseBusiness)
                .set(QAdvertiseBusiness.advertiseBusiness.advertisePutOnShelvesCount, QAdvertiseBusiness.advertiseBusiness.advertisePutOnShelvesCount.add(1))
                .where(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(
                        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode()

                )).execute();
        return new Result<>(ResultCodeEnum.OK);
    }

    @Transactional
    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnPutOffShelvesForClient(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        advertiseBusinessService.getJpaQueryFactory()
                .update(QAdvertiseBusiness.advertiseBusiness)
                .set(QAdvertiseBusiness.advertiseBusiness.advertisePutOnShelvesCount, QAdvertiseBusiness.advertiseBusiness.advertisePutOnShelvesCount.subtract(1))
                .where(QAdvertiseBusiness.advertiseBusiness.advertiseBusinessCode.eq(
                        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode()
                )).execute();
        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnAddOrderEventForClient(UpdateAdvertiseBusinessOnAddOrderEventForClientReqDTO updateAdvertiseBusinessOnAddOrderEventForClientReqDTO) {
        String advertiseBusinessCode = updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getAdvertiseBusinessCode();
        //加载
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //已禁用
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //设置订单顾客下单次数
        afterAdvertiseBusiness.setOrderBuySellCount(advertiseBusinessRespDTO.getOrderBuySellCount() + 1);
        if (updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单次数
            afterAdvertiseBusiness.setOrderBuyCount(advertiseBusinessRespDTO.getOrderBuyCount() + 1);
        } else if (updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单次数
            afterAdvertiseBusiness.setOrderSellCount(advertiseBusinessRespDTO.getOrderSellCount() + 1);
        }
        //设置买总完成率=买币订单放行次数/订单顾客买币订单下单次数
        if (updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterAdvertiseBusiness.setOrderBuyReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount()).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyCount() + 1), 8, BigDecimal.ROUND_UP));
        }
        //设置总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数
        afterAdvertiseBusiness.setOrderBuySellReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + advertiseBusinessRespDTO.getOrderSellReleaseCount()).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuySellCount() + 1), 8, BigDecimal.ROUND_UP));

        //设置订单当月下单总数
        afterAdvertiseBusiness.setOrderMonthBuySellCount(advertiseBusinessRespDTO.getOrderMonthBuySellCount() + 1);

        //设置订单最后下单时间
        afterAdvertiseBusiness.setOrderLastAddTime(updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getAddTime());

//更新到数据库
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnReleaseOrderEventForClient(UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO) {
        String advertiseBusinessCode = updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getAdvertiseBusinessCode();
        //加载
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //已禁用
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单放行次数
            afterAdvertiseBusiness.setOrderBuyReleaseCount(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + 1);
        } else if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单放行次数
            afterAdvertiseBusiness.setOrderSellReleaseCount(advertiseBusinessRespDTO.getOrderSellReleaseCount() + 1);
        }

        //设置买总完成率=买币订单放行次数/订单顾客买币订单下单次数
        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterAdvertiseBusiness.setOrderBuyReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + 1).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyCount()), 8, BigDecimal.ROUND_UP));
        }
        //设置总完成率=(买币订单放行次数+卖币订单放行次数)/订单顾客下单次数
        afterAdvertiseBusiness.setOrderBuySellReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + advertiseBusinessRespDTO.getOrderSellReleaseCount() + 1).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuySellCount()), 8, BigDecimal.ROUND_UP));

        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单累计放行时间
            afterAdvertiseBusiness.setOrderBuyTotalReleaseTime(advertiseBusinessRespDTO.getOrderBuyTotalReleaseTime() + updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        } else if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单累计放行时间
            afterAdvertiseBusiness.setOrderSellTotalReleaseTime(advertiseBusinessRespDTO.getOrderSellTotalReleaseTime() + updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        }

        //设置订单平均放行时间
        afterAdvertiseBusiness.setOrderBuySellAvgReleaseTime((advertiseBusinessRespDTO.getOrderBuyTotalReleaseTime() + advertiseBusinessRespDTO.getOrderSellTotalReleaseTime() + updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getPayTime().getTime()) / advertiseBusinessRespDTO.getOrderBuySellCount());

        //创建时间也是当前月才计入
        if (new SimpleDateFormat("yyyy-MM").format(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime()))) {

            //设置订单当月放行总数
            afterAdvertiseBusiness.setOrderMonthBuySellReleaseCount(advertiseBusinessRespDTO.getOrderMonthBuySellReleaseCount() + 1);
            //设置订单当月放行比例
            afterAdvertiseBusiness.setOrderMonthBuySellReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderMonthBuySellReleaseCount() + 1).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderMonthBuySellCount()), 8, BigDecimal.ROUND_UP));

            //设置订单最后放行时间
            afterAdvertiseBusiness.setOrderLastReleaseTime(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime());

        }

        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //设置买币订单总额
            afterAdvertiseBusiness.setOrderBuyTotalPrice(afterAdvertiseBusiness.getOrderBuyTotalPrice().add(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderTotalPrice()));
        } else if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //设置卖币订单总额
            afterAdvertiseBusiness.setOrderSellTotalPrice(afterAdvertiseBusiness.getOrderSellTotalPrice().add(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderTotalPrice()));
        }
//更新到数据库
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnAppealOrderEventForClient(UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO) {

        String advertiseBusinessCode = updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.getAdvertiseBusinessCode();
        //加载
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //已禁用
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //设置订单当日申诉次数
        afterAdvertiseBusiness.setOrderTodayAppealCount(advertiseBusinessRespDTO.getOrderTodayAppealCount() + 1);

        //设置订单最后申诉时间
        afterAdvertiseBusiness.setOrderLastAppealTime(updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.getAppealTime());
        //更新到数据库
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

}