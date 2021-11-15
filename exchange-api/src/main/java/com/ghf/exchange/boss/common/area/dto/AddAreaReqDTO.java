package com.ghf.exchange.boss.common.area.dto;

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
public class AddAreaReqDTO {

    @ApiModelProperty("地区名称,地区英文名称，地区编码，唯一")
    private String areaname;

    @ApiModelProperty("地区描述,地区中文名称，页面显示名称")
    private String areadesc;

    @ApiModelProperty("地区类型,地区级别，默认为0，其中1:一级地区，2：二级地区,3:三级地区，4:四级地区，5:五级地区")
    private int areaType;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一父亲时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    private int orderNum;

    @ApiModelProperty("父级地区名称,父级地区英文名称，父级地区编码")
    private String parentAreaname;

}
