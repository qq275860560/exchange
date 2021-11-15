package com.ghf.exchange.otc.advertise.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertise.dto.*;
import com.ghf.exchange.otc.advertise.service.AdvertiseService;
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
@Api(value = "广告接口", tags = {"广告接口"})
@RestController
@Lazy
@Slf4j
public class AdvertiseController {

    @Lazy
    @Resource
    private AdvertiseService advertiseService;

    @ApiOperation(value = "分页搜索广告", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/pageAdvertise")
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseRespDTO>> pageAdvertise(@RequestBody PageAdvertiseReqDTO pageAdvertiseReqDTO) {
        return advertiseService.pageAdvertise(pageAdvertiseReqDTO);
    }

    @ApiOperation(value = "根据广告编号获取广告详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/getAdvertiseByAdvertiseCode")
    @SneakyThrows
    public Result<AdvertiseRespDTO> getAdvertiseByAdvertiseCode(@RequestBody GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO) {
        return advertiseService.getAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO);
    }

    @ApiOperation(value = "根据广告编号判断广告是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/existsAdvertiseByAdvertiseCode")
    @SneakyThrows
    public Result<Boolean> existsAdvertiseByAdvertiseCode(@RequestBody GetAdvertiseByAdvertiseCodeReqDTO getAdvertiseByAdvertiseCodeReqDTO) {
        return advertiseService.existsAdvertiseByAdvertiseCode(getAdvertiseByAdvertiseCodeReqDTO);
    }

    @ApiOperation(value = "获取匹配广告详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/getMatchAdvertise")
    @SneakyThrows
    public Result<AdvertiseRespDTO> getMatchAdvertise(@RequestBody GetMatchAdvertiseReqDTO getMatchAdvertiseReqDTO) {
        return advertiseService.getMatchAdvertise(getMatchAdvertiseReqDTO);
    }

    @ApiOperation(value = "发布广告", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/addAdvertise")
    @SneakyThrows
    public Result<Void> addAdvertise(@RequestBody AddAdvertiseReqDTO addAdvertiseReqDTO) {
        return advertiseService.addAdvertise(addAdvertiseReqDTO);
    }

    @ApiOperation(value = "上架广告", notes = "<p>)</p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/putOnShelves")
    @SneakyThrows
    public Result<Void> putOnShelves(@RequestBody PutOnShelvesReqDTO putOnShelvesReqDTO) {
        return advertiseService.putOnShelves(putOnShelvesReqDTO);
    }

    @ApiOperation(value = "下架广告", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/putOffShelves")
    @SneakyThrows
    public Result<Void> putOffShelves(@RequestBody PutOffShelvesReqDTO putOffShelvesReqDTO) {
        return advertiseService.putOffShelves(putOffShelvesReqDTO);
    }

    @ApiOperation(value = "删除广告", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/deleteAdvertise")
    @SneakyThrows
    public Result<Void> deleteAdvertise(@RequestBody DeleteAdvertiseReqDTO deleteAdvertiseReqDTO) {
        return advertiseService.deleteAdvertise(deleteAdvertiseReqDTO);
    }

    @ApiOperation(value = "冻结广告数量", notes = "<p>冻结广告数量，比如：新建买币订单时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/freezeAdvertiseAmount")
    @SneakyThrows
    public Result<Void> freezeAdvertiseAmount(@RequestBody FreezeAdvertiseAmountReqDTO freezeAdvertiseAmountReqDTO) {
        return advertiseService.freezeAdvertiseAmount(freezeAdvertiseAmountReqDTO);
    }

    @ApiOperation(value = "解冻广告数量", notes = "<p>解冻广告数量，比如：取消买币订单时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/unFreezeAdvertiseAmount")
    @SneakyThrows
    public Result<Void> unFreezeAdvertiseAmount(@RequestBody UnFreezeAdvertiseAmountReqDTO unFreezeAdvertiseAmountReqDTO) {
        return advertiseService.unFreezeAdvertiseAmount(unFreezeAdvertiseAmountReqDTO);
    }

    @ApiOperation(value = "扣减广告冻结数量", notes = "<p>扣减广告冻结数量，比如：放行订单时</p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertise/decAdvertiseFrozenAmount")
    @SneakyThrows
    public Result<Void> decAdvertiseFrozenAmount(@RequestBody DecAdvertiseFrozenAmountReqDTO decAdvertiseFrozenAmountReqDTO) {
        return advertiseService.decAdvertiseFrozenAmount(decAdvertiseFrozenAmountReqDTO);
    }
}
