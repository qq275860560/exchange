package com.ghf.exchange.boss.authorization.rolepermission.dto;

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
public class ListPermissionByUsernameReqDTO {

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，必须在用户表存在")
    private String username;

    @ApiModelProperty("权限类型,1：平台,2：系统，3：子系统，4：模块，5：子模块，6:一级菜单，7：二级菜单，8：三级菜单,9:页面(组件)，10：子页面（子组件），11:按钮")
    private int permissionType;

}
