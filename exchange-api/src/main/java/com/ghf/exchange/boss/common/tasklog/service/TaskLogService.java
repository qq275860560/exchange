package com.ghf.exchange.boss.common.tasklog.service;

import com.ghf.exchange.boss.common.tasklog.dto.*;
import com.ghf.exchange.boss.common.tasklog.entity.TaskLog;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface TaskLogService extends BaseService<TaskLog, Long> {
    /**
     * 分页搜索任务日志
     *
     * @param pageTaskLogReqDTO
     * @return
     */
    Result<PageRespDTO<TaskLogRespDTO>> pageTaskLog(PageTaskLogReqDTO pageTaskLogReqDTO);

    /**
     * 列出任务日志
     *
     * @param listTaskLogReqDTO
     * @return
     */
    Result<List<TaskLogRespDTO>> listTaskLog(ListTaskLogReqDTO listTaskLogReqDTO);

    /**
     * 根据任务日志名称获取任务日志详情
     *
     * @param getTaskLogByTasklognameReqDTO
     * @return
     */
    Result<TaskLogRespDTO> getTaskLogByTasklogname(GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO);

    /**
     * 根据任务日志名称判断任务日志是否存在
     *
     * @param getTaskLogByTasklognameReqDTO
     * @return
     */
    Result<Boolean> existsTaskLogByTasklogname(GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO);

    /**
     * 新建任务日志
     *
     * @param addTaskLogReqDTO
     * @return
     */
    Result<Void> addTaskLog(AddTaskLogReqDTO addTaskLogReqDTO);

}