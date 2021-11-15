package com.ghf.exchange.boss.authorication.user.dto;

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
public class UpdateUserByUsernameReqDTO {

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")
    private String username;

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")
    private String nickname;

    @ApiModelProperty("用户真实姓名")
    private String realname;

    @ApiModelProperty("手机，唯一")
    private String mobile;

    @ApiModelProperty("邮箱，唯一")
    private String email;

    @ApiModelProperty("角色名称列表，角色英文名称列表，角色编码列表")
    private Set<String> rolenameSet = Collections.emptySet();

    @ApiModelProperty("组织名称列表,组织英文名称列表，组织编码列表")
    private Set<String> orgnameSet = Collections.emptySet();

}
