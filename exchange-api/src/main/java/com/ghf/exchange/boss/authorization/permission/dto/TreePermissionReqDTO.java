package com.ghf.exchange.boss.authorization.permission.dto;

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
public class TreePermissionReqDTO {

    @ApiModelProperty("权限名称，权限英文名称，权限编码，唯一")
    private String permissionname;

    @ApiModelProperty("以指定节点permissionname作为根节点时，树的高度最大返回treeDeep，或者叶子的深度最多返回treeDeep,1代表只有自己，2代表只有自己和儿子们，3代表只有自己，儿子们和孙子们.0代表有多少返回多少后代")
    private int treeDeep = 0;

}
