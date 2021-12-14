package com.ghf.exchange.otc.legalcurrency.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.legalcurrency.dto.*;
import com.ghf.exchange.otc.legalcurrency.entity.LegalCurrency;
import com.ghf.exchange.otc.legalcurrency.entity.QLegalCurrency;
import com.ghf.exchange.otc.legalcurrency.enums.LegalCurrencyStatusEnum;
import com.ghf.exchange.otc.legalcurrency.repository.LegalCurrencyRepository;
import com.ghf.exchange.otc.legalcurrency.service.LegalCurrencyService;
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
public class LegalCurrencyServiceImpl extends BaseServiceImpl<LegalCurrency, Long> implements LegalCurrencyService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private LegalCurrencyService legalCurrencyService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public LegalCurrencyServiceImpl(LegalCurrencyRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'pageLegalCurrency:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.legalCurrencyCode).concat(':').concat(#p0.legalCurrencyName)", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")

    @Override
    @SneakyThrows
    public Result<PageRespDTO<LegalCurrencyRespDTO>> pageLegalCurrency(PageLegalCurrencyReqDTO pageLegalCurrencyReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageLegalCurrencyReqDTO.getLegalCurrencyCode())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyCode.eq(pageLegalCurrencyReqDTO.getLegalCurrencyCode()));
        }

        if (!ObjectUtils.isEmpty(pageLegalCurrencyReqDTO.getLegalCurrencyName())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyName.eq(pageLegalCurrencyReqDTO.getLegalCurrencyName()));
        }

        PageRespDTO<LegalCurrencyRespDTO> pageRespDTO = legalCurrencyService.page(predicate, pageLegalCurrencyReqDTO, LegalCurrencyRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'pageLegalCurrencyForAdmin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.legalCurrencyCode).concat(':').concat(#p0.legalCurrencyName) ", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<LegalCurrencyRespDTO>> pageLegalCurrencyForAdmin(PageLegalCurrencyForAdminReqDTO pageLegalCurrencyForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageLegalCurrencyForAdminReqDTO.getLegalCurrencyCode())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyCode.eq(pageLegalCurrencyForAdminReqDTO.getLegalCurrencyCode()));
        }

        if (!ObjectUtils.isEmpty(pageLegalCurrencyForAdminReqDTO.getLegalCurrencyName())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyName.eq(pageLegalCurrencyForAdminReqDTO.getLegalCurrencyName()));
        }

        PageRespDTO<LegalCurrencyRespDTO> pageRespDTO = legalCurrencyService.page(predicate, pageLegalCurrencyForAdminReqDTO, LegalCurrencyRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'listLegalCurrency:'.concat(':').concat(#p0.legalCurrencyCode).concat(':').concat(#p0.legalCurrencyName) ")
    @Override
    @SneakyThrows
    public Result<List<LegalCurrencyRespDTO>> listLegalCurrency(ListLegalCurrencyReqDTO listLegalCurrencyReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listLegalCurrencyReqDTO.getLegalCurrencyCode())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyCode.eq(listLegalCurrencyReqDTO.getLegalCurrencyCode()));
        }

        if (!ObjectUtils.isEmpty(listLegalCurrencyReqDTO.getLegalCurrencyName())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyName.eq(listLegalCurrencyReqDTO.getLegalCurrencyName()));
        }

        predicate.and(QLegalCurrency.legalCurrency.status.eq(LegalCurrencyStatusEnum.ENABLE.getCode()));

        List<LegalCurrencyRespDTO> list = legalCurrencyService.list(predicate, LegalCurrencyRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'listLegalCurrencyForAdmin:'.concat(':').concat(#p0.legalCurrencyCode).concat(':').concat(#p0.legalCurrencyName)")
    @Override
    @SneakyThrows
    public Result<List<LegalCurrencyRespDTO>> listLegalCurrencyForAdmin(ListLegalCurrencyForAdminReqDTO listLegalCurrencyForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(listLegalCurrencyForAdminReqDTO.getLegalCurrencyCode())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyCode.eq(listLegalCurrencyForAdminReqDTO.getLegalCurrencyCode()));
        }

        if (!ObjectUtils.isEmpty(listLegalCurrencyForAdminReqDTO.getLegalCurrencyName())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyName.eq(listLegalCurrencyForAdminReqDTO.getLegalCurrencyName()));
        }
        predicate.and(QLegalCurrency.legalCurrency.status.eq(LegalCurrencyStatusEnum.ENABLE.getCode()));

        List<LegalCurrencyRespDTO> list = legalCurrencyService.list(predicate, LegalCurrencyRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'listLegalCurrencyForClient:'.concat(':').concat(#p0.legalCurrencyCode).concat(':').concat(#p0.legalCurrencyName)")
    @Override
    @SneakyThrows
    public Result<List<LegalCurrencyRespDTO>> listLegalCurrencyForClient(ListLegalCurrencyForClientReqDTO listLegalCurrencyForClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listLegalCurrencyForClientReqDTO.getLegalCurrencyCode())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyCode.eq(listLegalCurrencyForClientReqDTO.getLegalCurrencyCode()));
        }

        if (!ObjectUtils.isEmpty(listLegalCurrencyForClientReqDTO.getLegalCurrencyName())) {
            predicate.and(QLegalCurrency.legalCurrency.legalCurrencyName.eq(listLegalCurrencyForClientReqDTO.getLegalCurrencyName()));
        }
        predicate.and(QLegalCurrency.legalCurrency.status.eq(LegalCurrencyStatusEnum.ENABLE.getCode()));

        List<LegalCurrencyRespDTO> list = legalCurrencyService.list(predicate, LegalCurrencyRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'getLegalCurrencyByLegalCurrencyCountryCode:' +':'+#p0.legalCurrencyCountryCode")
    @Override
    @SneakyThrows
    public Result<LegalCurrencyRespDTO> getLegalCurrencyByLegalCurrencyCountryCode(GetLegalCurrencyByLegalCurrencyCountryCodeReqDTO getLegalCurrencyByLegalCurrencyCountryCodeReqDTO) {

        String legalCurrencyCountryCode = getLegalCurrencyByLegalCurrencyCountryCodeReqDTO.getLegalCurrencyCountryCode();
        Predicate predicate = QLegalCurrency.legalCurrency.legalCurrencyCountryCode.eq(legalCurrencyCountryCode);
        LegalCurrency legalCurrency = legalCurrencyService.get(predicate);

        //返回
        LegalCurrencyRespDTO legalCurrencyRespDTO = ModelMapperUtil.map(legalCurrency, LegalCurrencyRespDTO.class);

        return new Result<>(legalCurrencyRespDTO);
    }

    @Cacheable(cacheNames = "LegalCurrency", key = "'getLegalCurrencyByLegalCurrencyCode:' +':'+#p0.legalCurrencyCode")
    @Override
    @SneakyThrows
    public Result<LegalCurrencyRespDTO> getLegalCurrencyByLegalCurrencyCode(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {

        String legalCurrencyCode = getLegalCurrencyByLegalCurrencyCodeReqDTO.getLegalCurrencyCode();
        Predicate predicate = QLegalCurrency.legalCurrency.legalCurrencyCode.eq(legalCurrencyCode);
        LegalCurrency legalCurrency = legalCurrencyService.get(predicate);

        //返回
        LegalCurrencyRespDTO legalCurrencyRespDTO = ModelMapperUtil.map(legalCurrency, LegalCurrencyRespDTO.class);

        return new Result<>(legalCurrencyRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsLegalCurrencyByLegalCurrencyCode(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {

        String legalCurrencyCode = getLegalCurrencyByLegalCurrencyCodeReqDTO.getLegalCurrencyCode();
        Predicate predicate = QLegalCurrency.legalCurrency.legalCurrencyCode.eq(legalCurrencyCode);
        boolean b = legalCurrencyService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "LegalCurrency", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addLegalCurrencyForAdmin(AddLegalCurrencyForAdminReqDTO addLegalCurrencyForAdminReqDTO) {
        LegalCurrency legalCurrency = new LegalCurrency();

        //初始化id
        legalCurrency.setId(IdUtil.generateLongId());
        //判断编号
        if (!ObjectUtils.isEmpty(addLegalCurrencyForAdminReqDTO.getLegalCurrencyCode())) {
            //判断唯一性
            String legalCurrencyCode = addLegalCurrencyForAdminReqDTO.getLegalCurrencyCode();
            GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCodeReqDTO();
            getLegalCurrencyByLegalCurrencyCodeReqDTO.setLegalCurrencyCode(legalCurrencyCode);
            boolean b = legalCurrencyService.existsLegalCurrencyByLegalCurrencyCode(getLegalCurrencyByLegalCurrencyCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.PAYMENT_EXISTS);
            }
            legalCurrency.setLegalCurrencyCode(addLegalCurrencyForAdminReqDTO.getLegalCurrencyCode());
        } else {
            //自动生成编号
            legalCurrency.setLegalCurrencyCode(legalCurrency.getId() + "");
        }

        legalCurrency.setLegalCurrencyName(addLegalCurrencyForAdminReqDTO.getLegalCurrencyName());
        legalCurrency.setLegalCurrencySymbol(addLegalCurrencyForAdminReqDTO.getLegalCurrencySymbol());
        legalCurrency.setLegalCurrencyUnit(addLegalCurrencyForAdminReqDTO.getLegalCurrencyUnit());
        legalCurrency.setLegalCurrencyCountryCode(addLegalCurrencyForAdminReqDTO.getLegalCurrencyCountryCode());
        legalCurrency.setLegalCurrencyCountryName(addLegalCurrencyForAdminReqDTO.getLegalCurrencyCountryName());
        legalCurrency.setLegalCurrencyExchangeRate(addLegalCurrencyForAdminReqDTO.getLegalCurrencyExchangeRate());
        //设置状态
        legalCurrency.setStatus(LegalCurrencyStatusEnum.ENABLE.getCode());

        legalCurrency.setCreateTime(new Date());

        //持久化到数据库
        legalCurrencyService.add(legalCurrency);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "LegalCurrency", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateLegalCurrencyByLegalCurrencyForAdminCode(UpdateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO) {
        String legalCurrencyCode = updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencyCode();
        //加载
        GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO = new GetLegalCurrencyByLegalCurrencyCodeReqDTO();
        getLegalCurrencyByLegalCurrencyCodeReqDTO.setLegalCurrencyCode(legalCurrencyCode);
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCode(getLegalCurrencyByLegalCurrencyCodeReqDTO).getData();
        if (legalCurrencyRespDTO == null) {
            return new Result<>(ResultCodeEnum.LEGAL_CURRENCY_NOT_EXISTS);
        }
        //已禁用状态无权限
        if (legalCurrencyRespDTO.getStatus() == LegalCurrencyStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.LEGAL_CURRENCY_STATUS_DISABLE);
        }

        LegalCurrency afterLegalCurrency = ModelMapperUtil.map(legalCurrencyRespDTO, LegalCurrency.class);

        afterLegalCurrency.setLegalCurrencyName(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencyName());
        afterLegalCurrency.setLegalCurrencySymbol(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencySymbol());
        afterLegalCurrency.setLegalCurrencyUnit(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencyUnit());
        afterLegalCurrency.setLegalCurrencyCountryCode(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencyCountryCode());
        afterLegalCurrency.setLegalCurrencyCountryName(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencyCountryName());
        afterLegalCurrency.setLegalCurrencyExchangeRate(updateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO.getLegalCurrencyExchangeRate());
        //设置状态
        afterLegalCurrency.setStatus(LegalCurrencyStatusEnum.ENABLE.getCode());
        //更新到数据库
        legalCurrencyService.update(afterLegalCurrency);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "LegalCurrency", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableLegalCurrencyForAdmin(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {
        String legalCurrencyCode = getLegalCurrencyByLegalCurrencyCodeReqDTO.getLegalCurrencyCode();
        //加载
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCode(getLegalCurrencyByLegalCurrencyCodeReqDTO).getData();
        if (legalCurrencyRespDTO == null) {
            return new Result<>(ResultCodeEnum.LEGAL_CURRENCY_NOT_EXISTS);
        }

        //已启用状态无法启用
        if (legalCurrencyRespDTO.getStatus() == LegalCurrencyStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.LEGAL_CURRENCY_STATUS_ENABLE);
        }

        LegalCurrency afterLegalCurrency = ModelMapperUtil.map(legalCurrencyRespDTO, LegalCurrency.class);

        //设置状态
        afterLegalCurrency.setStatus(LegalCurrencyStatusEnum.ENABLE.getCode());

        //更新到数据库
        legalCurrencyService.update(afterLegalCurrency);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "LegalCurrency", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableLegalCurrencyForAdmin(GetLegalCurrencyByLegalCurrencyCodeReqDTO getLegalCurrencyByLegalCurrencyCodeReqDTO) {
        String legalCurrencyCode = getLegalCurrencyByLegalCurrencyCodeReqDTO.getLegalCurrencyCode();
        //加载
        LegalCurrencyRespDTO legalCurrencyRespDTO = legalCurrencyService.getLegalCurrencyByLegalCurrencyCode(getLegalCurrencyByLegalCurrencyCodeReqDTO).getData();
        if (legalCurrencyRespDTO == null) {
            return new Result<>(ResultCodeEnum.LEGAL_CURRENCY_NOT_EXISTS);
        }

        //已禁用状态无法禁用
        if (legalCurrencyRespDTO.getStatus() == LegalCurrencyStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.LEGAL_CURRENCY_STATUS_DISABLE);
        }

        LegalCurrency afterLegalCurrency = ModelMapperUtil.map(legalCurrencyRespDTO, LegalCurrency.class);

        //设置状态
        afterLegalCurrency.setStatus(LegalCurrencyStatusEnum.DISABLE.getCode());

        //更新到数据库
        legalCurrencyService.update(afterLegalCurrency);

        return new Result<>(ResultCodeEnum.OK);
    }

    //TODO 兑美元汇率

}