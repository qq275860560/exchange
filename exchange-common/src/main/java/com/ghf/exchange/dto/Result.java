package com.ghf.exchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghf.exchange.enums.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel(description = "返回结果")
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public class Result<T> implements Serializable {
    @ApiModelProperty("返回码{200:请求正常,400:参数有误,401:认证失败,403:授权失败}")
    private int code;
    @ApiModelProperty("提示信息")
    private String msg;
    @ApiModelProperty("数据部分")
    private T data;

    public Result() {
        this(ResultCodeEnum.OK.getCode(), ResultCodeEnum.OK.getMsg(), null);
    }

    public Result(T data) {
        this(ResultCodeEnum.OK.getCode(), ResultCodeEnum.OK.getMsg(), data);
    }

    public Result(ResultCodeEnum resultCodeEnum) {
        this(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), null);
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}