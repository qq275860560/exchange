package com.ghf.exchange.boss.authorication.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghf.exchange.dto.PageReqDTO;
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
public class PageUserReqDTO extends PageReqDTO {

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")
    private String username = "";

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")
    private String nickname = "";

    @ApiModelProperty("组织名称,组织英文名称，组织编码，必须在组织表存在")
    private String orgname = "";

    @ApiModelProperty("组织描述,组织中文名称，页面显示名称")
    private String orgdesc = "";

    @ApiModelProperty("角色名称，角色英文名称，角色编码，必须在角色表存在")
    private String rolename = "";
    @ApiModelProperty("角色描述，角色中文名称,比如平台管理员,普通注册用户")
    private String roledesc = "";

}
