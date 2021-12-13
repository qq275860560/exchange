package com.ghf.exchange.otc.advertiseamountlog.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertiseamountlog.dto.AddAdvertiseAmountLogForClientReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.AdvertiseAmountLogRespDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.dto.PageAdvertiseAmountLogForAdminReqDTO;
import com.ghf.exchange.otc.advertiseamountlog.service.AdvertiseAmountLogService;
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
@Api(value = "广告库存数量日志接口", tags = {"广告库存数量日志接口"})
@RestController
@Lazy
@Slf4j
public class AdvertiseAmountLogController {

    @Lazy
    @Resource
    private AdvertiseAmountLogService advertiseLogService;

    @ApiOperation(value = "管理员分页搜索广告库存数量日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseamountlog/pageAdvertiseAmountLogForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseAmountLogRespDTO>> pageAdvertiseAmountLogForAdmin(@RequestBody PageAdvertiseAmountLogForAdminReqDTO pageAdvertiseAmountLogReqDTO) {
        return advertiseLogService.pageAdvertiseAmountLogForAdmin(pageAdvertiseAmountLogReqDTO);
    }

    @ApiOperation(value = "根据广告库存数量日志编码获取广告库存数量日志详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseamountlog/getAdvertiseAmountLogByAdvertiseAmountLogCode")
    @SneakyThrows
    public Result<AdvertiseAmountLogRespDTO> getAdvertiseAmountLogByAdvertiseAmountLogCode(@RequestBody GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO) {
        return advertiseLogService.getAdvertiseAmountLogByAdvertiseAmountLogCode(getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO);
    }

    @ApiOperation(value = "根据广告库存数量日志编码判断广告库存数量日志是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseamountlog/existsAdvertiseAmountLogByAdvertiseAmountLogCode")
    @SneakyThrows
    public Result<Boolean> existsAdvertiseAmountLogByAdvertiseAmountLogCode(@RequestBody GetAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO) {
        return advertiseLogService.existsAdvertiseAmountLogByAdvertiseAmountLogCode(getAdvertiseAmountLogByAdvertiseAmountLogCodeReqDTO);
    }

    @ApiOperation(value = "微服务客户端新建广告库存数量日志", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseamountlog/addAdvertiseAmountLogForClient")
    @SneakyThrows
    public Result<Void> addAdvertiseAmountLogForClient(@RequestBody AddAdvertiseAmountLogForClientReqDTO addAdvertiseAmountLogForClientReqDTO) {
        return advertiseLogService.addAdvertiseAmountLogForClient(addAdvertiseAmountLogForClientReqDTO);
    }

}
