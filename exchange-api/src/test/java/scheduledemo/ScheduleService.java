package scheduledemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author jiangyuanlin@163.com
 */
@EnableScheduling
@Slf4j
public class ScheduleService {

    @Scheduled(cron = "0 0/30 * * * ?")
    public void myTask() {
        log.info("任务{}开始执行", "myTask");

        //处理业务逻辑
        log.info("任务{}执行完毕", "myTask");
    }

}
