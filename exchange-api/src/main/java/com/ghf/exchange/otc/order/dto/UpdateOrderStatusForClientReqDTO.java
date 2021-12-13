package com.ghf.exchange.otc.order.dto;

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
public class UpdateOrderStatusForClientReqDTO {

    @ApiModelProperty("订单编码，唯一")
    private String orderCode;

    @ApiModelProperty("订单状态,1:已下单,2:已付款,3:已放行,4:申诉中,5:已取消")

    private int status;

}
