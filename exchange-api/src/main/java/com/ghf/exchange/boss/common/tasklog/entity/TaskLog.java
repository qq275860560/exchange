package com.ghf.exchange.boss.common.tasklog.entity;

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
@Table(name = "t_task_log")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class TaskLog {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("任务名称，任务英文名称,任务编号,必须在task表存在")
    @Column(name = "taskname")
    private String taskname;

    @ApiModelProperty("任务日志名称,唯一,建议$taskName-yyyyMMddHHmmssSSS")
    @Column(name = "tasklogname")
    private String tasklogname;

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

    @ApiModelProperty("请求参数json,响应体内容")
    @Column(name = "output_json")
    private String outputJson;

    @ApiModelProperty("状态码")
    @Column(name = "response_status_code")
    private int responseStatusCode;

    @ApiModelProperty("响应头部json格式")
    @Column(name = "response_header")
    private String responseHeader;

    @ApiModelProperty("开始时间（1970年以来的毫秒数）")
    @Column(name = "start_time")
    private long startTime;

    @ApiModelProperty("结束时间（1970年以来的毫秒数）")
    @Column(name = "end_time")
    private long endTime;

    @ApiModelProperty("执行时长(毫秒)")
    @Column(name = "duration")
    private long duration;

    @ApiModelProperty("状态;0：失败，1：成功")
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
