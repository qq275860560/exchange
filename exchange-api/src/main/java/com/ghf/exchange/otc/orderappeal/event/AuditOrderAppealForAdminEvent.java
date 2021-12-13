package com.ghf.exchange.otc.orderappeal.event;

import com.ghf.exchange.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author jiangyuanlin@163.com
 */
@Getter
@Setter
@Slf4j
public class AuditOrderAppealForAdminEvent extends ApplicationEvent {
    @ApiModelProperty("申诉类型,1:对方未付款 2：对方未放行 3:其他")
    private int orderAppealType;
    @ApiModelProperty("订单编码")
    private String orderCode;
    @ApiModelProperty("审核结果,3:审核失败,4:审核成功")
    private int status;

    @ApiModelProperty("订单申诉前状态1:已下单,2:已付款,3:已放行,4:申诉中,5:已取消")
    private int orderOldStatus;

    public AuditOrderAppealForAdminEvent() {
        super(IdUtil.generateLongId());
    }

}
