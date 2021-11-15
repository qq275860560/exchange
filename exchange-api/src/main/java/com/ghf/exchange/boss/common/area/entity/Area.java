package com.ghf.exchange.boss.common.area.entity;

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
@Table(name = "t_area")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Area {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("地区名称,地区英文名称，地区编码，唯一")
    @Column(name = "areaname")
    private String areaname;

    @ApiModelProperty("地区描述,地区中文名称，页面显示名称")
    @Column(name = "areadesc")
    private String areadesc;

    @ApiModelProperty("地区类型,地区级别，默认为0，其中1:一级地区，2：二级地区,3:三级地区，4:四级地区，5:五级地区")
    @Column(name = "area_type")
    private int areaType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    @Column(name = "order_num")
    private int orderNum;

    @ApiModelProperty("'父级地区id'")
    @Column(name = "parent_area_id")
    private long parentAreaId;

    @ApiModelProperty("父级地区名称,父级地区英文名称，父级地区编码")
    @Column(name = "parent_areaname")
    private String parentAreaname;

    @ApiModelProperty("父级地区描述,父级地区中文名称，页面显示名称")
    @Column(name = "parent_areadesc")
    private String parentAreadesc;

    @ApiModelProperty("完整地区id，逗号隔开,前后都有逗号")
    @Column(name = "full_area_id")
    private String fullAreaId;

    @ApiModelProperty("完整地区名称,完整地区英文名称，完整地区编码，逗号隔开,前后都有逗号")
    @Column(name = "full_areaname")
    private String fullAreaname;

    @ApiModelProperty("完整地区描述,完整地区中文名称，页面显示的完整名称，逗号隔开,前后都有逗号")
    @Column(name = "full_areadesc")
    private String fullAreadesc;

    @ApiModelProperty("当前节点深度,冗余，根节点为1，第二层为为2，第三层为3，以此类推")
    @Column(name = "deep")
    private int deep;

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
