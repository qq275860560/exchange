package com.ghf.exchange.otc.orderappeal.dto;

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
public class PageOrderAppealReqDTO extends PageReqDTO {

    @ApiModelProperty("申诉编码，唯一")
    private String orderAppealCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("广告编码")

    private String advertiseCode;

    @ApiModelProperty("状态,1:上架,2:下架,3:删除")
    private int status;

}
