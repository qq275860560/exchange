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
public class AddTaskLogReqDTO {

    @ApiModelProperty("任务名称，任务英文名称,任务编号,必须在task表存在")
    private String taskname;

    @ApiModelProperty("任务日志名称,唯一,建议$taskName-yyyyMMddHHmmssSSS")
    private String tasklogname;

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

    @ApiModelProperty("开始时间（1970年以来的毫秒数）")
    private long startTime;

    @ApiModelProperty("结束时间（1970年以来的毫秒数）")
    private long endTime;

    @ApiModelProperty("执行时长(毫秒)")
    private long duration;

    @ApiModelProperty("状态;0：失败，1：成功")
    private int status;

}
