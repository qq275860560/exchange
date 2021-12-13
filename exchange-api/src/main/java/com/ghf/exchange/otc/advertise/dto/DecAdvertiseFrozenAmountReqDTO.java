package com.ghf.exchange.otc.advertise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

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
public class DecAdvertiseFrozenAmountReqDTO {

    @ApiModelProperty("广告编码，唯一")
    private String advertiseCode;

    @ApiModelProperty("库存数量")
    private BigDecimal advertiseAmount;

    @ApiModelProperty("备注")
    private String remark;

}
