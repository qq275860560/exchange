package com.ghf.exchange.boss.common.task.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.ClientIsLoginReqDTO;
import com.ghf.exchange.boss.authorication.client.dto.LoginClientReqDTO;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.task.dto.*;
import com.ghf.exchange.boss.common.task.entity.QTask;
import com.ghf.exchange.boss.common.task.entity.Task;
import com.ghf.exchange.boss.common.task.enums.TaskInvokeTypeEnum;
import com.ghf.exchange.boss.common.task.enums.TaskStatusEnum;
import com.ghf.exchange.boss.common.task.enums.TaskTypeEnum;
import com.ghf.exchange.boss.common.task.repository.TaskRepository;
import com.ghf.exchange.boss.common.task.service.TaskService;
import com.ghf.exchange.boss.common.tasklog.dto.AddTaskLogForClientReqDTO;
import com.ghf.exchange.boss.common.tasklog.enums.TaskLogStatusEnum;
import com.ghf.exchange.boss.common.tasklog.service.TaskLogService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
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
 * ????????????https://www.iteye.com/blog/zuxiong-2282631
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

        @Lazy
        @Resource
        private ClientService clientService;

        @Value("${security.oauth2.client.client-id}")
        private String clientId;
        @Value("${security.oauth2.client.client-secret}")
        private String clientSecret;

        @Lazy
        @Resource
        private ApplicationContext applicationContext;

        @SneakyThrows
        @Override
        public void execute(JobExecutionContext context) {
            String taskName = context.getJobDetail().getKey().getName();
            GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
            getTaskByTasknameReqDTO.setTaskname(taskName);
            TaskRespDTO taskRespDTO = taskService.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
            int status = taskRespDTO.getStatus();
            if (status == TaskStatusEnum.RUNNING.getCode()) {
                log.info("?????????????????????????????????");
                return;
            }
            if (status == TaskStatusEnum.COMPLETE.getCode()) {
                log.info("??????????????????????????????");
                return;
            }
            //?????????????????????????????????????????????
            taskService.updateTaskStatusIsRunningByTaskname(getTaskByTasknameReqDTO);

            if (taskRespDTO.getTaskInvokeType() == TaskInvokeTypeEnum.REMOTE.getCode()) {
                String requestUrl = taskRespDTO.getRequestUrl();
                String requestMethod = taskRespDTO.getRequestMethod();
                String requestHeader = taskRespDTO.getRequestHeader();
                String inputJson = taskRespDTO.getInputJson();
                int responseStatusCode;
                String responseHeader;
                String outputJson;
                int taskLogStatus;

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
                //?????????????????????????????????????????????
                taskService.updateTaskStatusIsCompleteByTaskname(getTaskByTasknameReqDTO);

                //??????????????????
                AddTaskLogForClientReqDTO addTaskLogForClientReqDTO = new AddTaskLogForClientReqDTO();
                addTaskLogForClientReqDTO.setTaskname(taskName);
                addTaskLogForClientReqDTO.setTaskInvokeType(taskRespDTO.getTaskInvokeType());
                addTaskLogForClientReqDTO.setTasklogname(taskName + ":" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                addTaskLogForClientReqDTO.setRequestUrl(requestUrl);
                addTaskLogForClientReqDTO.setRequestHeader(requestHeader);
                addTaskLogForClientReqDTO.setRequestMethod(requestMethod);
                addTaskLogForClientReqDTO.setInputJson(inputJson);
                addTaskLogForClientReqDTO.setResponseStatusCode(responseStatusCode);
                addTaskLogForClientReqDTO.setResponseHeader(responseHeader);
                addTaskLogForClientReqDTO.setOutputJson(outputJson);
                addTaskLogForClientReqDTO.setStartTime(startTime);
                addTaskLogForClientReqDTO.setEndTime(endTime);
                addTaskLogForClientReqDTO.setDuration(endTime - startTime);
                addTaskLogForClientReqDTO.setStatus(taskLogStatus);
                taskLogService.addTaskLogForClient(addTaskLogForClientReqDTO);
            } else if (taskRespDTO.getTaskInvokeType() == TaskInvokeTypeEnum.LOCAL.getCode()) {

                if (!clientService.clientIsLogin(ClientIsLoginReqDTO.builder().clientId(clientId).build()).getData()) {
                    clientService.loginClient(LoginClientReqDTO.builder().clientId(clientId).clientSecret(clientSecret).build());
                }

                int taskLogStatus;

                long startTime = System.currentTimeMillis();

                String taskReturnJson = null;

                try {
                    Class<?> instanceClazz = Class.forName(taskRespDTO.getTaskClassName());
                    Object instance = applicationContext.getBean(instanceClazz);
                    Class<?> parameterClass = Class.forName(taskRespDTO.getTaskParameterClassName());
                    Object parameterObject = JsonUtil.parse(taskRespDTO.getTaskParameterJson(), parameterClass);
                    Object returnObject = instanceClazz.getMethod(taskRespDTO.getTaskMethodName(), parameterClass).invoke(instance, parameterObject);
                    taskReturnJson = JsonUtil.toJsonString(returnObject);
                    taskLogStatus = TaskLogStatusEnum.SUCCESS.getCode();
                } catch (Exception e) {
                    log.error("", e);
                    taskReturnJson = null;
                    taskLogStatus = TaskLogStatusEnum.FAIL.getCode();
                }
                long endTime = System.currentTimeMillis();
                //?????????????????????????????????????????????
                taskService.updateTaskStatusIsCompleteByTaskname(getTaskByTasknameReqDTO);

                //??????????????????
                AddTaskLogForClientReqDTO addTaskLogForClientReqDTO = new AddTaskLogForClientReqDTO();
                addTaskLogForClientReqDTO.setTaskname(taskName);
                addTaskLogForClientReqDTO.setTaskInvokeType(taskRespDTO.getTaskInvokeType());
                addTaskLogForClientReqDTO.setTasklogname(taskName + ":" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                addTaskLogForClientReqDTO.setTaskClassName(taskRespDTO.getTaskClassName());
                addTaskLogForClientReqDTO.setTaskMethodName(taskRespDTO.getTaskMethodName());
                addTaskLogForClientReqDTO.setTaskParameterClassName(taskRespDTO.getTaskParameterClassName());
                addTaskLogForClientReqDTO.setTaskParameterJson(taskRespDTO.getTaskParameterJson());
                addTaskLogForClientReqDTO.setTaskReturnJson(taskReturnJson);

                addTaskLogForClientReqDTO.setStartTime(startTime);
                addTaskLogForClientReqDTO.setEndTime(endTime);
                addTaskLogForClientReqDTO.setDuration(endTime - startTime);
                addTaskLogForClientReqDTO.setStatus(taskLogStatus);

                taskLogService.addTaskLogForClient(addTaskLogForClientReqDTO);
            }
        }
    }

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        log.info("???????????????task?????????????????????????????????????????????quartz");
        ListTaskReqDTO listTaskReqDTO = new ListTaskReqDTO();
        //TODO ?????????????????????JVM???????????????,
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
        log.info("???????????????task??????");
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
        //???????????????????????????????????????????????????????????????????????????????????????
        predicate.and(QTask.task.status.eq(TaskStatusEnum.PAUSE.getCode()));
        predicate.and(QTask.task.endAt.after(new Date()));
        List<TaskRespDTO> list = taskService.list(predicate, TaskRespDTO.class);
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<TaskRespDTO> getTaskByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        //TODO ????????????
        String taskname = getTaskByTasknameReqDTO.getTaskname();
        Predicate predicate = QTask.task.taskname.eq(taskname);
        Task task = taskService.get(predicate);
        //??????
        TaskRespDTO taskRespDTO = ModelMapperUtil.map(task, TaskRespDTO.class);
        return new Result<>(taskRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsTaskByTaskname(GetTaskByTasknameReqDTO getTaskByTasknameReqDTO) {
        //TODO ????????????
        String taskname = getTaskByTasknameReqDTO.getTaskname();
        Predicate predicate = QTask.task.taskname.eq(taskname);
        boolean b = taskService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Void> addTaskForClient(AddTaskForClientReqDTO addTaskForClientReqDTO) {
        Task task = ModelMapperUtil.map(addTaskForClientReqDTO, Task.class);

        //?????????id
        task.setId(IdUtil.generateLongId());
        //???????????????
        if (!ObjectUtils.isEmpty(addTaskForClientReqDTO.getTaskname())) {
            //???????????????
            String taskname = addTaskForClientReqDTO.getTaskname();
            GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
            getTaskByTasknameReqDTO.setTaskname(taskname);
            boolean b = taskService.existsTaskByTaskname(getTaskByTasknameReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.TASK_EXISTS);
            }
            task.setTaskname(addTaskForClientReqDTO.getTaskname());
        } else {
            //????????????
            task.setTaskname(task.getId() + "");
        }

        //??????????????????
        if (addTaskForClientReqDTO.getTasktype() != TaskTypeEnum.SIMPLY.getCode() && addTaskForClientReqDTO.getTasktype() != TaskTypeEnum.CRON.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_TYPE_NOT_EXISTS);
        }
        //??????????????????
        task.setTasktype(addTaskForClientReqDTO.getTasktype());
        //????????????????????????
        if (addTaskForClientReqDTO.getTaskInvokeType() != TaskInvokeTypeEnum.REMOTE.getCode() && addTaskForClientReqDTO.getTaskInvokeType() != TaskInvokeTypeEnum.LOCAL.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_INVOKE_TYPE_DELETE);
        }
        //????????????????????????
        task.setTaskInvokeType(addTaskForClientReqDTO.getTaskInvokeType());

        if (addTaskForClientReqDTO.getTasktype() == TaskTypeEnum.SIMPLY.getCode()) {
            //????????????????????????
            if (addTaskForClientReqDTO.getTaskRepeatCount() < 0) {
                task.setTaskRepeatCount(0);
            } else {
                task.setTaskRepeatCount(addTaskForClientReqDTO.getTaskRepeatCount());
            }
            //??????????????????
            if (addTaskForClientReqDTO.getTaskInterval() <= 0) {
                task.setTaskInterval(60);
            } else {
                task.setTaskInterval(addTaskForClientReqDTO.getTaskInterval());
            }
        } else if (addTaskForClientReqDTO.getTasktype() == TaskTypeEnum.CRON.getCode()) {
            //??????CRON?????????
            if (ObjectUtils.isEmpty(task.getCronExpression())) {
                return new Result<>(ResultCodeEnum.TASK_CRON_EXPRESSION_NOT_EMPTY);
            }
        }

        if (addTaskForClientReqDTO.getTaskInvokeType() == TaskInvokeTypeEnum.REMOTE.getCode()) {
            //??????HTTP????????????
            if (ObjectUtils.isEmpty(addTaskForClientReqDTO.getRequestUrl())) {
                return new Result<>(ResultCodeEnum.TASK_REQUEST_URL_NOT_EMPTY);
            }
            //??????HTTP????????????
            task.setRequestUrl(addTaskForClientReqDTO.getRequestUrl());
            //??????HTTP????????????
            if (ObjectUtils.isEmpty(addTaskForClientReqDTO.getRequestHeader())) {
                return new Result<>(ResultCodeEnum.TASK_REQUEST_HEADER_NOT_EMPTY);
            }
            //??????HTTP????????????
            task.setRequestHeader(addTaskForClientReqDTO.getRequestHeader());
            //??????HTTP????????????
            if (ObjectUtils.isEmpty(addTaskForClientReqDTO.getRequestMethod())) {
                task.setRequestMethod(HttpMethod.POST.name());
            } else {
                task.setRequestMethod(addTaskForClientReqDTO.getRequestMethod());
            }
            //??????HTTP?????????
            task.setInputJson(addTaskForClientReqDTO.getInputJson());

        } else if (addTaskForClientReqDTO.getTaskInvokeType() == TaskInvokeTypeEnum.LOCAL.getCode()) {
            //???????????????
            if (ObjectUtils.isEmpty(addTaskForClientReqDTO.getTaskClassName())) {
                return new Result<>(ResultCodeEnum.TASK_CLASS_NAME_NOT_EMPTY);
            }
            //???????????????
            task.setTaskClassName(addTaskForClientReqDTO.getTaskClassName());
            //??????????????????
            if (ObjectUtils.isEmpty(addTaskForClientReqDTO.getTaskMethodName())) {
                return new Result<>(ResultCodeEnum.TASK_METHOD_NAME_NOT_EMPTY);
            }
            //??????????????????
            task.setTaskMethodName(addTaskForClientReqDTO.getTaskMethodName());
            //????????????DTO??????
            task.setTaskParameterClassName(addTaskForClientReqDTO.getTaskParameterClassName());
            //????????????DTO???json
            task.setTaskParameterJson(addTaskForClientReqDTO.getTaskParameterJson());

        }

        //????????????????????????
        task.setStartAt(addTaskForClientReqDTO.getStartAt());
        //????????????????????????
        task.setEndAt(addTaskForClientReqDTO.getEndAt());
        //??????????????????
        task.setTaskdesc(addTaskForClientReqDTO.getTaskdesc());
        //????????????
        task.setStatus(TaskStatusEnum.PAUSE.getCode());
        //?????????????????????
        taskService.add(task);

        //??????quartz????????????
        int tasktype = task.getTasktype();
        int taskRepeatCount = task.getTaskRepeatCount();
        int taskInterval = task.getTaskInterval();
        String cronExpression = task.getCronExpression();
        Date startAt = task.getStartAt();
        Date endAt = task.getEndAt();

        //TODO ???????????????JVM????????????????????????
        ScheduleBuilder scheduleBuilder = null;
        if (tasktype == TaskTypeEnum.SIMPLY.getCode()) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(taskInterval)
                    .withRepeatCount(taskRepeatCount);
        } else {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        }
        JobDetail jobDetail = JobBuilder.newJob(MyTask.class).withIdentity(task.getTaskname(), TASK_GROUP).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getTaskname(), TASK_GROUP).withSchedule(scheduleBuilder)
                .startAt(startAt).endAt(endAt).build();
        scheduler.scheduleJob(jobDetail, trigger);

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateTaskByTasknameForClient(UpdateTaskByTasknameForClientReqDTO updateTaskByTasknameForClientReqDTO) {
        String taskname = updateTaskByTasknameForClientReqDTO.getTaskname();

        GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
        getTaskByTasknameReqDTO.setTaskname(taskname);
        TaskRespDTO taskRespDTO = this.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Task afterTask = ModelMapperUtil.map(taskRespDTO, Task.class);
        if (afterTask.getStatus() == TaskStatusEnum.RUNNING.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_RUNNGIN);
        } else if (afterTask.getStatus() == TaskStatusEnum.COMPLETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_COMPLETE);
        } else if (afterTask.getStatus() == TaskStatusEnum.DELETE.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_STATUS_DELETE);
        }

