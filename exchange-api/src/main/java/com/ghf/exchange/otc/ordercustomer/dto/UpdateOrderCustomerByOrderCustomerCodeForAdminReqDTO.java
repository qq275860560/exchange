package com.ghf.exchange.otc.ordercustomer.dto;

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
public class UpdateOrderCustomerByOrderCustomerCodeForAdminReqDTO {

    @ApiModelProperty("订单顾客编码，默认和用户名相同")

    private String orderCustomerCode;

    @ApiModelProperty("登陆用户名,用户英文名称，用户编码，唯一")

    private String username;

    @ApiModelProperty("密码，BCryptPasswordEncoder加密")

    private String password;

    @ApiModelProperty("用户昵称，用户中文名称，可以随时修改")

    private String nickname;

    @ApiModelProperty("用户真实姓名")

    private String realname;

    @ApiModelProperty("手机")

    private String mobile;

    @ApiModelProperty("邮箱")

    private String email;

    @ApiModelProperty("国家编码")

    private String countryCode;

    @ApiModelProperty("国家名称")

    private String countryName;

}
