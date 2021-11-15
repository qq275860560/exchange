package com.ghf.exchange.boss.authorization.role.entity;

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
@Table(name = "t_role")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Role {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("角色名称，角色英文名称，角色编码，唯一,ROLE_开头")
    @Column(name = "rolename")
    private String rolename;

    @ApiModelProperty("角色描述，角色中文名称,比如平台管理员,普通注册用户")
    @Column(name = "roledesc")
    private String roledesc;

    @ApiModelProperty("权限名称列表，冗余,逗号隔开，前后都有逗号")
    @Column(name = "permissionnames")
    private String permissionnames;

    @ApiModelProperty("权限描述列表，冗余,逗号隔开，前后都有逗号")
    @Column(name = "permissiondescs")
    private String permissiondescs;

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

    @ApiModelProperty("备注")
    @Column(name = "remark")
    private String remark = "";

}
