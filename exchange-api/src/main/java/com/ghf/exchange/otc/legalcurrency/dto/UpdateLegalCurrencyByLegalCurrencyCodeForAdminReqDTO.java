package com.ghf.exchange.otc.legalcurrency.dto;

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
public class UpdateLegalCurrencyByLegalCurrencyCodeForAdminReqDTO {
    @ApiModelProperty("法币编码")

    private String legalCurrencyCode;

    @ApiModelProperty("法币名称")

    private String legalCurrencyName;

    @ApiModelProperty("法币符号")

    private String legalCurrencySymbol;

    @ApiModelProperty("法币单位")

    private String legalCurrencyUnit;

    @ApiModelProperty("法币所在国家编码")

    private String legalCurrencyCountryCode;

    @ApiModelProperty("法币所在国家名称")

    private String legalCurrencyCountryName;

    @ApiModelProperty("汇率，兑美元汇率")
    private String legalCurrencyExchangeRate;

}
