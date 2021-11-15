package com.ghf.exchange.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseIdsDTO {

    @ApiModelProperty(value = "id集合", required = true)
    @NotNull(message = "id集合不能为空")
    @NotEmpty(message = "id集合不能为空")
    private List<Long> ids;
}
