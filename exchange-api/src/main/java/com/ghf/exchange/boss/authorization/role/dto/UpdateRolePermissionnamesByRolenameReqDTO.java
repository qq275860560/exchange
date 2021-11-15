package com.ghf.exchange.boss.authorization.role.dto;

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
public class UpdateRolePermissionnamesByRolenameReqDTO {

    @ApiModelProperty("角色名称，角色英文名称，角色编码，唯一,ROLE_开头")
    private String rolename;

    @ApiModelProperty("权限名称列表，冗余,逗号隔开，前后都有逗号")
    private String permissionnames;

    @ApiModelProperty("权限描述列表，冗余,逗号隔开，前后都有逗号")
    private String permissiondescs;

}
