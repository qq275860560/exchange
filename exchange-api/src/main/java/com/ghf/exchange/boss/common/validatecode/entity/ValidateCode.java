package com.ghf.exchange.boss.common.validatecode.entity;

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
@Table(name = "t_validate_code")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class ValidateCode {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("验证码的键,唯一")
    @Column(name = "validate_code_key")
    private String validateCodeKey;

    @ApiModelProperty("验证码的值，对应图片/短信/邮箱验证码的值")
    @Column(name = "validate_code_value")
    private String validateCodeValue;

    @ApiModelProperty("验证方式{1:图片,2:短信,3:邮箱,4:滑块}")
    @Column(name = "validate_code_type")
    private int validateCodeType;

    @ApiModelProperty("用途,1:注册,2:登录,3:忘记密码,4:提交表单")
    @Column(name = "validate_code_usage")
    private int validateCodeUsage;

    @ApiModelProperty("有效时间，单位秒")
    @Column(name = "seconds")
    private int seconds;

    @ApiModelProperty("可重新验证多少次")
    @Column(name = "retry_count")
    private int retryCount;

    @ApiModelProperty("已经验证多少次")
    @Column(name = "execute_count")
    private int executeCount;

    @ApiModelProperty("验证码状态{0:未验证,1:已验证通过，2:已验证失败}")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("验证不通过原因,0:无，1:不准确，2:超时")
    @Column(name = "cause")
    private int cause;

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
