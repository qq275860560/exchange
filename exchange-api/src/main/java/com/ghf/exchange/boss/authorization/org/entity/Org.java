package com.ghf.exchange.boss.authorization.org.entity;

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
@Table(name = "t_org")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Org {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("组织名称,组织英文名称，组织编码，唯一")
    @Column(name = "orgname")
    private String orgname;

    @ApiModelProperty("组织描述,组织中文名称，页面显示名称")
    @Column(name = "orgdesc")
    private String orgdesc;

    @ApiModelProperty("组织类型,组织级别，默认为0，其中1:一级组织，2：二级组织,3:三级组织，4:四级组织，5:五级组织")
    @Column(name = "org_type")
    private int orgType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    @Column(name = "order_num")
    private int orderNum;

    @ApiModelProperty("'父级组织id'")
    @Column(name = "parent_org_id")
    private long parentOrgId;

    @ApiModelProperty("父级组织名称,父级组织英文名称，父级组织编码")
    @Column(name = "parent_orgname")
    private String parentOrgname;

    @ApiModelProperty("父级组织描述,父级组织中文名称，页面显示名称")
    @Column(name = "parent_orgdesc")
    private String parentOrgdesc;

    @ApiModelProperty("完整组织id，逗号隔开,前后都有逗号")
    @Column(name = "full_org_id")
    private String fullOrgId;

    @ApiModelProperty("完整组织名称,完整组织英文名称，完整组织编码，逗号隔开,前后都有逗号")
    @Column(name = "full_orgname")
    private String fullOrgname;

    @ApiModelProperty("完整组织描述,完整组织中文名称，页面显示的完整名称，逗号隔开,前后都有逗号")
    @Column(name = "full_orgdesc")
    private String fullOrgdesc;

    @ApiModelProperty("当前节点深度,冗余，根节点为1，第二层为为2，第三层为3，以此类推")
    @Column(name = "deep")
    private int deep;

    @ApiModelProperty("角色名称列表，逗号隔开，前后都有逗号")
    @Column(name = "rolenames")
    private String rolenames;

    @ApiModelProperty("角色描述列表，逗号隔开，前后都有逗号")
    @Column(name = "roledescs")
    private String roledescs;

    @ApiModelProperty("地区名称,地区英文名称，地区编码")
    @Column(name = "areaname")
    private String areaname;

    @ApiModelProperty("地区描述,地区中文名称，页面显示名称")
    @Column(name = "areadesc")
    private String areadesc;

    @ApiModelProperty("完整地区名称,完整地区英文名称，完整地区编码，逗号隔开,前后都有逗号")
    @Column(name = "full_areaname")
    private String fullAreaname;

    @ApiModelProperty("完整地区描述,完整地区中文名称，页面显示的完整名称，逗号隔开,前后都有逗")
    @Column(name = "full_areadesc")
    private String fullAreadesc;

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
