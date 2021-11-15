package com.ghf.exchange.boss.authorication.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Proxy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_user")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class User {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")
    @Column(name = "username")
    private String username;

    @ApiModelProperty("密码，BCryptPasswordEncoder加密")
    @Column(name = "password")
    private String password;

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")
    @Column(name = "nickname")
    private String nickname;

    @ApiModelProperty("用户真实姓名")
    @Column(name = "realname")
    private String realname;

    @ApiModelProperty("手机")
    @Column(name = "mobile")
    private String mobile;

    @ApiModelProperty("邮箱")
    @Column(name = "email")
    private String email;

    @ApiModelProperty("组织名称列表，冗余,逗号隔开，前后都有逗号")
    @Column(name = "orgnames")
    private String orgnames;

    @ApiModelProperty("组织描述列表，冗余,逗号隔开，前后都有逗号")
    @Column(name = "orgdescs")
    private String orgdescs;

    @ApiModelProperty("角色名称列表，冗余,逗号隔开，前后都有逗号")
    @Column(name = "rolenames")
    private String rolenames;

    @ApiModelProperty("角色描述列表，冗余,逗号隔开，前后都有逗号")
    @Column(name = "roledescs")
    private String roledescs;

    @ApiModelProperty("最后登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @ApiModelProperty("最后登录IP")
    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建人id")
    @Column(name = "create_user_id")
    private long createUserId;

    @ApiModelProperty("创建人名称")
    @Column(name = "create_user_name")
    private String createUserName;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
