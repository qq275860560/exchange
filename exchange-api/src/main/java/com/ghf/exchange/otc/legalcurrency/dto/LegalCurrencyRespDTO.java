package com.ghf.exchange.otc.legalcurrency.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.util.Date;

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
public class LegalCurrencyRespDTO {

    @ApiModelProperty("id")
    @Id

    private long id;

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

    @ApiModelProperty("状态,0:禁用,1:启用")

    private int status;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;
}
