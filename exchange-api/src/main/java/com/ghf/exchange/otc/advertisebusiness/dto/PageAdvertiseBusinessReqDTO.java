package com.ghf.exchange.otc.advertisebusiness.dto;

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
public class PageAdvertiseBusinessReqDTO extends PageReqDTO {
    @ApiModelProperty("广告商家编码，默认和用户名相同")

    private String advertiseBusinessCode;
    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")

    private String username;

}
