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
public class GetRoleByRolenameReqDTO {

    @ApiModelProperty("角色名称，角色英文名称，角色编码，唯一,ROLE_开头")
    private String rolename;

    @ApiModelProperty("备注")
    private String remark;
}
