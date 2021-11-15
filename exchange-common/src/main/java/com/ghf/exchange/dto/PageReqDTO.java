package com.ghf.exchange.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageReqDTO implements Serializable {

    @ApiModelProperty(value = "当前页，默认为1", example = "1")
    private int pageNum = 1;

    @ApiModelProperty(value = "页大小，默认为10", example = "10")
    private int pageSize = 10;

    @ApiModelProperty(value = "排序数组,每个数组元素都包含排序字段名称和排序方向")
    private List<Sort> sort;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sort {
        @ApiModelProperty(value = "排序方向，desc:降序,asc:升序", example = "desc")
        private String direction;
        @ApiModelProperty(value = "排序字段名称，此字段名称不是数据库表字段的名称，而是实体类po的字段名称，比如createTime", example = "createTime")
        private String property;
    }

}
