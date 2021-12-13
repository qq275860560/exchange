package com.ghf.exchange.otc.advertise.dto;

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
public class PageAdvertiseReqDTO extends PageReqDTO {

    @ApiModelProperty("广告编码，唯一")
    private String advertiseCode = "";

    @ApiModelProperty("买卖类型,1:买币,2:卖币")
    private int advertiseBuySellType;

    @ApiModelProperty("币种编码")
    private String advertiseCoinCode = "";

    @ApiModelProperty("法币符号")
    private String advertiseLegalCurrencySymbol = "";

    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")
    private String advertiseBusinessUsername = "";

    @ApiModelProperty("状态,1:上架,2:下架,3:删除")
    private int status;

}
