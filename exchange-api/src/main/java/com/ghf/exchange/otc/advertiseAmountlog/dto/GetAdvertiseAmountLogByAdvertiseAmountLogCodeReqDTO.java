package com.ghf.exchange.otc.advertiseamountlog.dto;

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
public class GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO {

    @ApiModelProperty("广告库存数量日志编码， 唯一")

    private String advertiseAmountLogCode;

}
