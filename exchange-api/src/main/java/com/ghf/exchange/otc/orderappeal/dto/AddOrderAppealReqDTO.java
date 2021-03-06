package com.ghf.exchange.otc.orderappeal.dto;

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
public class AddOrderAppealReqDTO {
    @ApiModelProperty("申诉编码，唯一")

    private String orderAppealCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("申诉类型,1:对方未付款 2：对方未放行 3:其他")
    private int orderAppealType;

    @ApiModelProperty("申诉内容")

    private String orderAppealContent;

}
