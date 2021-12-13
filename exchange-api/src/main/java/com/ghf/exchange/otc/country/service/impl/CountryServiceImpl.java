package com.ghf.exchange.otc.country.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.country.dto.*;
import com.ghf.exchange.otc.country.entity.Country;
import com.ghf.exchange.otc.country.entity.QCountry;
import com.ghf.exchange.otc.country.enums.CountryStatusEnum;
import com.ghf.exchange.otc.country.repository.CountryRepository;
import com.ghf.exchange.otc.country.service.CountryService;
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
public class CountryServiceImpl extends BaseServiceImpl<Country, Long> implements CountryService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private CountryService countryService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public CountryServiceImpl(CountryRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Country", key = "'pageCountry:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.countryCode).concat(':').concat(#p0.countryName)", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")

    @Override
    @SneakyThrows
    public Result<PageRespDTO<CountryRespDTO>> pageCountry(PageCountryReqDTO pageCountryReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageCountryReqDTO.getCountryCode())) {
            predicate.and(QCountry.country.countryCode.eq(pageCountryReqDTO.getCountryCode()));
        }

        if (!ObjectUtils.isEmpty(pageCountryReqDTO.getCountryName())) {
            predicate.and(QCountry.country.countryName.eq(pageCountryReqDTO.getCountryName()));
        }

        PageRespDTO<CountryRespDTO> pageRespDTO = countryService.page(predicate, pageCountryReqDTO, CountryRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Country", key = "'pageCountryForAdmin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.countryCode).concat(':').concat(#p0.countryName) ", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<CountryRespDTO>> pageCountryForAdmin(PageCountryForAdminReqDTO pageCountryForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(pageCountryForAdminReqDTO.getCountryCode())) {
            predicate.and(QCountry.country.countryCode.eq(pageCountryForAdminReqDTO.getCountryCode()));
        }

        if (!ObjectUtils.isEmpty(pageCountryForAdminReqDTO.getCountryName())) {
            predicate.and(QCountry.country.countryName.eq(pageCountryForAdminReqDTO.getCountryName()));
        }

        PageRespDTO<CountryRespDTO> pageRespDTO = countryService.page(predicate, pageCountryForAdminReqDTO, CountryRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Country", key = "'listCountry:'.concat(':').concat(#p0.countryCode).concat(':').concat(#p0.countryName) ")
    @Override
    @SneakyThrows
    public Result<List<CountryRespDTO>> listCountry(ListCountryReqDTO listCountryReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listCountryReqDTO.getCountryCode())) {
            predicate.and(QCountry.country.countryCode.eq(listCountryReqDTO.getCountryCode()));
        }

        if (!ObjectUtils.isEmpty(listCountryReqDTO.getCountryName())) {
            predicate.and(QCountry.country.countryName.eq(listCountryReqDTO.getCountryName()));
        }

        predicate.and(QCountry.country.status.eq(CountryStatusEnum.ENABLE.getCode()));

        List<CountryRespDTO> list = countryService.list(predicate, CountryRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Country", key = "'listCountryForAdmin:'.concat(':').concat(#p0.countryCode).concat(':').concat(#p0.countryName)")
    @Override
    @SneakyThrows
    public Result<List<CountryRespDTO>> listCountryForAdmin(ListCountryForAdminReqDTO listCountryForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (!ObjectUtils.isEmpty(listCountryForAdminReqDTO.getCountryCode())) {
            predicate.and(QCountry.country.countryCode.eq(listCountryForAdminReqDTO.getCountryCode()));
        }

        if (!ObjectUtils.isEmpty(listCountryForAdminReqDTO.getCountryName())) {
            predicate.and(QCountry.country.countryName.eq(listCountryForAdminReqDTO.getCountryName()));
        }
        predicate.and(QCountry.country.status.eq(CountryStatusEnum.ENABLE.getCode()));

        List<CountryRespDTO> list = countryService.list(predicate, CountryRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Country", key = "'listCountryForClient:'.concat(':').concat(#p0.countryCode).concat(':').concat(#p0.countryName)")
    @Override
    @SneakyThrows
    public Result<List<CountryRespDTO>> listCountryForClient(ListCountryForClientReqDTO listCountryForClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listCountryForClientReqDTO.getCountryCode())) {
            predicate.and(QCountry.country.countryCode.eq(listCountryForClientReqDTO.getCountryCode()));
        }

        if (!ObjectUtils.isEmpty(listCountryForClientReqDTO.getCountryName())) {
            predicate.and(QCountry.country.countryName.eq(listCountryForClientReqDTO.getCountryName()));
        }
        predicate.and(QCountry.country.status.eq(CountryStatusEnum.ENABLE.getCode()));

        List<CountryRespDTO> list = countryService.list(predicate, CountryRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Country", key = "'getCountryByCountryCode:' +':'+#p0.countryCode")
    @Override
    @SneakyThrows
    public Result<CountryRespDTO> getCountryByCountryCode(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {

        String countryCode = getCountryByCountryCodeReqDTO.getCountryCode();
        Predicate predicate = QCountry.country.countryCode.eq(countryCode);
        Country country = countryService.get(predicate);

        //返回
        CountryRespDTO countryRespDTO = ModelMapperUtil.map(country, CountryRespDTO.class);

        return new Result<>(countryRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsCountryByCountryCode(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {

        String countryCode = getCountryByCountryCodeReqDTO.getCountryCode();
        Predicate predicate = QCountry.country.countryCode.eq(countryCode);
        boolean b = countryService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Country", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addCountryForAdmin(AddCountryForAdminReqDTO addCountryForAdminReqDTO) {
        Country country = new Country();

        //初始化id
        country.setId(IdUtil.generateLongId());
        //判断编号
        if (!ObjectUtils.isEmpty(addCountryForAdminReqDTO.getCountryCode())) {
            //判断唯一性
            String countryCode = addCountryForAdminReqDTO.getCountryCode();
            GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO = new GetCountryByCountryCodeReqDTO();
            getCountryByCountryCodeReqDTO.setCountryCode(countryCode);
            boolean b = countryService.existsCountryByCountryCode(getCountryByCountryCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.PAYMENT_EXISTS);
            }
            country.setCountryCode(addCountryForAdminReqDTO.getCountryCode());
        } else {
            //自动生成编号
            country.setCountryCode(country.getId() + "");
        }

        country.setCountryName(addCountryForAdminReqDTO.getCountryName());
        //设置状态
        country.setStatus(CountryStatusEnum.ENABLE.getCode());

        country.setCreateTime(new Date());

        //持久化到数据库
        countryService.add(country);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Country", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateCountryByCountryForAdminCode(UpdateCountryByCountryCodeForAdminReqDTO updateCountryByCountryCodeForAdminReqDTO) {
        String countryCode = updateCountryByCountryCodeForAdminReqDTO.getCountryCode();
        //加载
        GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO = new GetCountryByCountryCodeReqDTO();
        getCountryByCountryCodeReqDTO.setCountryCode(countryCode);
        CountryRespDTO countryRespDTO = countryService.getCountryByCountryCode(getCountryByCountryCodeReqDTO).getData();
        if (countryRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }
        //已禁用状态无权限
        if (countryRespDTO.getStatus() == CountryStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_DISABLE);
        }

        Country afterCountry = ModelMapperUtil.map(countryRespDTO, Country.class);

        afterCountry.setCountryName(updateCountryByCountryCodeForAdminReqDTO.getCountryName());
        //设置状态
        afterCountry.setStatus(CountryStatusEnum.ENABLE.getCode());
        //更新到数据库
        countryService.update(afterCountry);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Country", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableCountryForAdmin(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {
        String countryCode = getCountryByCountryCodeReqDTO.getCountryCode();
        //加载
        CountryRespDTO countryRespDTO = countryService.getCountryByCountryCode(getCountryByCountryCodeReqDTO).getData();
        if (countryRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }

        //已启用状态无法启用
        if (countryRespDTO.getStatus() == CountryStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_ENABLE);
        }

        Country afterCountry = ModelMapperUtil.map(countryRespDTO, Country.class);

        //设置状态
        afterCountry.setStatus(CountryStatusEnum.ENABLE.getCode());

        //更新到数据库
        countryService.update(afterCountry);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Country", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableCountryForAdmin(GetCountryByCountryCodeReqDTO getCountryByCountryCodeReqDTO) {
        String countryCode = getCountryByCountryCodeReqDTO.getCountryCode();
        //加载
        CountryRespDTO countryRespDTO = countryService.getCountryByCountryCode(getCountryByCountryCodeReqDTO).getData();
        if (countryRespDTO == null) {
            return new Result<>(ResultCodeEnum.COIN_NOT_EXISTS);
        }

        //已禁用状态无法禁用
        if (countryRespDTO.getStatus() == CountryStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.COIN_STATUS_DISABLE);
        }

        Country afterCountry = ModelMapperUtil.map(countryRespDTO, Country.class);

        //设置状态
        afterCountry.setStatus(CountryStatusEnum.DISABLE.getCode());

        //更新到数据库
        countryService.update(afterCountry);

        return new Result<>(ResultCodeEnum.OK);
    }

}