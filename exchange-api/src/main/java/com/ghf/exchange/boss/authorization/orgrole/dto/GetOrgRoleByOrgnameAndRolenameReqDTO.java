package com.ghf.exchange.boss.authorization.orgrole.dto;

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
public class GetOrgRoleByOrgnameAndRolenameReqDTO {

    @ApiModelProperty("组织名称,组织英文名称，组织编码，必须在组织表存在")
    private String orgname;

    @ApiModelProperty("角色名称，角色英文名称，角色编码，必须在角色表存在")
    private String rolename;

}
