package com.ghf.exchange.otc.advertiselog.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertiselog.dto.AddAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.dto.AdvertiseLogRespDTO;
import com.ghf.exchange.otc.advertiselog.dto.GetAdvertiseLogByAdvertiseLogCodeReqDTO;
import com.ghf.exchange.otc.advertiselog.dto.PageAdvertiseLogReqDTO;
import com.ghf.exchange.otc.advertiselog.service.AdvertiseLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "广告日志接口", tags = {"广告日志接口"})
@RestController
@Lazy
@Slf4j
public class AdvertiseLogController {

    @Lazy
    @Resource
    private AdvertiseLogService advertiseLogService;

    @ApiOperation(value = "分页搜索广告日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiselog/pageAdvertiseLog")
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseLogRespDTO>> pageAdvertiseLog(@RequestBody PageAdvertiseLogReqDTO pageAdvertiseLogReqDTO) {
        return advertiseLogService.pageAdvertiseLog(pageAdvertiseLogReqDTO);
    }

    @ApiOperation(value = "根据广告日志编码获取广告日志详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiselog/getAdvertiseLogByAdvertiseLogCode")
    @SneakyThrows
    public Result<AdvertiseLogRespDTO> getAdvertiseLogByAdvertiseLogCode(@RequestBody GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO) {
        return advertiseLogService.getAdvertiseLogByAdvertiseLogCode(getAdvertiseLogByAdvertiseLogCodeReqDTO);
    }

    @ApiOperation(value = "根据广告日志编码判断广告日志是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiselog/existsAdvertiseLogByAdvertiseLogCode")
    @SneakyThrows
    public Result<Boolean> existsAdvertiseLogByAdvertiseLogCode(@RequestBody GetAdvertiseLogByAdvertiseLogCodeReqDTO getAdvertiseLogByAdvertiseLogCodeReqDTO) {
        return advertiseLogService.existsAdvertiseLogByAdvertiseLogCode(getAdvertiseLogByAdvertiseLogCodeReqDTO);
    }

    @ApiOperation(value = "新建广告日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiselog/addAdvertiseLog")
    @SneakyThrows
    public Result<Void> addAdvertiseLog(@RequestBody AddAdvertiseLogReqDTO addAdvertiseLogReqDTO) {
        return advertiseLogService.addAdvertiseLog(addAdvertiseLogReqDTO);
    }

}
