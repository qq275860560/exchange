package com.ghf.exchange.boss.authorization.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;

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
public class AddRoleReqDTO {

    @ApiModelProperty("角色名称，角色英文名称，角色编码，唯一,ROLE_开头")
    private String rolename;

    @ApiModelProperty("角色描述，角色中文名称,比如平台管理员,普通注册用户")
    private String roledesc;

    @ApiModelProperty("权限名称列表，权限英文名称列表，权限编码列表")
    private Set<String> permissionnameSet = Collections.emptySet();

    @ApiModelProperty("备注")
    private String remark;

}
