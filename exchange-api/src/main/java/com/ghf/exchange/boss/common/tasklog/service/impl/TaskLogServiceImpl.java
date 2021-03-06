package com.ghf.exchange.boss.common.tasklog.service.impl;

import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.tasklog.dto.*;
import com.ghf.exchange.boss.common.tasklog.entity.QTaskLog;
import com.ghf.exchange.boss.common.tasklog.entity.TaskLog;
import com.ghf.exchange.boss.common.tasklog.repository.TaskLogRepository;
import com.ghf.exchange.boss.common.tasklog.service.TaskLogService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class TaskLogServiceImpl extends BaseServiceImpl<TaskLog, Long> implements TaskLogService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private TaskLogService tasklogService;

    public TaskLogServiceImpl(TaskLogRepository repository) {
        super(repository);
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<TaskLogRespDTO>> pageTaskLog(PageTaskLogReqDTO pageTaskLogReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageTaskLogReqDTO.getTaskname())) {
            predicate.and(QTaskLog.taskLog.taskname.contains(pageTaskLogReqDTO.getTaskname()));
        }
        if (!ObjectUtils.isEmpty(pageTaskLogReqDTO.getTasklogname())) {
            predicate.and(QTaskLog.taskLog.tasklogname.contains(pageTaskLogReqDTO.getTasklogname()));
        }

        PageRespDTO<TaskLogRespDTO> pageRespDTO = tasklogService.page(predicate, pageTaskLogReqDTO, TaskLogRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<TaskLogRespDTO>> listTaskLog(ListTaskLogReqDTO listTaskLogReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listTaskLogReqDTO.getTaskname())) {
            predicate.and(QTaskLog.taskLog.taskname.contains(listTaskLogReqDTO.getTaskname()));
        }
        if (!ObjectUtils.isEmpty(listTaskLogReqDTO.getTasklogname())) {
            predicate.and(QTaskLog.taskLog.tasklogname.contains(listTaskLogReqDTO.getTasklogname()));
        }

        List<TaskLogRespDTO> list = tasklogService.list(predicate, TaskLogRespDTO.class);
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<TaskLogRespDTO> getTaskLogByTasklogname(GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO) {
        //TODO ????????????
        String tasklogname = getTaskLogByTasklognameReqDTO.getTasklogname();
        Predicate predicate = QTaskLog.taskLog.tasklogname.eq(tasklogname);
        TaskLog tasklog = tasklogService.get(predicate);
        //??????
        TaskLogRespDTO tasklogRespDTO = ModelMapperUtil.map(tasklog, TaskLogRespDTO.class);
        return new Result<>(tasklogRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsTaskLogByTasklogname(GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO) {
        //TODO ????????????
        String tasklogname = getTaskLogByTasklognameReqDTO.getTasklogname();
        Predicate predicate = QTaskLog.taskLog.tasklogname.eq(tasklogname);
        boolean b = tasklogService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Void> addTaskLogForClient(AddTaskLogForClientReqDTO addTaskLogForClientReqDTO) {
        TaskLog tasklog = ModelMapperUtil.map(addTaskLogForClientReqDTO, TaskLog.class);
        //?????????id
        tasklog.setId(IdUtil.generateLongId());
        //???????????????
        if (!ObjectUtils.isEmpty(addTaskLogForClientReqDTO.getTaskname())) {
            //???????????????
            String tasklogname = tasklog.getTasklogname();
            GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO = new GetTaskLogByTasklognameReqDTO();
            getTaskLogByTasklognameReqDTO.setTasklogname(tasklogname);
            boolean b = tasklogService.existsTaskLogByTasklogname(getTaskLogByTasklognameReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.TASK_EXISTS);
            }
            tasklog.setTaskname(addTaskLogForClientReqDTO.getTaskname());
        } else {
            //????????????
            tasklog.setTaskname(tasklog.getId() + "");
        }
        //??????????????????
        tasklogService.add(tasklog);
        return new Result<>(ResultCodeEnum.OK);
    }

}