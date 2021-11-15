package com.ghf.exchange.boss.authorization.org.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: ouyang
 * @Date: 2021/9/9 下午12:10
 * @Description:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class GetOrgByOrgdescReqDTO {
    @ApiModelProperty("组织名称，组织编码，唯一")
    private String orgdesc;

}
