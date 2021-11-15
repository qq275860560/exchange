package com.ghf.exchange.boss.authorication.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collections;
import java.util.Date;
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
public class UserRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")
    private String username;

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")
    private String nickname;

    @ApiModelProperty("用户真实姓名")
    private String realname;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("用户状态是否启用,0:禁用,1:启用")
    private int status;

    @ApiModelProperty("角色名称列表，角色英文名称列表，角色编码列表")
    private Set<String> rolenameSet = Collections.emptySet();

    @ApiModelProperty("角色名称列表，冗余,逗号隔开，前后都有逗号")
    private String rolenames;

    @ApiModelProperty("角色描述列表，角色中文名称列表")
    private Set<String> roledescSet = Collections.emptySet();

    @ApiModelProperty("角色描述列表，冗余,逗号隔开，前后都有逗号")
    private String roledescs;

    @ApiModelProperty("组织名称列表,组织英文名称列表，组织编码列表")
    private Set<String> orgnameSet = Collections.emptySet();

    @ApiModelProperty("组织名称列表，冗余,逗号隔开，前后都有逗号")
    private String orgnames;

    @ApiModelProperty("组织描述列表，组织中文名称列表")
    private Set<String> orgdescSet = Collections.emptySet();

    @ApiModelProperty("组织描述列表，冗余,逗号隔开，前后都有逗号")
    private String orgdescs;

    @ApiModelProperty("最后登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    @ApiModelProperty("最后登录IP")
    private String lastLoginIp;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
