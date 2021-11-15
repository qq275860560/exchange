package com.ghf.exchange.boss.common.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class TaskRespDTO {
    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("任务名称，任务英文名称,任务编号，唯一")
    private String taskname;

    @ApiModelProperty("任务描述，任务中文名称")
    private String taskdesc;

    @ApiModelProperty("任务类型,0:简单任务（直接设置重复执行次数和执行间隔，也就是task_repeat_count和task_interval字段有效）,1:cron任务（根据cron表达式确定执行间隔,也就是cron_expression字段有效）")
    private int tasktype;

    @ApiModelProperty("重复次数，默认为0,表示任务只执行一次就完成，即使当前时间还未到end_at，任务也完成了")
    private int taskRepeatCount;

    @ApiModelProperty("每次执行间距(单位秒)，必须大于0，默认为60，表示每隔60秒执行一次，该参数在repeat_count>0时并且在还在有效期是有用")
    private int taskInterval;

    @ApiModelProperty("时间表达式,null代表该任务在start_at时执行一次")
    private String cronExpression;

    @ApiModelProperty("httpUrl链接，局域网内连接，内部服务连接，通常是微服务链接")
    private String requestUrl;

    @ApiModelProperty("请求方法(GET/POST/PUT/DELETE)，默认为POST")
    private String requestMethod;

    @ApiModelProperty("请求头部json格式")
    private String requestHeader;

    @ApiModelProperty("请求参数json,请求体内容")
    private String inputJson;

    @ApiModelProperty("任务第一次/下一次启动时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startAt;

    @ApiModelProperty("任务最后一次截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endAt;

    @ApiModelProperty("状态,0:暂停中,1:运行中,2:已完成")
    private int status;
}
