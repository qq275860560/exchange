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
public class ListTaskLogReqDTO {

    @ApiModelProperty("任务名称，任务英文名称,任务编号,必须在task表存在")
    private String taskname;

    @ApiModelProperty("任务日志名称,唯一,建议$taskName-yyyyMMddHHmmssSSS")
    private String tasklogname;

}
