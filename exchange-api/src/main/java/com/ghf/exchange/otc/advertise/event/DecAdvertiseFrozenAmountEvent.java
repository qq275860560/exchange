package com.ghf.exchange.otc.advertise.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghf.exchange.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
@Setter
@Slf4j
public class DecAdvertiseFrozenAmountEvent extends ApplicationEvent {
    @ApiModelProperty("广告库存数量日志编码， 唯一")

    private String advertiseAmountLogCode;

    @ApiModelProperty("广告编码")

    private String advertiseCode;

    @ApiModelProperty("操作类型，1:冻结广告库存数量,2:解冻广告库存数量:3:扣减广告冻结库存数量")

    private int advertiseAmountLogType;

    @ApiModelProperty("变动库存数量")

    private BigDecimal amount;

    @ApiModelProperty("变动之前总库存数量")

    private BigDecimal beforeAdvertiseTotalAmount;

    @ApiModelProperty("变动之前可用库存数量")

    private BigDecimal beforeAdvertiseAvailableAmount;

    @ApiModelProperty("变动之前冻结库存数量")

    private BigDecimal beforeAdvertiseFrozenAmount;

    @ApiModelProperty("变动之后总库存数量")

    private BigDecimal afterAdvertiseTotalAmount;

    @ApiModelProperty("变动之后可用库存数量")

    private BigDecimal afterAdvertiseAvailableAmount;

    @ApiModelProperty("变动之后冻结库存数量")

    private BigDecimal afterAdvertiseFrozenAmount;

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

    public DecAdvertiseFrozenAmountEvent() {
        super(IdUtil.generateLongId());
    }
}
