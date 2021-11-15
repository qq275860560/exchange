package com.ghf.exchange.boss.common.validatecode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;

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
public class GenerateEmailValidateCodeReqDTO {

    @ApiModelProperty("用途,1:注册,2:登录,3:忘记密码,4:提交表单")
    private int validateCodeUsage;

    @ApiModelProperty("邮箱号码，验证方式为邮箱时必填")
    @Email(message = "邮箱格式不正确")
    private String email;

}
