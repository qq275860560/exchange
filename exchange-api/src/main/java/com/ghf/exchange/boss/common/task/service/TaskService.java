package com.ghf.exchange.boss.common.task.service;

import com.ghf.exchange.boss.common.task.dto.*;
import com.ghf.exchange.boss.common.task.entity.Task;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface TaskService extends BaseService<Task, Long> {
    /**
     * 分页搜索定时任务
     *
     * @param pageTaskReqDTO
     * @return
     */
    Result<PageRespDTO<TaskRespDTO>> pageTask(PageTaskReqDTO pageTaskReqDTO);

    /**
     * 列出定时任务
     *
     * @param listTaskReqDTO
     * @return
     */
    Result<List<TaskRespDTO>> listTask(ListTaskReqDTO listTaskReqDTO);

    /**
     * 根据定时任务名称获取定时任务详情
     *
     * @param getTaskByTasknameReqDTO
     * @return
     */
    Result<TaskRespDTO> getTaskByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO);

    /**
     * 根据定时任务名称判断定时任务是否存在
     *
     * @param getTaskByTasknameReqDTO
     * @return
     */
    Result<Boolean> existsTaskByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO);

    /**
     * 新建定时任务
     *
     * @param addTaskReqDTO
     * @return
     */
    Result<Void> addTask(AddTaskReqDTO addTaskReqDTO);

    /**
     * 更新定时任务
     *
     * @param updateTaskByTasknameReqDTO
     * @return
     */
    Result<Void> updateTaskByTaskname(UpdateTaskByTasknameReqDTO updateTaskByTasknameReqDTO);

    /**
     * 更新定时任务状态为运行
     *
     * @param getTaskByTasknameReqDTO
     * @return
     */
    Result<Void> updateTaskStatusIsRunningByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO);

    /**
     * 更新定时任务状态为完成
     *
     * @param getTaskByTasknameReqDTO
     * @return
     */
    Result<Void> updateTaskStatusIsCompleteByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO);

    /**
     * 删除定时任务
     *
     * @param getTaskByTasknameReqDTO
     * @return
     */
    Result<Void> deleteTask(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO);

}