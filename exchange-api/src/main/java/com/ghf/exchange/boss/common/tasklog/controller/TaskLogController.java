package com.ghf.exchange.boss.common.tasklog.controller;

import com.ghf.exchange.boss.common.tasklog.dto.*;
import com.ghf.exchange.boss.common.tasklog.service.TaskLogService;
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
@Api(value = "任务日志接口", tags = {"任务日志接口"})
@RestController
@Lazy
@Slf4j
public class TaskLogController {

    @Lazy
    @Resource
    private TaskLogService taskLogService;

    @ApiOperation(value = "分页搜索任务日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/pageTaskLog")
    @SneakyThrows
    public Result<PageRespDTO<TaskLogRespDTO>> pageTaskLog(@RequestBody PageTaskLogReqDTO pageTaskLogReqDTO) {
        return taskLogService.pageTaskLog(pageTaskLogReqDTO);
    }

    @ApiOperation(value = "列出任务日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/listTaskLog")
    @SneakyThrows
    public Result<List<TaskLogRespDTO>> listTaskLog(@RequestBody ListTaskLogReqDTO listTaskLogReqDTO) {
        return taskLogService.listTaskLog(listTaskLogReqDTO);
    }

    @ApiOperation(value = "根据任务日志名称获取任务日志详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/getTaskLogByTasklogname")
    @SneakyThrows
    public Result<TaskLogRespDTO> getTaskLogByTasklogname(@RequestBody GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO) {
        return taskLogService.getTaskLogByTasklogname(getTaskLogByTasklognameReqDTO);
    }

    @ApiOperation(value = "根据任务日志名称判断任务日志是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/existsTaskLogByTasklogname")
    @SneakyThrows
    public Result<Boolean> existsTaskLogByTasklogname(@RequestBody GetTaskLogByTasklognameReqDTO getTaskLogByTasklognameReqDTO) {
        return taskLogService.existsTaskLogByTasklogname(getTaskLogByTasklognameReqDTO);
    }

    @ApiOperation(value = "新建任务日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/addTaskLog")
    @SneakyThrows
    public Result<Void> addTaskLog(@RequestBody AddTaskLogReqDTO addTaskLogReqDTO) {
        return taskLogService.addTaskLog(addTaskLogReqDTO);
    }
}
