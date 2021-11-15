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
public class UpdateOrgByOrgnameReqDTO {

    @ApiModelProperty("组织名称,组织英文名称，组织编码，唯一")
    private String orgname;

    @ApiModelProperty("组织描述,组织中文名称，页面显示名称")
    private String orgdesc;

    @ApiModelProperty("组织类型,组织级别，默认为0，其中1:一级组织，2：二级组织,3:三级组织，4:四级组织，5:五级组织")
    private int orgType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    private int orderNum;

    @ApiModelProperty("父级组织名称,父级组织英文名称，父级组织编码")
    private String parentOrgname;

    @ApiModelProperty("角色名称列表，角色英文名称列表，角色编码列表")
    private Set<String> rolenameSet = Collections.emptySet();

    @ApiModelProperty("地区名称,地区英文名称，地区编码")
    private String areaname;

}
