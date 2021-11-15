package com.ghf.exchange.otc.advertiselog.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.ClientRespDTO;
import com.ghf.exchange.boss.authorication.client.enums.ClientScopeEnum;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.dto.AdvertiseLogRespDTO;
import com.ghf.exchange.otc.advertiselog.dto.GetAdvertiseLogByAdvertiseLogCodeReqDTO;
import com.ghf.exchange.otc.advertiselog.dto.PageAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.entity.AdvertiseLog;
import com.ghf.exchange.otc.advertiselog.entity.QAdvertiseLog;
import com.ghf.exchange.otc.advertiselog.event.AddAdvertiseLogEvent;
import com.ghf.exchange.otc.advertiselog.repository.AdvertiseLogRepository;
import com.ghf.exchange.otc.advertiselog.service.AdvertiseLogService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class AdvertiseLogServiceImpl extends BaseServiceImpl<AdvertiseLog, Long> implements AdvertiseLogService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;
    @Lazy
    @Resource
    private AdvertiseLogService advertiseLogService;

    @Value("${security.oauth2.client.client-id}")
    public String clientId;

    @Value("${security.oauth2.client.client-secret}")
    public String secret;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AdvertiseLogServiceImpl(AdvertiseLogRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseLogRespDTO>> pageAdvertiseLog(PageAdvertiseLogReqDTO pageAdvertiseLogReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageAdvertiseLogReqDTO.getAdvertiseCode())) {
            predicate.and(QAdvertiseLog.advertiseLog.advertiseCode.contains(pageAdvertiseLogReqDTO.getAdvertiseCode()));
        }
        if (!ObjectUtils.isEmpty(pageAdvertiseLogReqDTO.getAdvertiseLogCode())) {
            predicate.and(QAdvertiseLog.advertiseLog.advertiseLogCode.contains(pageAdvertiseLogReqDTO.getAdvertiseLogCode()));
        }

        PageRespDTO<AdvertiseLogRespDTO> pageResult = advertiseLogService.page(predicate, pageAdvertiseLogReqDTO, AdvertiseLogRespDTO.class);
        return new Result<>(pageResult);
    }

    @Override
    @SneakyThrows
    public Result<AdvertiseLogRespDTO> getAdvertiseLogByAdvertiseLogCode(GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO) {
        //TODO 权限判断
        String advertiseLogCode = getAdvertiseLogByAdvertiseLogCodeReqDTO.getAdvertiseLogCode();
        Predicate predicate = QAdvertiseLog.advertiseLog.advertiseLogCode.eq(advertiseLogCode);
        AdvertiseLog advertiseLog = advertiseLogService.get(predicate);
        //返回
        AdvertiseLogRespDTO advertiseLogRespDTO = AutoMapUtils.map(advertiseLog, AdvertiseLogRespDTO.class);
        return new Result<>(advertiseLogRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsAdvertiseLogByAdvertiseLogCode(GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO) {
        //TODO 权限判断
        String advertiseLogCode = getAdvertiseLogByAdvertiseLogCodeReqDTO.getAdvertiseLogCode();
        Predicate predicate = QAdvertiseLog.advertiseLog.advertiseLogCode.eq(advertiseLogCode);
        boolean b = advertiseLogService.exists(predicate);
        return new Result<>(b);
    }

    @Transactional
    @Override
    @SneakyThrows
    public Result<Void> addAdvertiseLog(AddAdvertiseLogReqDTO addAdvertiseLogReqDTO) {
        AdvertiseLog advertiseLog = AutoMapUtils.map(addAdvertiseLogReqDTO, AdvertiseLog.class);

        //默认无权限
        boolean flag = false;
        //获取当前登陆客户端详情
        ClientRespDTO currentLoginClient = clientService.getCurrentLoginClient().getData();

        if (currentLoginClient.getScopes().contains(ClientScopeEnum.SERVER.getCode())) {
            //如果是内部后端服务器，才有权限调用该接口
            flag = true;
        }

        if (!flag) {
            //无权限取消订单，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //初始化id
        advertiseLog.setId(IdUtil.generateLongId());
        //判断广告编号
        if (!ObjectUtils.isEmpty(addAdvertiseLogReqDTO.getAdvertiseLogCode())) {
            //判断唯一性
            String advertiseLogCode = addAdvertiseLogReqDTO.getAdvertiseLogCode();
            GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO = new GetAdvertiseLogByAdvertiseLogCodeReqDTO();
            getAdvertiseLogByAdvertiseLogCodeReqDTO.setAdvertiseLogCode(advertiseLogCode);
            boolean b = advertiseLogService.existsAdvertiseLogByAdvertiseLogCode(getAdvertiseLogByAdvertiseLogCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.ADVERTISE_LOG_EXISTS);
            }
            advertiseLog.setAdvertiseLogCode(addAdvertiseLogReqDTO.getAdvertiseLogCode());
        } else {
            //自动生成广告日志编号
            advertiseLog.setAdvertiseLogCode(advertiseLog.getId() + "");
        }

        advertiseLog.setCreateTime(new Date());
        //持久化到数据库
        advertiseLogService.add(advertiseLog);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new AddAdvertiseLogEvent(addAdvertiseLogReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

}