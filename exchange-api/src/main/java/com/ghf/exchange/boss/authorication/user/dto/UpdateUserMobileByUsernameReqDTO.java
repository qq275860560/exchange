package com.ghf.exchange.boss.authorication.user.dto;

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
public class UpdateUserMobileByUsernameReqDTO {

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")
    private String username;

    @ApiModelProperty("手机")
    private String mobile;

}
