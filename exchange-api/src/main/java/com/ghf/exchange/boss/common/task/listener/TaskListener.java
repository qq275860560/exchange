package com.ghf.exchange.boss.common.task.listener;

import com.ghf.exchange.boss.common.task.dto.AddTaskForClientReqDTO;
import com.ghf.exchange.boss.common.task.service.TaskService;
import com.ghf.exchange.otc.order.event.AddOrderEvent;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class TaskListener {

    @Lazy
    @Resource
    private TaskService taskService;

    //TODO 也可以放入rabbitmq死信队列
    @Async
    @EventListener
    public void onAddOrderEvent(AddOrderEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event));
        AddTaskForClientReqDTO addTaskForClientReqDTO = ModelMapperUtil.map(event, AddTaskForClientReqDTO.class);

        taskService.addTaskForClient(addTaskForClientReqDTO);

    }

}