package quartzdemo;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.common.task.dto.AddTaskReqDTO;
import com.ghf.exchange.boss.common.task.dto.GetTaskByTasknameReqDTO;
import com.ghf.exchange.boss.common.task.dto.TaskRespDTO;
import com.ghf.exchange.boss.common.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class TaskServiceTest {

    @Resource
    private TaskService taskService;

    //curl -i -X POST 'http://localhost:8080/api/task/addTask'   -d '{"taskname":"mytaskname","taskdesc":"mytaskdesc","tasktype":0,"taskRepeatCount":0,"taskInterval":60,"requestUrl":"http://localhost:8080/api/user/login","requestHeader":"{\"Content-Type\":\"application/json\"}","inputJson":"{\"username\":\"admin\",\"password\":\"123456\"}","startAt":"2021-08-31 19:55:00","endAt":"2021-08-31 19:55:09"}'  -H "Content-Type: application/json"  -H "Authorization:bearer $token"

    //编写代码前，请详细阅读api文档：业务支撑系统-公共服务子系统-定时任务-新增任务
    //定时任务的http接口，没有用户概念，不需要认证，要把url拦截去除，配置文件中有一个参数none.authentication.urls可配置不需要认证和授权的url
    //未来定时任务是在内网中运行，http链接是微服务的http链接，不必担心安全问题
    @Test
    public void addTaskTest() {

        AddTaskReqDTO addTaskReqDTO = new AddTaskReqDTO();
        addTaskReqDTO.setTaskname("task-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        addTaskReqDTO.setTaskdesc("在startAt-endAt时间段内，指定执行次数和间隔方式访问登录用户接口");
        addTaskReqDTO.setStartAt(new Date(System.currentTimeMillis() + 1000 * 120));//120秒钟后开始启动任务
        addTaskReqDTO.setEndAt(new Date(System.currentTimeMillis() + 1000 * 600));//600秒钟后结束整个任务

        addTaskReqDTO.setTasktype(0);//0代表简单方式，也就是指定时间段，任务执行多少次，此时taskRepeatCount和taskInterval字段才有效
        addTaskReqDTO.setTaskRepeatCount(0);//重复执行次数，0代表重复0次，也就是总共执行一次。很多时候业务只需要执行一次即可，但有时候为了防止tomcat中途宕机，也可能执行多次，
        // 但业务端必须做好幂等，只要第一次执行成功了，后面的执行忽略掉即可
        addTaskReqDTO.setTaskInterval(60);//如果repeatCount=0，也就是任务总共执行一次，此字段无意义，因为在startAt那时候任务已经触发，彻底执行完了

        addTaskReqDTO.setRequestUrl("http://localhost:8080/api/user/login");
        addTaskReqDTO.setRequestHeader("{\"Content-Type\":\"application/json\"}");
        addTaskReqDTO.setRequestMethod("POST");
        addTaskReqDTO.setInputJson("{\"username\":\"admin\",\"password\":\"123456\"}");
        taskService.addTask(addTaskReqDTO);

        GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
        getTaskByTasknameReqDTO.setTaskname(addTaskReqDTO.getTaskname());
        TaskRespDTO taskOutput = taskService.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Assert.assertTrue(taskOutput.getTaskname().equals(addTaskReqDTO.getTaskname()));

        //清理
        taskService.deleteTask(getTaskByTasknameReqDTO);

        Assert.assertTrue(1 == 1);
    }

    //@Test
    public void addTaskTest2() {

        AddTaskReqDTO addTaskReqDTO = new AddTaskReqDTO();
        addTaskReqDTO.setTaskname("task-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        addTaskReqDTO.setTaskdesc("在startAt-endAt时间段内，cron表达式触发访问登录用户接口");
        addTaskReqDTO.setStartAt(new Date(System.currentTimeMillis() + 1000 * 120));//120秒钟后开始启动任务
        addTaskReqDTO.setEndAt(new Date(System.currentTimeMillis() + 1000 * 600));//600秒钟后结束整个任务

        addTaskReqDTO.setTasktype(1);//1代表cron方式，在指定时间段，按照cron表达式执行，此时cronExpression字段才有效
        addTaskReqDTO.setCronExpression("0 0 1 * * ?");

        addTaskReqDTO.setRequestUrl("http://localhost:8080/api/user/login");
        addTaskReqDTO.setRequestHeader("{\"Content-Type\":\"application/json\"}");
        addTaskReqDTO.setRequestMethod("POST");
        addTaskReqDTO.setInputJson("{\"username\":\"admin\",\"password\":\"123456\"}");
        taskService.addTask(addTaskReqDTO);

        GetTaskByTasknameReqDTO getTaskByTasknameReqDTO = new GetTaskByTasknameReqDTO();
        getTaskByTasknameReqDTO.setTaskname(addTaskReqDTO.getTaskname());
        TaskRespDTO taskOutput = taskService.getTaskByTaskname(getTaskByTasknameReqDTO).getData();
        Assert.assertTrue(taskOutput.getTaskname().equals(addTaskReqDTO.getTaskname()));

        //清理
        taskService.deleteTask(getTaskByTasknameReqDTO);

        Assert.assertTrue(1 == 1);

    }

}
