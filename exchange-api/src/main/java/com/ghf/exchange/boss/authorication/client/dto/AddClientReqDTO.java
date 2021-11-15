package com.ghf.exchange.boss.authorication.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;

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
public class AddClientReqDTO {

    @ApiModelProperty("客户端id，唯一，相当于oauth2认证码模式的client_id")
    private String clientId;

    @ApiModelProperty("密码，相当于oauth2认证码模式的client_secret，数据库存储以BCryptPasswordEncoder加密,但认证请求时的密码为明文")
    private String clientSecret;

    @ApiModelProperty("url,用于接收code或者access_token，多个请逗号分隔")
    private String registeredRedirectUris;

    @ApiModelProperty("认证模式,多个请逗号分隔,可选项为authorization_code,refresh_token,implicit,password,client_credentials")
    private String authorizedGrantTypes;

    @ApiModelProperty("范围")
    @Column(name = "scopes")
    private String scopes;

    @ApiModelProperty("token的有效时间,单位秒")
    private int accessTokenValiditySeconds;

}
