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

    @ApiOperation(value = "分页搜索定时任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/pageTask")
    @SneakyThrows
    public Result<PageRespDTO<TaskRespDTO>> pageTask(@RequestBody PageTaskReqDTO pageTaskReqDTO) {
        return taskService.pageTask(pageTaskReqDTO);
    }

    @ApiOperation(value = "列出定时任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/listTask")
    @SneakyThrows
    public Result<List<TaskRespDTO>> listTask(@RequestBody ListTaskReqDTO listTaskReqDTO) {
        return taskService.listTask(listTaskReqDTO);
    }

    @ApiOperation(value = "根据任务名称获取定时任务详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/getTaskByTaskname")
    @SneakyThrows
    public Result<TaskRespDTO> getTaskByTaskname(@RequestBody GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        return taskService.getTaskByTaskname(getTaskByTasknameReqDTO);
    }

    @ApiOperation(value = "根据任务名称判断定时任务是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/existsTaskByTaskname")
    @SneakyThrows
    public Result<Boolean> existsTaskByTaskname(@RequestBody GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        return taskService.existsTaskByTaskname(getTaskByTasknameReqDTO);
    }

    @ApiOperation(value = "微服务客户端新建定时任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/addTaskForClient")
    @SneakyThrows
    public Result<Void> addTasForClientk(@RequestBody AddTaskForClientReqDTO addTaskForClientReqDTO) {
        return taskService.addTaskForClient(addTaskForClientReqDTO);
    }

    @ApiOperation(value = "微服务客户端更新定时任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/updateTaskByTasknameForClient")
    @SneakyThrows
    public Result<Void> updateTaskByTasknameForClient(@RequestBody UpdateTaskByTasknameForClientReqDTO updateTaskByTasknameForClientReqDTO) {
        return taskService.updateTaskByTasknameForClient(updateTaskByTasknameForClientReqDTO);
    }

    @ApiOperation(value = "删除定时任务", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/task/deleteTask")
    @SneakyThrows
    public Result<Void> deleteTask(@RequestBody GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        return taskService.deleteTask(getTaskByTasknameReqDTO);
    }
}
