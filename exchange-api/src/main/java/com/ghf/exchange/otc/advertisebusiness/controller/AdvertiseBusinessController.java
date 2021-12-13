package com.ghf.exchange.otc.advertisebusiness.controller;

import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.otc.advertisebusiness.dto.*;
import com.ghf.exchange.otc.advertisebusiness.service.AdvertiseBusinessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "广告商家接口", tags = {"广告商家接口"})
@RestController
@Lazy
@Slf4j
public class AdvertiseBusinessController {

    @Lazy
    @Resource
    private AdvertiseBusinessService advertiseBusinessService;

    @ApiOperation(value = "分页搜索广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/pageAdvertiseBusiness")
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseBusinessRespDTO>> pageAdvertiseBusiness(@RequestBody PageAdvertiseBusinessReqDTO pageAdvertiseBusinessReqDTO) {
        return advertiseBusinessService.pageAdvertiseBusiness(pageAdvertiseBusinessReqDTO);
    }

    @ApiOperation(value = "管理员分页搜索广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/pageAdvertiseBusinessForAdmin")
    @SneakyThrows
    public Result<PageRespDTO<AdvertiseBusinessRespDTO>> pageAdvertiseBusinessForAdmin(@RequestBody PageAdvertiseBusinessForAdminReqDTO pageAdvertiseBusinessForAdminReqDTO) {
        return advertiseBusinessService.pageAdvertiseBusinessForAdmin(pageAdvertiseBusinessForAdminReqDTO);
    }

    @ApiOperation(value = "列出广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/listAdvertiseBusiness")
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusiness(@RequestBody ListAdvertiseBusinessReqDTO listAdvertiseBusinessReqDTO) {
        return advertiseBusinessService.listAdvertiseBusiness(listAdvertiseBusinessReqDTO);
    }

    @ApiOperation(value = "管理员列出广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/listAdvertiseBusinessForAdmin")
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusinessForAdmin(@RequestBody ListAdvertiseBusinessForAdminReqDTO listAdvertiseBusinessForAdminReqDTO) {
        return advertiseBusinessService.listAdvertiseBusinessForAdmin(listAdvertiseBusinessForAdminReqDTO);
    }

    @ApiOperation(value = "微服务客户端列出广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/listAdvertiseBusinessForClient")
    @SneakyThrows
    public Result<List<AdvertiseBusinessRespDTO>> listAdvertiseBusinessForClient(@RequestBody ListAdvertiseBusinessForClientReqDTO listAdvertiseBusinessForClientReqDTO) {
        return advertiseBusinessService.listAdvertiseBusinessForClient(listAdvertiseBusinessForClientReqDTO);
    }

    @ApiOperation(value = "根据广告商家编号获取广告商家详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/getAdvertiseBusinessByAdvertiseBusinessCode")
    @SneakyThrows
    public Result<AdvertiseBusinessRespDTO> getAdvertiseBusinessByAdvertiseBusinessCode(@RequestBody GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        return advertiseBusinessService.getAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @ApiOperation(value = "根据广告商家编号判断广告商家是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/existsAdvertiseBusinessByAdvertiseBusinessCode")
    @SneakyThrows
    public Result<Boolean> existsAdvertiseBusinessByAdvertiseBusinessCode(@RequestBody GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        return advertiseBusinessService.existsAdvertiseBusinessByAdvertiseBusinessCode(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @ApiOperation(value = "微服务客户端新建广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/addAdvertiseBusinessForClient")
    @SneakyThrows
    public Result<Void> addAdvertiseBusinessForClient(@RequestBody AddAdvertiseBusinessForClientReqDTO addAdvertiseBusinessForClientReqDTO) {
        return advertiseBusinessService.addAdvertiseBusinessForClient(addAdvertiseBusinessForClientReqDTO);
    }

    @ApiOperation(value = "管理员更新广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/updateAdvertiseBusinessByAdvertiseBusinessForAdminCode")
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessByAdvertiseBusinessForAdminCode(@RequestBody UpdateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO) {
        return advertiseBusinessService.updateAdvertiseBusinessByAdvertiseBusinessForAdminCode(updateAdvertiseBusinessByAdvertiseBusinessCodeForAdminReqDTO);
    }

    @ApiOperation(value = "管理员启用广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/enableAdvertiseBusinessForAdmin")
    @SneakyThrows
    public Result<Void> enableAdvertiseBusinessForAdmin(@RequestBody GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        return advertiseBusinessService.enableAdvertiseBusinessForAdmin(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @ApiOperation(value = "管理员停用广告商家", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/disableAdvertiseBusinessForAdmin")
    @SneakyThrows
    public Result<Void> disableAdvertiseBusinessForAdmin(@RequestBody GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        return advertiseBusinessService.disableAdvertiseBusinessForAdmin(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @ApiOperation(value = "客户端更新广告商家（上架广告时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/updateAdvertiseBusinessOnPutOnShelvesForClient")
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnPutOnShelvesForClient(@RequestBody GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        return advertiseBusinessService.updateAdvertiseBusinessOnPutOnShelvesForClient(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @ApiOperation(value = "客户端更新广告商家（下架广告时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/updateAdvertiseBusinessOnPutOffShelvesForClient")
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnPutOffShelvesForClient(@RequestBody GetAdvertiseBusinessByAdvertiseBusinessCodeReqDTO getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO) {
        return advertiseBusinessService.updateAdvertiseBusinessOnPutOffShelvesForClient(getAdvertiseBusinessByAdvertiseBusinessCodeReqDTO);
    }

    @ApiOperation(value = "客户端更新广告商家（下单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/updateAdvertiseBusinessOnAddOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnAddOrderEventForClient(@RequestBody UpdateAdvertiseBusinessOnAddOrderEventForClientReqDTO updateAdvertiseBusinessOnAddOrderEventForClientReqDTO) {
        return advertiseBusinessService.updateAdvertiseBusinessOnAddOrderEventForClient(updateAdvertiseBusinessOnAddOrderEventForClientReqDTO);
    }

    @ApiOperation(value = "客户端更新广告商家（放行订单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/updateAdvertiseBusinessOnReleaseOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnReleaseOrderEventForClient(@RequestBody UpdateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO) {
        return advertiseBusinessService.updateAdvertiseBusinessOnReleaseOrderEventForClient(updateAdvertiseBusinessOnReleaseOrderEventForClientReqDTO);
    }

    @ApiOperation(value = "客户端更新广告商家（申诉订单时）", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/advertiseBusiness/updateAdvertiseBusinessOnAppealOrderEventForClient")
    @SneakyThrows
    public Result<Void> updateAdvertiseBusinessOnAppealOrderEventForClient(@RequestBody UpdateAdvertiseBusinessOnAppealOrderEventForClientReqDTO updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO) {
        return advertiseBusinessService.updateAdvertiseBusinessOnAppealOrderEventForClient(updateAdvertiseBusinessOnAppealOrderEventForClientReqDTO);
    }

}
