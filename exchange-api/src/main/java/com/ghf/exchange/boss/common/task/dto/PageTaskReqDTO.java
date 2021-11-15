package com.ghf.exchange.boss.common.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghf.exchange.dto.PageReqDTO;
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
public class PageTaskReqDTO extends PageReqDTO {

    @ApiModelProperty("任务名称，任务英文名称,任务编号，唯一")
    private String taskname;

    @ApiModelProperty("任务描述，任务中文名称")
    private String taskdesc;

}
