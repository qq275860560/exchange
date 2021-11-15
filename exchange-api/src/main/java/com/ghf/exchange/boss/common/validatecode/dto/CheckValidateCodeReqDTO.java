package com.ghf.exchange.boss.common.validatecode.dto;

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
public class CheckValidateCodeReqDTO {

    @ApiModelProperty("验证码的键,唯一")
    private String validateCodeKey;

    @ApiModelProperty("验证码的值，对应图片/短信/邮箱/滑块验证码的值")
    private String validateCodeValue;

}
