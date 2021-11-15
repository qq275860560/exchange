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
 * <p>
 * </p>
 *
 * @description TODO
 * @author: zhongshengliang
 * @create: 2021/9/10 1:58 下午
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class GetChildAreaByAreaCodeReqDTO {

    @ApiModelProperty("地区名称,地区英文名称，地区编码，唯一")
    private String areaCode;
}
