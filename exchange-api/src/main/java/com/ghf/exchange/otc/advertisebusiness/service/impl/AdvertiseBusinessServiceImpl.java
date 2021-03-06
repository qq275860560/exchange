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
        //??????????????????????????????
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
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
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
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderTodayAppealCount(0);
            }
        });
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusiness(ListAdvertiseBusinessReqDTO listAdvertiseBusinessReqDTO) {

        //??????????????????????????????
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
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
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
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
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
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
                //?????????????????????????????????0
                advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
            }

            if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                //?????????????????????????????????0
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

        //??????
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = ModelMapperUtil.map(advertiseBusiness, AdvertiseBusinessRespDTO.class);

        if (advertiseBusinessRespDTO.getOrderLastAddTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
            //?????????????????????????????????0
            advertiseBusinessRespDTO.setOrderMonthBuySellCount(0);
        }

        if (advertiseBusinessRespDTO.getOrderLastReleaseTime() == null || !new SimpleDateFormat("yyyy-MM").format(advertiseBusinessRespDTO.getOrderLastReleaseTime()).equals(new SimpleDateFormat("yyyy-MM").format(new Date()))) {
            //?????????????????????????????????0
            advertiseBusinessRespDTO.setOrderMonthBuySellReleaseCount(0);
        }

        if (advertiseBusinessRespDTO.getOrderLastAppealTime() == null || !new SimpleDateFormat("yyyy-MM-dd").format(advertiseBusinessRespDTO.getOrderLastAppealTime()).equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
            //?????????????????????????????????0
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

        //?????????id
        advertiseBusiness.setId(IdUtil.generateLongId());
        //????????????
        if (!ObjectUtils.isEmpty(addAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode())) {
            //???????????????
            String advertiseBusinessCode = addAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode();
            GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
            getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
            boolean b = advertiseBusinessService.existsAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_EXISTS);
            }
            advertiseBusiness.setAdvertiseBusinessCode(addAdvertiseBusinessForAdminReqDTO.getAdvertiseBusinessCode());
        } else {
            //??????????????????
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
        //?????????????????????????????????
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(addAdvertiseBusinessForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        advertiseBusiness.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        advertiseBusiness.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        advertiseBusiness.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        advertiseBusiness.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        advertiseBusiness.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        advertiseBusiness.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());
        //???????????????
        advertiseBusiness.setDeposit(addAdvertiseBusinessForAdminReqDTO.getDeposit());

        //??????kyc
        if (addAdvertiseBusinessForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_1.getCode() && addAdvertiseBusinessForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_2.getCode() && addAdvertiseBusinessForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_3.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_KYC_NOT_EXISTS);
        }
        //??????kyc
        advertiseBusiness.setKyc(addAdvertiseBusinessForAdminReqDTO.getKyc());
        advertiseBusiness.setAdvertisePermission(addAdvertiseBusinessForAdminReqDTO.getAdvertisePermission());
        //????????????
        advertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.ENABLE.getCode());

        advertiseBusiness.setCreateTime(new Date());

        //?????????????????????
        advertiseBusinessService.add(advertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessByAdvertiseBusinessForAdminCode(UpdateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO) {
        String advertiseBusinessCode = updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getAdvertiseBusinessCode();
        //??????
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }
        //????????????????????????
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

        //?????????????????????????????????
        GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.setLegalCurrencyCountryCode(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getCountryCode());
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCountryCode(getLegalCurrencyByLegalCurrencyCountryCodeReqDTO).getData();
        afterAdvertiseBusiness.setCountryCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterAdvertiseBusiness.setCountryName(legalCurrencyRespDTO.getLegalCurrencyCountryName());
        afterAdvertiseBusiness.setLegalCurrencyCode(legalCurrencyRespDTO.getLegalCurrencyCode());
        afterAdvertiseBusiness.setLegalCurrencyName(legalCurrencyRespDTO.getLegalCurrencyName());
        afterAdvertiseBusiness.setLegalCurrencySymbol(legalCurrencyRespDTO.getLegalCurrencySymbol());
        afterAdvertiseBusiness.setLegalCurrencyUnit(legalCurrencyRespDTO.getLegalCurrencyUnit());

        //???????????????
        afterAdvertiseBusiness.setDeposit(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getDeposit());
        //??????kyc
        if (updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_1.getCode() && updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_2.getCode() && updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc() != AdvertiseBusinessKycEnum.KYC_3.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_KYC_NOT_EXISTS);
        }
        //??????kyc
        afterAdvertiseBusiness.setKyc(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getKyc());
        afterAdvertiseBusiness.setAdvertisePermission(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO.getAdvertisePermission());

        //????????????
        afterAdvertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.ENABLE.getCode());
        //??????????????????
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> enableAdvertiseBusinessForAdmin(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        String advertiseBusinessCode = getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode();
        //??????
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //???????????????????????????
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_ENABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //????????????
        afterAdvertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.ENABLE.getCode());

        //??????????????????
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> disableAdvertiseBusinessForAdmin(GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        String advertiseBusinessCode = getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.getAdvertiseBusinessCode();
        //??????
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //???????????????????????????
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //????????????
        afterAdvertiseBusiness.setStatus(AdvertiseBusinessStatusEnum.DISABLE.getCode());

        //??????????????????
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
        //??????
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //?????????
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //??????????????????????????????
        afterAdvertiseBusiness.setOrderBuySellCount(advertiseBusinessRespDTO.getOrderBuySellCount() + 1);
        if (updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //????????????????????????
            afterAdvertiseBusiness.setOrderBuyCount(advertiseBusinessRespDTO.getOrderBuyCount() + 1);
        } else if (updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //????????????????????????
            afterAdvertiseBusiness.setOrderSellCount(advertiseBusinessRespDTO.getOrderSellCount() + 1);
        }
        //?????????????????????=????????????????????????/????????????????????????????????????
        if (updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterAdvertiseBusiness.setOrderBuyReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount()).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyCount() + 1), 8, BigDecimal.ROUND_UP));
        }
        //??????????????????=(????????????????????????+????????????????????????)/????????????????????????
        afterAdvertiseBusiness.setOrderBuySellReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + advertiseBusinessRespDTO.getOrderSellReleaseCount()).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuySellCount() + 1), 8, BigDecimal.ROUND_UP));

        //??????????????????????????????
        afterAdvertiseBusiness.setOrderMonthBuySellCount(advertiseBusinessRespDTO.getOrderMonthBuySellCount() + 1);

        //??????????????????????????????
        afterAdvertiseBusiness.setOrderLastAddTime(updateAdvertiseBusinessOnAddOrderEventForClientReqDTO.getAddTime());

