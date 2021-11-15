package com.ghf.exchange.otc.account.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

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
public class AccountRespDTO {

    @ApiModelProperty("id")
    private long id;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("币种编码")
    private String coinCode;

    @ApiModelProperty("可用金额")
    private BigDecimal balance;

    @ApiModelProperty("冻结金额")
    private BigDecimal frozenBalance;

    @ApiModelProperty("操作时间")
    @Column(name = "create_time")
    private Date createTime;
}