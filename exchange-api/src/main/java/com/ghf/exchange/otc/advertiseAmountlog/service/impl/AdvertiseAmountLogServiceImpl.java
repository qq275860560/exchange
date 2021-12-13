package com.ghf.exchange.otc.advertiseamountlog.service.impl;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.advertiseamountlog.dto.AddAdvertiseAmountLogForClientReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.AdvertiseAmountLogRespDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.PageAdvertiseAmountLogForAdminReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.entity.AdvertiseAmountLog;
import com.ghf.exchange.otc.advertiseamountlog.entity.QAdvertiseAmountLog;
import com.ghf.exchange.otc.advertiseamountlog.event.AddAdvertiseAmountLogForClientEvent;
import com.ghf.exchange.otc.advertiseamountlog.repository.AdvertiseAmountLogRepository;
import com.ghf.exchange.otc.advertiseamountlog.service.AdvertiseAmountLogService;
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
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class AdvertiseAmountLogServiceImpl extends BaseServiceImpl<AdvertiseAmountLog, Long> implements AdvertiseAmountLogService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private AdvertiseAmountLogService advertiseAmountLogService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AdvertiseAmountLogServiceImpl(AdvertiseAmountLogRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseAmountLogRespDTO>> pageAdvertiseAmountLogForAdmin(PageAdvertiseAmountLogForAdminReqDTO pageAdvertiseAmountLogForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageAdvertiseAmountLogForAdminReqDTO.getAdvertiseCode())) {
            predicate.and(QAdvertiseAmountLog.advertiseAmountLog.advertiseCode.contains(pageAdvertiseAmountLogForAdminReqDTO.getAdvertiseCode()));
        }
        if (!ObjectUtils.isEmpty(pageAdvertiseAmountLogForAdminReqDTO.getAdvertiseAmountLogCode())) {
            predicate.and(QAdvertiseAmountLog.advertiseAmountLog.advertiseAmountLogCode.contains(pageAdvertiseAmountLogForAdminReqDTO.getAdvertiseAmountLogCode()));
        }

        PageRespDTO<AdvertiseAmountLogRespDTO> pageRespDTO = advertiseAmountLogService.page(predicate, pageAdvertiseAmountLogForAdminReqDTO, AdvertiseAmountLogRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<AdvertiseAmountLogRespDTO> getAdvertiseAmountLogByAdvertiseAmountLogCode(GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO) {
        //TODO 权限判断
        String advertiseAmountLogCode = getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO.getAdvertiseAmountLogCode();
        Predicate predicate = QAdvertiseAmountLog.advertiseAmountLog.advertiseAmountLogCode.eq(advertiseAmountLogCode);
        AdvertiseAmountLog advertiseAmountLog = advertiseAmountLogService.get(predicate);
        //返回
        AdvertiseAmountLogRespDTO advertiseAmountLogRespDTO = ModelMapperUtil.map(advertiseAmountLog, AdvertiseAmountLogRespDTO.class);
        return new Result<>(advertiseAmountLogRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAdvertiseAmountLogByAdvertiseAmountLogCode(GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO) {
        //TODO 权限判断
        String advertiseAmountLogCode = getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO.getAdvertiseAmountLogCode();
        Predicate predicate = QAdvertiseAmountLog.advertiseAmountLog.advertiseAmountLogCode.eq(advertiseAmountLogCode);
        boolean b = advertiseAmountLogService.exists(predicate);
        return new Result<>(b);
    }

    @Transactional
    @Override
    @SneakyThrows
    public Result<Void> addAdvertiseAmountLogForClient(AddAdvertiseAmountLogForClientReqDTO addAdvertiseAmountLogReqDTO) {
        AdvertiseAmountLog advertiseAmountLog = ModelMapperUtil.map(addAdvertiseAmountLogReqDTO, AdvertiseAmountLog.class);

        //初始化id
        advertiseAmountLog.setId(IdUtil.generateLongId());
        //判断广告编号
        if (!ObjectUtils.isEmpty(addAdvertiseAmountLogReqDTO.getAdvertiseAmountLogCode())) {
            //判断唯一性
            String advertiseAmountLogCode = addAdvertiseAmountLogReqDTO.getAdvertiseAmountLogCode();
            GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO = new GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO();
            getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO.setAdvertiseAmountLogCode(advertiseAmountLogCode);
            boolean b = advertiseAmountLogService.existsAdvertiseAmountLogByAdvertiseAmountLogCode(getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ADVERTISE_AMOUNT_LOG_EXISTS);
            }
            advertiseAmountLog.setAdvertiseAmountLogCode(addAdvertiseAmountLogReqDTO.getAdvertiseAmountLogCode());
        } else {
            //自动生成广告日志编号
            advertiseAmountLog.setAdvertiseAmountLogCode(advertiseAmountLog.getId() + "");
        }

        advertiseAmountLog.setCreateTime(new Date());
        //持久化到数据库
        advertiseAmountLogService.add(advertiseAmountLog);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new AddAdvertiseAmountLogForClientEvent());

        return new Result<>(ResultCodeEnum.OK);
    }

}