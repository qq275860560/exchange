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
public class EmailValidateCodeRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("验证码的键,唯一")
    private String validateCodeKey;

    @ApiModelProperty("验证码有效时间，单位秒")
    private int seconds;

    @ApiModelProperty("用途,1:注册,2:登录,3:忘记密码,4:提交表单")
    private int validateCodeUsage;

    @ApiModelProperty("验证码状态{0:未验证,1:验证通过，2:验证不通过}")
    private int status;

    @ApiModelProperty("验证不通过原因,0:无，1:不准确，2:超时")
    private int cause;

}
