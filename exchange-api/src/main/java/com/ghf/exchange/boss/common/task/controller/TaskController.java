package com.ghf.exchange.boss.common.task.controller;

import com.ghf.exchange.boss.common.task.dto.*;
import com.ghf.exchange.boss.common.task.service.TaskService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "定时任务接口", tags = {"定时任务接口"})
@RestController
@Lazy
@Slf4j
public class TaskController {

    @Lazy
    @Resource
    private TaskService taskService;

    @ApiOperation(value = "分页搜索任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/pageTask")
    @SneakyThrows
    public Result<PageRespDTO<TaskRespDTO>> pageTask(@RequestBody PageTaskReqDTO pageTaskReqDTO) {
        return taskService.pageTask(pageTaskReqDTO);
    }

    @ApiOperation(value = "列出任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/listTask")
    @SneakyThrows
    public Result<List<TaskRespDTO>> listTask(@RequestBody ListTaskReqDTO listTaskReqDTO) {
        return taskService.listTask(listTaskReqDTO);
    }

    @ApiOperation(value = "根据任务名称获取任务详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/getTaskByTaskname")
    @SneakyThrows
    public Result<TaskRespDTO> getTaskByTaskname(@RequestBody GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        return taskService.getTaskByTaskname(getTaskByTasknameReqDTO);
    }

    @ApiOperation(value = "根据任务名称判断任务是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/existsTaskByTaskname")
    @SneakyThrows
    public Result<Boolean> existsTaskByTaskname(@RequestBody GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        return taskService.existsTaskByTaskname(getTaskByTasknameReqDTO);
    }

    @ApiOperation(value = "新建任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/addTask")
    @SneakyThrows
    public Result<Void> addTask(@RequestBody AddTaskReqDTO addTaskReqDTO) {
        return taskService.addTask(addTaskReqDTO);
    }

    @ApiOperation(value = "更新任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/updateTaskByTaskname")
    @SneakyThrows
    public Result<Void> updateTaskByTaskname(@RequestBody UpdateTaskByTasknameReqDTO updateTaskByTasknameReqDTO) {
        return taskService.updateTaskByTaskname(updateTaskByTasknameReqDTO);
    }

    @ApiOperation(value = "删除任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/deleteTask")
    @SneakyThrows
    public Result<Void> deleteTask(@RequestBody GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        return taskService.deleteTask(getTaskByTasknameReqDTO);
    }
}
