package com.ghf.exchange.boss.common.tasklog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
public class TaskLogRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("任务名称，任务英文名称,任务编号,必须在task表存在")
    private String taskname;

    @ApiModelProperty("任务日志名称,唯一,建议$taskName-yyyyMMddHHmmssSSS")
    private String tasklogname;

    @ApiModelProperty("任务调用类型,0:远程调用，需要设置http请求链接，请求方法，请求头部，请求体json字符串,1:本地调用,需要设置spring容器中的类全名，方法名称，方法输入参数DTO类型全名(只支持一个DTO)，方法输入参数DTO对应的json字符串")
    private int taskInvokeType;

    @ApiModelProperty("httpUrl链接，局域网内连接，内部服务连接，通常是微服务链接")
    private String requestUrl;

    @ApiModelProperty("请求方法(GET/POST/PUT/DELETE)，默认为POST")
    private String requestMethod;

    @ApiModelProperty("请求头部json格式")
    private String requestHeader;

    @ApiModelProperty("请求参数json,请求体内容")
    private String inputJson;

    @ApiModelProperty("请求参数json,响应体内容")
    private String outputJson;

    @ApiModelProperty("状态码")
    private int responseStatusCode;

    @ApiModelProperty("响应头部json格式")
    private String responseHeader;

    @ApiModelProperty("类全名")
    private String taskClassName;
    @ApiModelProperty("方法名称")
    private String taskMethodName;
    @ApiModelProperty("方法输入参数DTO类型全名(只支持一个DTO)")
    private String taskParameterClassName;
    @ApiModelProperty("方法输入参数DTO对应的json字符串")
    private String taskParameterJson;
    @ApiModelProperty("方法输出参数DTO对应的json字符串")
    private String taskReturnJson;

    @ApiModelProperty("开始时间（1970年以来的毫秒数）")
    private long startTime;

    @ApiModelProperty("结束时间（1970年以来的毫秒数）")
    private long endTime;

    @ApiModelProperty("执行时长(毫秒)")
    private long duration;

    @ApiModelProperty("状态;0：失败，1：成功")
    private int status;
}
