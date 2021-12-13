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
public class PageOrderAppealForAdminReqDTO extends PageReqDTO {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("申诉编码，唯一")
    private String orderAppealCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("广告编码")

    private String advertiseCode;

    @ApiModelProperty("申诉状态,1:已申诉,2:已审核,3:已取消")
    private int status;

}
