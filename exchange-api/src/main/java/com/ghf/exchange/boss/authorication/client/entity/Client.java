package com.ghf.exchange.boss.authorication.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Proxy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_client")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Client {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("客户端id，唯一，相当于oauth2认证码模式的client_id")
    @Column(name = "client_id")
    private String clientId;

    @ApiModelProperty("密码，相当于oauth2认证码模式的client_secret，数据库存储以BCryptPasswordEncoder加密,但认证请求时的密码为明文")
    @Column(name = "client_secret")
    private String clientSecret;

    @ApiModelProperty("url,用于接收code或者access_token，多个请逗号分隔")
    @Column(name = "registered_redirect_uris")
    private String registeredRedirectUris;

    @ApiModelProperty("认证模式,多个请逗号分隔,可选项为authorization_code,refresh_token,implicit,password,client_credentials")
    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;

    @Column(name = "scopes")
    private String scopes;

    @ApiModelProperty("token的有效时间,单位秒")
    @Column(name = "access_token_validity_seconds")
    private int accessTokenValiditySeconds;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建人id")
    @Column(name = "create_user_id")
    private long createUserId;

    @ApiModelProperty("创建人名称")
    @Column(name = "create_user_name")
    private String createUserName;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
