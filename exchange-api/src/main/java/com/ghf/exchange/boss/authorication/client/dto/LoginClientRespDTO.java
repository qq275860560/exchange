package com.ghf.exchange.boss.authorication.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;

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
public class LoginClientRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("客户端id，唯一，相当于oauth2认证码模式的client_id")
    private String clientId;

    @ApiModelProperty("url,用于接收code或者access_token，多个请逗号分隔")
    private String registeredRedirectUris;

    @ApiModelProperty("认证模式,多个请逗号分隔,可选项为authorization_code,refresh_token,implicit,password,client_credentials")
    private String authorizedGrantTypes;

    @ApiModelProperty("范围集合")
    private Set<String> scopeSet = Collections.emptySet();

    @ApiModelProperty("范围")
    private String scopes;

    @ApiModelProperty("token的有效时间,单位秒")
    private int accessTokenValiditySeconds;

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;

    @ApiModelProperty("凭证类型，请在调用接口时放到http头部字段Authorization，格式如下 -H 'Authorization:$tokenType $accessToken'")
    private String tokenType;
    @ApiModelProperty("凭证，请在调用接口时放到http头部字段Authorization，格式如下 -H 'Authorization:bearer $token'")
    private String accessToken;

}
