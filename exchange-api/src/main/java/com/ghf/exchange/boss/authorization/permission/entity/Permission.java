package com.ghf.exchange.boss.authorization.permission.entity;

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
@Table(name = "t_permission")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Permission {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("权限名称，权限英文名称，权限编码，唯一")
    @Column(name = "permissionname")
    private String permissionname;

    @ApiModelProperty("权限描述，权限中文名称")
    @Column(name = "permissiondesc")
    private String permissiondesc;

    @ApiModelProperty("权限类型,1：平台,2：系统，3：子系统，4：模块，5：子模块，6:一级菜单，7：二级菜单，8：三级菜单,9:页面(组件)，10：子页面（子组件），11:按钮")
    @Column(name = "permission_type")
    private int permissionType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    @Column(name = "order_num")
    private int orderNum;

    @ApiModelProperty("图标url")
    @Column(name = "icon")
    private String icon;

    @ApiModelProperty("前端页面相对路径,可能为空，可能多个，多个请用逗号分隔")
    @Column(name = "page_url")
    private String pageUrl;

    @ApiModelProperty("前端组件相对路径,比如/component/user/pageUser,可能为空，可能多个，多个请用逗号分隔")
    @Column(name = "component_url")
    private String componentUrl;

    @ApiModelProperty("后端ajax相对路径,唯一通常指ip端口后面的路径,比如/api/app/getApp或者/api/user/**,可能为空，可能多个，多个请用逗号分隔")
    @Column(name = "request_url")
    private String requestUrl;

    @ApiModelProperty("父级权限id")
    @Column(name = "parent_permission_id")
    private long parentPermissionId;

    @ApiModelProperty("父级权限名称，父级权限英文名称，父级权限编码")
    @Column(name = "parent_permissionname")
    private String parentPermissionname;

    @ApiModelProperty("父级权限描述，父级权限中文名称")
    @Column(name = "parent_permissiondesc")
    private String parentPermissiondesc;

    @ApiModelProperty("完整权限id，逗号隔开,前后都有逗号")
    @Column(name = "full_permission_id")
    private String fullPermissionId;

    @ApiModelProperty("完整权限名称,完整权限英文名称，完整权限编码，逗号隔开,前后都有逗号")
    @Column(name = "full_permissionname")
    private String fullPermissionname;

    @ApiModelProperty("完整权限描述,完整权限中文名称，页面显示的完整名称，逗号隔开,前后都有逗号")
    @Column(name = "full_permissiondesc")
    private String fullPermissiondesc;

    @ApiModelProperty("当前节点深度,冗余，根节点为1，第二层为为2，第三层为3，以此类推")
    @Column(name = "deep")
    private int deep;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("菜单是否显示,0:隐藏,1:显示")
    @Column(name = "display")
    private int display;

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
