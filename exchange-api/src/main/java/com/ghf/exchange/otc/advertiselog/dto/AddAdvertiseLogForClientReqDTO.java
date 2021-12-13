package com.ghf.exchange.otc.advertiselog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

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
public class AddAdvertiseLogForClientReqDTO {

    @ApiModelProperty("广告日志编码， 唯一")

    private String advertiseLogCode;

    @ApiModelProperty("广告编码")

    private String advertiseCode;

    @ApiModelProperty("操作类型，1:发布,2:上架:3:下架,4:删除")

    private int advertiseLogType;

    @ApiModelProperty("登录客户端")

    private String advertiseLogClientId;

    @ApiModelProperty("操作人，如果是后端服务器进行操作，此字段为空")

    private String advertiseLogUsername;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    @ApiModelProperty("IP地址")

    private String advertiseLogIpAddr;

    @ApiModelProperty("备注")

    private String remark;
}
