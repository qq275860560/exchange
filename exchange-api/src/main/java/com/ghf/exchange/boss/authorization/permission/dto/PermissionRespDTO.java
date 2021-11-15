package com.ghf.exchange.boss.authorization.permission.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

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
public class PermissionRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("权限名称，权限英文名称，权限编码，唯一")
    private String permissionname;

    @ApiModelProperty("权限描述，权限中文名称")
    private String permissiondesc;

    @ApiModelProperty("权限类型,1：平台,2：系统，3：子系统，4：模块，5：子模块，6:一级菜单，7：二级菜单，8：三级菜单,9:页面(组件)，10：子页面（子组件），11:按钮")
    private int permissionType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    private int orderNum;

    @ApiModelProperty("图标url")
    private String icon;

    @ApiModelProperty("前端页面相对路径,可能为空，可能多个，多个请用逗号分隔")
    private String pageUrl;

    @ApiModelProperty("前端组件相对路径,比如/component/user/pageUser,可能为空，可能多个，多个请用逗号分隔")
    private String componentUrl;

    @ApiModelProperty("后端ajax相对路径,唯一通常指ip端口后面的路径,比如/api/app/getApp或者/api/user/**,可能为空，可能多个，多个请用逗号分隔")
    private String requestUrl;

    @ApiModelProperty("父级权限id")
    private long parentPermissionId;

    @ApiModelProperty("父级权限名称，父级权限英文名称，父级权限编码")
    private String parentPermissionname;

    @ApiModelProperty("父级权限描述，父级权限中文名称")
    private String parentPermissiondesc;

    @ApiModelProperty("完整权限id，逗号隔开,前后都有逗号")
    private String fullPermissionId;

    @ApiModelProperty("完整权限名称,完整权限英文名称，完整权限编码，逗号隔开,前后都有逗号")
    private String fullPermissionname;

    @ApiModelProperty("完整权限描述,完整权限中文名称，页面显示的完整名称，逗号隔开,前后都有逗号")
    private String fullPermissiondesc;

    @ApiModelProperty("当前节点深度,冗余，根节点为1，第二层为为2，第三层为3，以此类推")
    private int deep;

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;

    @ApiModelProperty("菜单是否显示,0:隐藏,1:显示")
    private int display;

    @ApiModelProperty("孩子列表，树状返回时才有值")
    private List<PermissionRespDTO> children = Collections.emptyList();

}