//??????????????????
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnReleaseOrderEventForClient(UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO) {
        String advertiseBusinessCode = updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getAdvertiseBusinessCode();
        //??????
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //?????????
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //??????????????????????????????
            afterAdvertiseBusiness.setOrderBuyReleaseCount(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + 1);
        } else if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //??????????????????????????????
            afterAdvertiseBusiness.setOrderSellReleaseCount(advertiseBusinessRespDTO.getOrderSellReleaseCount() + 1);
        }

        //?????????????????????=????????????????????????/????????????????????????????????????
        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            afterAdvertiseBusiness.setOrderBuyReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + 1).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyCount()), 8, BigDecimal.ROUND_UP));
        }
        //??????????????????=(????????????????????????+????????????????????????)/????????????????????????
        afterAdvertiseBusiness.setOrderBuySellReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuyReleaseCount() + advertiseBusinessRespDTO.getOrderSellReleaseCount() + 1).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderBuySellCount()), 8, BigDecimal.ROUND_UP));

        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //????????????????????????????????????
            afterAdvertiseBusiness.setOrderBuyTotalReleaseTime(advertiseBusinessRespDTO.getOrderBuyTotalReleaseTime() + updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        } else if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //????????????????????????????????????
            afterAdvertiseBusiness.setOrderSellTotalReleaseTime(advertiseBusinessRespDTO.getOrderSellTotalReleaseTime() + updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getPayTime().getTime());
        }

        //??????????????????????????????
        afterAdvertiseBusiness.setOrderBuySellAvgReleaseTime((advertiseBusinessRespDTO.getOrderBuyTotalReleaseTime() + advertiseBusinessRespDTO.getOrderSellTotalReleaseTime() + updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime().getTime() - updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getPayTime().getTime()) / advertiseBusinessRespDTO.getOrderBuySellCount());

        //????????????????????????????????????
        if (new SimpleDateFormat("yyyy-MM").format(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getAddTime()).equals(new SimpleDateFormat("yyyy-MM").format(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime()))) {

            //??????????????????????????????
            afterAdvertiseBusiness.setOrderMonthBuySellReleaseCount(advertiseBusinessRespDTO.getOrderMonthBuySellReleaseCount() + 1);
            //??????????????????????????????
            afterAdvertiseBusiness.setOrderMonthBuySellReleaseRate(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderMonthBuySellReleaseCount() + 1).divide(BigDecimal.valueOf(advertiseBusinessRespDTO.getOrderMonthBuySellCount()), 8, BigDecimal.ROUND_UP));

            //??????????????????????????????
            afterAdvertiseBusiness.setOrderLastReleaseTime(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getReleaseTime());

        }

        if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.BUY.getCode()) {
            //????????????????????????
            afterAdvertiseBusiness.setOrderBuyTotalPrice(afterAdvertiseBusiness.getOrderBuyTotalPrice().add(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderTotalPrice()));
        } else if (updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderBuySellType() == OrderBuySellTypeEnum.SELL.getCode()) {
            //????????????????????????
            afterAdvertiseBusiness.setOrderSellTotalPrice(afterAdvertiseBusiness.getOrderSellTotalPrice().add(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO.getOrderTotalPrice()));
        }
//??????????????????
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnAppealOrderEventForClient(UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO) {

        String advertiseBusinessCode = updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.getAdvertiseBusinessCode();
        //??????
        GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO = new GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO();
        getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO.setAdvertiseBusinessCode(advertiseBusinessCode);
        AdvertiseBusinessRespDTO advertiseBusinessRespDTO = advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO).getData();
        if (advertiseBusinessRespDTO == null) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_NOT_EXISTS);
        }

        //?????????
        if (advertiseBusinessRespDTO.getStatus() == AdvertiseBusinessStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.ADVERTISE_BUSINESS_STATUS_DISABLE);
        }

        AdvertiseBusiness afterAdvertiseBusiness = ModelMapperUtil.map(advertiseBusinessRespDTO, AdvertiseBusiness.class);

        //??????????????????????????????
        afterAdvertiseBusiness.setOrderTodayAppealCount(advertiseBusinessRespDTO.getOrderTodayAppealCount() + 1);

        //??????????????????????????????
        afterAdvertiseBusiness.setOrderLastAppealTime(updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO.getAppealTime());
        //??????????????????
        advertiseBusinessService.update(afterAdvertiseBusiness);

        return new Result<>(ResultCodeEnum.OK);
    }

}