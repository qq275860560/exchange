package com.ghf.exchange.otc.country.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
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
public class CountryRespDTO {

    @ApiModelProperty("id")
    private long id;
    @ApiModelProperty("国家编码")

    private String countryCode;

    @ApiModelProperty("国家名称")

    private String countryName;

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;

    @ApiModelProperty("操作时间")
    @Column(name = "create_time")
    private Date createTime;
}
