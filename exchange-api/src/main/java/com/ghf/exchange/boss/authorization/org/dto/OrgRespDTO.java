package com.ghf.exchange.boss.authorization.org.dto;

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
public class OrgRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("组织名称,组织英文名称，组织编码，唯一")
    private String orgname;

    @ApiModelProperty("组织描述,组织中文名称，页面显示名称")
    private String orgdesc;

    @ApiModelProperty("组织类型,组织级别，默认为0，其中1:一级组织，2：二级组织,3:三级组织，4:四级组织，5:五级组织")
    private int orgType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    private int orderNum;

    @ApiModelProperty("'父级组织id'")
    private long parentOrgId;

    @ApiModelProperty("父级组织名称,父级组织英文名称，父级组织编码")
    private String parentOrgname;

    @ApiModelProperty("父级组织描述,父级组织中文名称，页面显示名称")
    private String parentOrgdesc;

    @ApiModelProperty("完整组织id，逗号隔开,前后都有逗号")
    private String fullOrgId;

    @ApiModelProperty("完整组织名称,完整组织英文名称，完整组织编码，逗号隔开,前后都有逗号")
    private String fullOrgname;

    @ApiModelProperty("完整组织名称列表,完整组织英文名称列表，完整组织编码列表")
    private List<String> fullOrgnameList = Collections.emptyList();

    @ApiModelProperty("完整组织描述,完整组织中文名称，页面显示的完整名称，逗号隔开,前后都有逗号")
    private String fullOrgdesc;

    @ApiModelProperty("完整组织描述列表，完整组织中文名称列表，页面显示的完整名称列表")
    private List<String> fullOrgdescList = Collections.emptyList();

    @ApiModelProperty("当前节点深度,冗余，根节点为1，第二层为为2，第三层为3，以此类推")
    private int deep;

    @ApiModelProperty("地区名称,地区英文名称，地区编码")
    private String areaname;

    @ApiModelProperty("地区描述,地区中文名称，页面显示名称")
    private String areadesc;

    @ApiModelProperty("完整地区名称,完整地区英文名称，完整地区编码，逗号隔开,前后都有逗号")
    private String fullAreaname;

    @ApiModelProperty("完整地区名称列表，完整地区英文名称列表，完整地区编码列表")
    private List<String> fullAreanameList = Collections.emptyList();

    @ApiModelProperty("完整地区描述,完整地区中文名称，页面显示的完整名称，逗号隔开,前后都有逗号")
    private String fullAreadesc;

    @ApiModelProperty("完整地区描述列表，完整地区中文名称列表，页面显示的完整名称列表")
    private List<String> fullAreadescList = Collections.emptyList();

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;

    @ApiModelProperty("角色名称列表，角色英文名称列表，角色编码列表")
    private Set<String> rolenameSet = Collections.emptySet();

    @ApiModelProperty("角色名称列表，冗余,逗号隔开，前后都有逗号")
    private String rolenames;

    @ApiModelProperty("角色描述列表，角色中文名称列表")
    private Set<String> roledescSet = Collections.emptySet();

    @ApiModelProperty("角色描述列表，冗余,逗号隔开，前后都有逗号")
    private String roledescs;

    @ApiModelProperty("孩子列表，树状返回时才有值")
    private List<OrgRespDTO> children = Collections.emptyList();

}
