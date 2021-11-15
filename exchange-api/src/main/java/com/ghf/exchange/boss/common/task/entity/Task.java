package com.ghf.exchange.boss.common.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Proxy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_task")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Task {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("任务名称，任务英文名称,任务编号，唯一")
    @Column(name = "taskname")
    private String taskname;

    @ApiModelProperty("任务描述，任务中文名称")
    @Column(name = "taskdesc")
    private String taskdesc;

    @ApiModelProperty("任务类型,0:简单任务（直接设置重复执行次数和执行间隔，也就是task_repeat_count和task_interval字段有效）,1:cron任务（根据cron表达式确定执行间隔,也就是cron_expression字段有效）")
    @Column(name = "tasktype")
    private int tasktype;

    @ApiModelProperty("重复次数，默认为0,表示任务只执行一次就完成，即使当前时间还未到end_at，任务也完成了")
    @Column(name = "task_repeat_count")
    private int taskRepeatCount;

    @ApiModelProperty("每次执行间距(单位秒)，必须大于0，默认为60，表示每隔60秒执行一次，该参数在repeat_count>0时并且在还在有效期是有用")
    @Column(name = "task_interval")
    private int taskInterval;

    @ApiModelProperty("时间表达式,null代表该任务在start_at时执行一次")
    @Column(name = "cron_expression")
    private String cronExpression;

    @ApiModelProperty("任务第一次/下一次启动时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_at")
    private Date startAt;

    @ApiModelProperty("任务最后一次截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "end_at")
    private Date endAt;

    @ApiModelProperty("httpUrl链接，局域网内连接，内部服务连接，通常是微服务链接")
    @Column(name = "request_url")
    private String requestUrl;

    @ApiModelProperty("请求方法(GET/POST/PUT/DELETE)，默认为POST")
    @Column(name = "request_method")
    private String requestMethod;

    @ApiModelProperty("请求头部json格式")
    @Column(name = "request_header")
    private String requestHeader;

    @ApiModelProperty("请求参数json,请求体内容")
    @Column(name = "input_json")
    private String inputJson;

    @ApiModelProperty("状态,0:暂停中,1:运行中,2:已完成")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建人id")
    @Column(name = "create_user_id")
    private long createUserId;

    @ApiModelProperty("创建人名称")
    @Column(name = "create_user_name")
    private String createUserName;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