//??????????????????
        if (updateTaskByTasknameForClientReqDTO.getTasktype() != TaskTypeEnum.SIMPLY.getCode() && updateTaskByTasknameForClientReqDTO.getTasktype() != TaskTypeEnum.CRON.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_TYPE_NOT_EXISTS);
        }
        //??????????????????
        afterTask.setTasktype(updateTaskByTasknameForClientReqDTO.getTasktype());
        //????????????????????????
        if (updateTaskByTasknameForClientReqDTO.getTaskInvokeType() != TaskInvokeTypeEnum.REMOTE.getCode() && updateTaskByTasknameForClientReqDTO.getTaskInvokeType() != TaskInvokeTypeEnum.LOCAL.getCode()) {
            return new Result<>(ResultCodeEnum.TASK_INVOKE_TYPE_DELETE);
        }
        //????????????????????????
        afterTask.setTaskInvokeType(updateTaskByTasknameForClientReqDTO.getTaskInvokeType());

        if (updateTaskByTasknameForClientReqDTO.getTasktype() == TaskTypeEnum.SIMPLY.getCode()) {
            //????????????????????????
            if (updateTaskByTasknameForClientReqDTO.getTaskRepeatCount() < 0) {
                afterTask.setTaskRepeatCount(0);
            } else {
                afterTask.setTaskRepeatCount(updateTaskByTasknameForClientReqDTO.getTaskRepeatCount());
            }
            //??????????????????
            if (updateTaskByTasknameForClientReqDTO.getTaskInterval() <= 0) {
                afterTask.setTaskInterval(60);
            } else {
                afterTask.setTaskInterval(updateTaskByTasknameForClientReqDTO.getTaskInterval());
            }
        } else if (updateTaskByTasknameForClientReqDTO.getTasktype() == TaskTypeEnum.CRON.getCode()) {
            //??????CRON?????????
            if (ObjectUtils.isEmpty(afterTask.getCronExpression())) {
                return new Result<>(ResultCodeEnum.TASK_CRON_EXPRESSION_NOT_EMPTY);
            }
        }

        if (updateTaskByTasknameForClientReqDTO.getTaskInvokeType() == TaskInvokeTypeEnum.REMOTE.getCode()) {
            //??????HTTP????????????
            if (ObjectUtils.isEmpty(updateTaskByTasknameForClientReqDTO.getRequestUrl())) {
                return new Result<>(ResultCodeEnum.TASK_REQUEST_URL_NOT_EMPTY);
            }
            //??????HTTP????????????
            afterTask.setRequestUrl(updateTaskByTasknameForClientReqDTO.getRequestUrl());
            //??????HTTP????????????
            if (ObjectUtils.isEmpty(updateTaskByTasknameForClientReqDTO.getRequestHeader())) {
                return new Result<>(ResultCodeEnum.TASK_REQUEST_HEADER_NOT_EMPTY);
            }
            //??????HTTP????????????
            afterTask.setRequestHeader(updateTaskByTasknameForClientReqDTO.getRequestHeader());
            //??????HTTP????????????
            if (ObjectUtils.isEmpty(updateTaskByTasknameForClientReqDTO.getRequestMethod())) {
                afterTask.setRequestMethod(HttpMethod.POST.name());
            } else {
                afterTask.setRequestMethod(updateTaskByTasknameForClientReqDTO.getRequestMethod());
            }
            //??????HTTP?????????
            afterTask.setInputJson(updateTaskByTasknameForClientReqDTO.getInputJson());

        } else if (updateTaskByTasknameForClientReqDTO.getTaskInvokeType() == TaskInvokeTypeEnum.LOCAL.getCode()) {
            //???????????????
            if (ObjectUtils.isEmpty(updateTaskByTasknameForClientReqDTO.getTaskClassName())) {
                return new Result<>(ResultCodeEnum.TASK_CLASS_NAME_NOT_EMPTY);
            }
            //???????????????
            afterTask.setTaskClassName(updateTaskByTasknameForClientReqDTO.getTaskClassName());
            //??????????????????
            if (ObjectUtils.isEmpty(updateTaskByTasknameForClientReqDTO.getTaskMethodName())) {
                return new Result<>(ResultCodeEnum.TASK_METHOD_NAME_NOT_EMPTY);
            }
            //??????????????????
            afterTask.setTaskMethodName(updateTaskByTasknameForClientReqDTO.getTaskMethodName());
            //????????????DTO??????
            afterTask.setTaskParameterClassName(updateTaskByTasknameForClientReqDTO.getTaskParameterClassName());
            //????????????DTO???json
            afterTask.setTaskParameterJson(updateTaskByTasknameForClientReqDTO.getTaskParameterJson());

        }

        //????????????????????????
        afterTask.setStartAt(updateTaskByTasknameForClientReqDTO.getStartAt());
        //????????????????????????
        afterTask.setEndAt(updateTaskByTasknameForClientReqDTO.getEndAt());
        //??????????????????
        afterTask.setTaskdesc(updateTaskByTasknameForClientReqDTO.getTaskdesc());
        //????????????
        afterTask.setStatus(TaskStatusEnum.PAUSE.getCode());
        //?????????????????????
        this.update(afterTask);

        //??????trigger
        int tasktype = afterTask.getTasktype();
        int taskRepeatCount = afterTask.getTaskRepeatCount();
        int taskInterval = afterTask.getTaskInterval();
        String cronExpression = afterTask.getCronExpression();
        Date startAt = afterTask.getStartAt();
        Date endAt = afterTask.getEndAt();

//TODO ????????????jvm??????????????????????????????????????????JVM????????????????????????????????????????????????,?????????????????????????????????jvm????????????????????????JVM
        ScheduleBuilder scheduleBuilder = null;
        if (tasktype == TaskTypeEnum.SIMPLY.getCode()) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(taskInterval)
                    .withRepeatCount(taskRepeatCount);
        } else {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        }

        TriggerKey triggerKey = new TriggerKey(taskname, TASK_GROUP);
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
        Task afterTask = ModelMapperUtil.map(taskRespDTO, Task.class);

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
        Task afterTask = ModelMapperUtil.map(taskRespDTO, Task.class);

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
        Task afterTask = ModelMapperUtil.map(taskRespDTO, Task.class);

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