package com.ghf.exchange.boss.common.task.service.impl;

import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.task.dto.*;
import com.ghf.exchange.boss.common.task.entity.QTask;
import com.ghf.exchange.boss.common.task.entity.Task;
import com.ghf.exchange.boss.common.task.enums.TaskStatusEnum;
import com.ghf.exchange.boss.common.task.enums.TaskTypeEnum;
import com.ghf.exchange.boss.common.task.repository.TaskRepository;
import com.ghf.exchange.boss.common.task.service.TaskService;
import com.ghf.exchange.boss.common.tasklog.dto.AddTaskLogReqDTO;
import com.ghf.exchange.boss.common.tasklog.enums.TaskLogStatusEnum;
import com.ghf.exchange.boss.common.tasklog.service.TaskLogService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.JsonUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 参考实现https://www.iteye.com/blog/zuxiong-2282631
 *
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService, ApplicationRunner {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private TaskService taskService;

    @Lazy
    @Resource
    private Scheduler scheduler;
    private static final String TASK_GROUP = "TaskGroup";

    public TaskServiceImpl(TaskRepository repository) {
        super(repository);
    }

    @Component
    @Lazy
    @Slf4j
    public static class MyTask implements Job {
        @Lazy
        @Resource
        private TaskService taskService;

        @Lazy
        @Resource
        private TaskLogService taskLogService;

        @SneakyThrows
        @Override
        public void execute(JobExecutionContext context) {
            String taskName = context.getJobDetail().getKey().getName();
            GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
            getTaskByTasknameReqDTO.setTaskname(taskName);
            TaskRespDTO taskRespDTO = taskService.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
            int status = taskRespDTO.getStatus();
            if (status == TaskStatusEnum.RUNNING.getCode()) {
                log.info("任务已经运行中，不执行");
                return;
            }
            if (status == TaskStatusEnum.COMPLETE.getCode()) {
                log.info("任务已经完成，不执行");
                return;
            }
            String requestUrl = taskRespDTO.getRequestUrl();
            String requestMethod = taskRespDTO.getRequestMethod();
            String requestHeader = taskRespDTO.getRequestHeader();
            String inputJson = taskRespDTO.getInputJson();
            int responseStatusCode;
            String responseHeader;
            String outputJson;
            int taskLogStatus;

            //把数据库中的任务修改成运行状态
            taskService.updateTaskStatusIsRunningByTaskname(getTaskByTasknameReqDTO);
            long startTime = System.currentTimeMillis();
            try {
                ResponseEntity<String> response = new RestTemplate().exchange(requestUrl, HttpMethod.resolve(requestMethod),
                        new HttpEntity<>(inputJson,
                                new HttpHeaders() {
                                    {
                                        if (!ObjectUtils.isEmpty(requestHeader)) {
                                            Map<String, String> requestHeaderMap = JsonUtil.parse(requestHeader, Map.class);
                                            for (Entry<String, String> entry : requestHeaderMap.entrySet()) {
                                                add(entry.getKey(), entry.getValue());
                                            }
                                        }

                                    }
                                }),
                        String.class);
                responseStatusCode = response.getStatusCodeValue();
                responseHeader = JsonUtil.toJsonString(response.getHeaders());
                outputJson = response.getBody();
                taskLogStatus = TaskLogStatusEnum.SUCCESS.getCode();
            } catch (Exception e) {
                log.error("", e);
                responseStatusCode = -1;
                responseHeader = null;
                outputJson = ExceptionUtils.getStackTrace(e);
                taskLogStatus = TaskLogStatusEnum.FAIL.getCode();
            }
            long endTime = System.currentTimeMillis();
            //把数据库中的任务修改成完成状态
            taskService.updateTaskStatusIsCompleteByTaskname(getTaskByTasknameReqDTO);
            //写入任务日志
            AddTaskLogReqDTO addTaskLogReqDTO = new AddTaskLogReqDTO();
            addTaskLogReqDTO.setTaskname(taskName);
            addTaskLogReqDTO.setTasklogname(taskName + ":" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            addTaskLogReqDTO.setRequestUrl(requestUrl);
            addTaskLogReqDTO.setRequestHeader(requestHeader);
            addTaskLogReqDTO.setRequestMethod(requestMethod);
            addTaskLogReqDTO.setInputJson(inputJson);
            addTaskLogReqDTO.setResponseStatusCode(responseStatusCode);
            addTaskLogReqDTO.setResponseHeader(responseHeader);
            addTaskLogReqDTO.setOutputJson(outputJson);
            addTaskLogReqDTO.setStartTime(startTime);
            addTaskLogReqDTO.setEndTime(endTime);
            addTaskLogReqDTO.setDuration(endTime - startTime);
            addTaskLogReqDTO.setStatus(taskLogStatus);
            taskLogService.addTaskLog(addTaskLogReqDTO);
        }
    }

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        log.info("初始化任务task开始：所有暂停中的任务开始加入quartz");
        ListTaskReqDTO listTaskReqDTO = new ListTaskReqDTO();
        List<TaskRespDTO> list = taskService.listTask(listTaskReqDTO).getData();

        for (TaskRespDTO taskRespDTO : list) {
            String taskname = taskRespDTO.getTaskname();
            int tasktype = taskRespDTO.getTasktype();
            int taskRepeatCount = taskRespDTO.getTaskRepeatCount();
            int taskInterval = taskRespDTO.getTaskInterval();
            String cronExpression = taskRespDTO.getCronExpression();
            Date startAt = taskRespDTO.getStartAt();
            Date endAt = taskRespDTO.getEndAt();

            ScheduleBuilder scheduleBuilder = null;
            if (tasktype == TaskTypeEnum.SIMPLY.getCode()) {
                scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(taskInterval)
                        .withRepeatCount(taskRepeatCount);
            } else {
                scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            }
            JobDetail jobDetail = JobBuilder.newJob(MyTask.class).withIdentity(taskname, TASK_GROUP).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(taskname, TASK_GROUP).withSchedule(scheduleBuilder)
                    .startAt(startAt).endAt(endAt).build();
            scheduler.scheduleJob(jobDetail, trigger);

        }
        log.info("初始化任务task结束");
    }

    @Override
    @SneakyThrows
    public Result<PageRespDTO<TaskRespDTO>> pageTask(PageTaskReqDTO pageTaskReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageTaskReqDTO.getTaskname())) {
            predicate.and(QTask.task.taskname.contains(pageTaskReqDTO.getTaskname()));
        }
        if (!ObjectUtils.isEmpty(pageTaskReqDTO.getTaskdesc())) {
            predicate.and(QTask.task.taskdesc.contains(pageTaskReqDTO.getTaskdesc()));
        }
        PageRespDTO<TaskRespDTO> pageRespDTO = taskService.page(predicate, pageTaskReqDTO, TaskRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<TaskRespDTO>> listTask(ListTaskReqDTO listTaskReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listTaskReqDTO.getTaskname())) {
            predicate.and(QTask.task.taskname.contains(listTaskReqDTO.getTaskname()));
        }
        if (!ObjectUtils.isEmpty(listTaskReqDTO.getTaskdesc())) {
            predicate.and(QTask.task.taskdesc.contains(listTaskReqDTO.getTaskdesc()));
        }
        //此接口只能获取暂停中的任务，并且当前时间小于截止时间的任务
        predicate.and(QTask.task.status.eq(TaskStatusEnum.PAUSE.getCode()));
        predicate.and(QTask.task.endAt.after(new Date()));
        List<TaskRespDTO> list = taskService.list(predicate, TaskRespDTO.class);
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<TaskRespDTO> getTaskByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        //TODO 权限判断
        String taskname = getTaskByTasknameReqDTO.getTaskname();
        Predicate predicate = QTask.task.taskname.eq(taskname);
        Task task = taskService.get(predicate);
        //返回
        TaskRespDTO taskRespDTO = AutoMapUtils.map(task, TaskRespDTO.class);
        return new Result<>(taskRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsTaskByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        //TODO 权限判断
        String taskname = getTaskByTasknameReqDTO.getTaskname();
        Predicate predicate = QTask.task.taskname.eq(taskname);
        boolean b = taskService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Void> addTask(AddTaskReqDTO addTaskReqDTO) {
        Task task = AutoMapUtils.map(addTaskReqDTO, Task.class);
        //TODO 获取当前调用客户端详情
        //判断唯一性
        String taskname = task.getTaskname();
        GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
        getTaskByTasknameReqDTO.setTaskname(taskname);
        boolean b = taskService.existsTaskByTaskname(getTaskByTasknameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.TASK_EXISTS);
        }
        //初始化id
        task.setId(IdUtil.generateLongId());

        if (task.getTasktype() != TaskTypeEnum.SIMPLY.getCode() && task.getTasktype() != TaskTypeEnum.CRON.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_TYPE_DELETE);
        } else {

        }
        if (task.getTaskRepeatCount() < 0) {
            task.setTaskRepeatCount(0);
        }
        if (task.getTaskInterval() <= 0) {
            task.setTaskInterval(60);
        }

        if (ObjectUtils.isEmpty(task.getRequestMethod())) {
            task.setRequestMethod(HttpMethod.POST.name());
        }
        //状态设置为暂停
        task.setStatus(TaskStatusEnum.PAUSE.getCode());
        //新增到数据库
        taskService.add(task);

        //TODO 新增quartz中的任务
        int tasktype = task.getTasktype();
        int taskRepeatCount = task.getTaskRepeatCount();
        int taskInterval = task.getTaskInterval();
        String cronExpression = task.getCronExpression();
        Date startAt = task.getStartAt();
        Date endAt = task.getEndAt();

        ScheduleBuilder scheduleBuilder = null;
        if (tasktype == TaskTypeEnum.SIMPLY.getCode()) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(taskInterval)
                    .withRepeatCount(taskRepeatCount);
        } else {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        }
        JobDetail jobDetail = JobBuilder.newJob(MyTask.class).withIdentity(taskname, TASK_GROUP).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(taskname, TASK_GROUP).withSchedule(scheduleBuilder)
                .startAt(startAt).endAt(endAt).build();
        scheduler.scheduleJob(jobDetail, trigger);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateTaskByTaskname(UpdateTaskByTasknameReqDTO updateTaskByTasknameReqDTO) {
        String taskname = updateTaskByTasknameReqDTO.getTaskname();

        GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
        getTaskByTasknameReqDTO.setTaskname(taskname);
        TaskRespDTO taskRespDTO = this.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Task afterTask = AutoMapUtils.map(taskRespDTO, Task.class);
        if (afterTask.getStatus() == TaskStatusEnum.RUNNING.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_RUNNGIN);
        } else if (afterTask.getStatus() == TaskStatusEnum.COMPLETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_COMPLETE);
        } else if (afterTask.getStatus() == TaskStatusEnum.DELETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_DELETE);
        }
        afterTask.setTaskdesc(updateTaskByTasknameReqDTO.getTaskdesc());

        if (updateTaskByTasknameReqDTO.getTasktype() != TaskTypeEnum.SIMPLY.getCode() && updateTaskByTasknameReqDTO.getTasktype() != TaskTypeEnum.CRON.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_TYPE_DELETE);
        } else {
            afterTask.setTasktype(updateTaskByTasknameReqDTO.getTasktype());
        }
        afterTask.setTaskRepeatCount(updateTaskByTasknameReqDTO.getTaskRepeatCount() < 0 ? 0 : updateTaskByTasknameReqDTO.getTaskRepeatCount());
        afterTask.setTaskInterval(updateTaskByTasknameReqDTO.getTaskInterval() <= 0 ? 60 : updateTaskByTasknameReqDTO.getTaskInterval());
        afterTask.setCronExpression(updateTaskByTasknameReqDTO.getCronExpression());
        afterTask.setStartAt(updateTaskByTasknameReqDTO.getStartAt());
        afterTask.setEndAt(updateTaskByTasknameReqDTO.getEndAt());

        afterTask.setRequestUrl(updateTaskByTasknameReqDTO.getRequestUrl());
        afterTask.setRequestMethod(updateTaskByTasknameReqDTO.getRequestMethod());
        afterTask.setRequestHeader(updateTaskByTasknameReqDTO.getRequestHeader());
        afterTask.setInputJson(updateTaskByTasknameReqDTO.getInputJson());
        afterTask.setStatus(TaskStatusEnum.PAUSE.getCode());
        this.update(afterTask);

        //更新trigger
        int tasktype = afterTask.getTasktype();
        int taskRepeatCount = afterTask.getTaskRepeatCount();
        int taskInterval = afterTask.getTaskInterval();
        String cronExpression = afterTask.getCronExpression();
        Date startAt = afterTask.getStartAt();
        Date endAt = afterTask.getEndAt();

        TriggerKey triggerKey = new TriggerKey(taskname, TASK_GROUP);
        ScheduleBuilder scheduleBuilder = null;
        if (tasktype == TaskTypeEnum.SIMPLY.getCode()) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(taskInterval)
                    .withRepeatCount(taskRepeatCount);
        } else {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        }

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder)
                .startAt(startAt).endAt(endAt).build();
        scheduler.rescheduleJob(triggerKey, trigger);

        return new Result<>(ResultCodeEnum.OK);

    }

    @Override
    @SneakyThrows
    public Result<Void> updateTaskStatusIsRunningByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {

        String taskname = getTaskByTasknameReqDTO.getTaskname();

        TaskRespDTO taskRespDTO = this.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Task afterTask = AutoMapUtils.map(taskRespDTO, Task.class);

        if (afterTask.getStatus() == TaskStatusEnum.RUNNING.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_RUNNGIN);
        } else if (afterTask.getStatus() == TaskStatusEnum.COMPLETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_COMPLETE);
        } else if (afterTask.getStatus() == TaskStatusEnum.DELETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_DELETE);
        }
        afterTask.setStatus(TaskStatusEnum.RUNNING.getCode());
        this.update(afterTask);
        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateTaskStatusIsCompleteByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {

        String taskname = getTaskByTasknameReqDTO.getTaskname();

        TaskRespDTO taskRespDTO = this.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Task afterTask = AutoMapUtils.map(taskRespDTO, Task.class);

        if (afterTask.getStatus() == TaskStatusEnum.COMPLETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_COMPLETE);
        } else if (afterTask.getStatus() == TaskStatusEnum.DELETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_DELETE);
        }
        afterTask.setStatus(TaskStatusEnum.COMPLETE.getCode());
        this.update(afterTask);
        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> deleteTask(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        String taskname = getTaskByTasknameReqDTO.getTaskname();

        TaskRespDTO taskRespDTO = this.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Task afterTask = AutoMapUtils.map(taskRespDTO, Task.class);

        if (afterTask.getStatus() == TaskStatusEnum.RUNNING.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_RUNNGIN);
        } else if (afterTask.getStatus() == TaskStatusEnum.COMPLETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_COMPLETE);
        } else if (afterTask.getStatus() == TaskStatusEnum.DELETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_DELETE);
        }
        afterTask.setStatus(TaskStatusEnum.DELETE.getCode());
        this.update(afterTask);
        JobKey jobKey = new JobKey(taskname, TASK_GROUP);
        scheduler.deleteJob(jobKey);
        return new Result<>(ResultCodeEnum.OK);
    }

}