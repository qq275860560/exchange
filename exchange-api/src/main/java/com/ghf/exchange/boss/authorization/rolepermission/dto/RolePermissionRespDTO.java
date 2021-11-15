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
public class RolePermissionRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("角色名称，角色英文名称，角色编码，必须在角色表存在")
    private String rolename;

    @ApiModelProperty("权限名称，权限英文名称，权限编码，必须在权限表存在")
    private String permissionname;

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;
}
