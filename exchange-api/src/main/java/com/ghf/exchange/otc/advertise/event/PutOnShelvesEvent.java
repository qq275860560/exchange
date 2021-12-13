package com.ghf.exchange.otc.advertise.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghf.exchange.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
@Setter
@Slf4j
public class PutOnShelvesEvent extends ApplicationEvent {

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

    public PutOnShelvesEvent() {
        super(IdUtil.generateLongId());
    }
}
