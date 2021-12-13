package com.ghf.exchange.otc.order.event;

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
public class AddOrderEvent extends ApplicationEvent {

    @ApiModelProperty("订单日志编码， 唯一")

    private String orderLogCode;

    @ApiModelProperty("订单编码")

    private String orderCode;

    @ApiModelProperty("操作类型，1:下单,2:付款:3:放行,4:取消,5:恢复,6:确认未付款,7:确认未放行")

    private int orderLogType;

    @ApiModelProperty("登录客户端")

    private String orderLogClientId;

    @ApiModelProperty("操作人，如果是后端服务器进行操作，此字段为空")

    private String orderLogUsername;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("IP地址")

    private String orderLogIpAddr;

    @ApiModelProperty("备注")

    private String remark;

    @ApiModelProperty("广告编码，唯一")
    private String advertiseCode;

    @ApiModelProperty("任务名称，任务英文名称,任务编号，唯一")
    private String taskname;
    @ApiModelProperty("任务描述，任务中文名称")
    private String taskdesc;
    @ApiModelProperty("任务类型,0:简单任务（直接设置重复执行次数和执行间隔，也就是task_repeat_count和task_interval字段有效）,1:cron任务（根据cron表达式确定执行间隔,也就是cron_expression字段有效）")
    private int tasktype;
    @ApiModelProperty("任务调用类型,0:远程调用，需要设置http请求链接，请求方法，请求头部，请求体json字符串,1:本地调用,需要设置spring容器中的类全名，方法名称，方法输入参数DTO类型全名(只支持一个DTO)，方法输入参数DTO对应的json字符串")
    private int taskInvokeType;
    @ApiModelProperty("重复次数，默认为0,表示任务只执行一次就完成，即使当前时间还未到end_at，任务也完成了")
    private int taskRepeatCount;
    @ApiModelProperty("每次执行间距(单位秒)，必须大于0，默认为60，表示每隔60秒执行一次，该参数在repeat_count>0时并且在还在有效期是有用")
    private int taskInterval;
    @ApiModelProperty("类全名")
    private String taskClassName;
    @ApiModelProperty("方法名称")
    private String taskMethodName;
    @ApiModelProperty("方法输入参数DTO类型全名(只支持一个DTO)")
    private String taskParameterClassName;
    @ApiModelProperty("方法输入参数DTO对应的json字符串")
    private String taskParameterJson;
    @ApiModelProperty("任务第一次/下一次启动时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startAt;
    @ApiModelProperty("任务最后一次截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endAt;

    @ApiModelProperty("订单买卖类型,1:买币,2:卖币，跟广告买卖类型相反")
    private int orderBuySellType;
    @ApiModelProperty("订单法币成交总价,后台计算")
    private BigDecimal orderTotalPrice;
    @ApiModelProperty("订单顾客的登陆用户名,用户英文名称，用户编码")
    private String orderCustomerUsername;
    @ApiModelProperty("广告商家的登陆用户名,用户英文名称，用户编码")
    private String advertiseBusinessUsername;
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;
    @ApiModelProperty("付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;
    @ApiModelProperty("放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;
    @ApiModelProperty("申诉时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appealTime;
    @ApiModelProperty("取消时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;
    @ApiModelProperty("确认未付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date agreeUnPayTime;
    @ApiModelProperty("确认未放行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date agreeUnReleaseTime;

    public AddOrderEvent() {
        super(IdUtil.generateLongId());
    }
}
