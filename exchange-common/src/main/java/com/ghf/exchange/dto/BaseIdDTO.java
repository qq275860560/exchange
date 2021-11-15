package com.ghf.exchange.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author jiangyuanlin@163.com
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseIdDTO {

    @ApiModelProperty(value = "对象id", required = true)
    @NotNull(message = "id不能为空")
    private long id;
}
