package com.ghf.exchange.otc.orderappeal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;

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
public class AuditOrderAppealForAdminReqDTO {
    @ApiModelProperty("申诉编码，唯一")

    private String orderAppealCode;

    @ApiModelProperty("审核结果,3:审核失败,4:审核成功")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("审核结果说明")
    private String orderAppealAuditResult;

}
