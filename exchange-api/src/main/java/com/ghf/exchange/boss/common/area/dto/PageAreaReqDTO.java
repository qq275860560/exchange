package com.ghf.exchange.boss.common.area.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghf.exchange.dto.PageReqDTO;
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
public class PageAreaReqDTO extends PageReqDTO {

    @ApiModelProperty("地区名称,地区英文名称，地区编码，唯一")
    private String areaname;

    @ApiModelProperty("地区描述,地区中文名称，页面显示名称")
    private String areadesc;

    @ApiModelProperty("地区类型,地区级别，默认为0，其中1:一级地区，2：二级地区,3:三级地区，4:四级地区，5:五级地区")
    private int areaType;

}
