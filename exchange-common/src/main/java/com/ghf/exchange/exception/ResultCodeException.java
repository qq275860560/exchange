package com.ghf.exchange.exception;

import com.ghf.exchange.enums.ResultCodeEnum;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiangyuanlin@163.com
 */
public class ResultCodeException extends RuntimeException {

    @ApiModelProperty("返回码{200:请求正常,400:参数有误,401:认证失败,403:授权失败}")
    private int code;
    @ApiModelProperty("提示信息")
    private String msg;

    private Object data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResultCodeException(Integer code, String message) {
        super(code + "," + message);
        this.code = code;
        this.msg = message;
    }

    public ResultCodeException(ResultCodeEnum sysEnum) {
        this(sysEnum.getCode(), sysEnum.getMsg());

    }

}
