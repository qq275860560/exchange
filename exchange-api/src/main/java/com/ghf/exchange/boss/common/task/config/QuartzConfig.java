package com.ghf.exchange.boss.common.task.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @author jiangyuanlin@163.com
 */
@Configuration
@Slf4j
public class QuartzConfig {

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory() {
            @Autowired
            private AutowireCapableBeanFactory autowireCapableBeanFactory;

            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                Object jobInstance = super.createJobInstance(bundle);
                autowireCapableBeanFactory.autowireBean(jobInstance);
                return jobInstance;
            }

        };
    }

    @Bean
    public StdSchedulerFactory stdSchedulerFactory() {
        return new StdSchedulerFactory();
    }

    @Bean
    public Scheduler scheduler(StdSchedulerFactory stdSchedulerFactory, SpringBeanJobFactory springBeanJobFactory)
            throws Exception {
        // 也可以通过SchedulerFactoryBean获取Scheduler
        Scheduler scheduler = stdSchedulerFactory.getScheduler();
        scheduler.setJobFactory(springBeanJobFactory);

        scheduler.getListenerManager().addTriggerListener(new TriggerListener() {

            @Override
            public String getName() {
                return "全局trigger监听器";
            }

            @Override
            public void triggerFired(Trigger trigger, JobExecutionContext context) {
                try {

                    log.info("triggerFired事件，jobName=" + trigger.getJobKey().getName() + "，triggerName=" + trigger.getKey().getName() + ",状态="
                            + scheduler.getTriggerState(trigger.getKey()).name());
                } catch (Exception e) {
                    log.error("", e);
                }

            }

            @Override
            public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
                try {
                    log.info("vetoJobExecution事件，jobName=" + trigger.getJobKey().getName() + "，triggerName=" + trigger.getKey().getName() + ",状态="
                            + scheduler.getTriggerState(trigger.getKey()).name());
                } catch (Exception e) {
                    log.error("", e);
                }
                return false;
            }

            @Override
            public void triggerMisfired(Trigger trigger) {
                try {
                    log.info("triggerMisfired事件，jobName=" + trigger.getJobKey().getName() + "，triggerName=" + trigger.getKey().getName() + ",状态="
                            + scheduler.getTriggerState(trigger.getKey()).name());
                } catch (Exception e) {
                    log.error("", e);
                }

            }

            @Override
            public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                        CompletedExecutionInstruction triggerInstructionCode) {
                try {
                    log.info("triggerComplete事件，jobName=" + trigger.getJobKey().getName() + "，triggerName=" + trigger.getKey().getName() + ",状态="
                            + scheduler.getTriggerState(trigger.getKey()).name());
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }, EverythingMatcher.allTriggers());
        // 开启定时任务
        scheduler.start();
        return scheduler;
    }

}