package com.ghf.exchange.boss.common.area.controller;

import com.ghf.exchange.boss.common.area.dto.*;
import com.ghf.exchange.boss.common.area.service.AreaService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
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
@Api(value = "地区接口", tags = {"地区接口"})
@RestController
@Lazy
@Slf4j
public class AreaController {

    @Lazy
    @Resource
    private AreaService areaService;

    @ApiOperation(value = "分页搜索地区", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/pageArea")
    @SneakyThrows
    public Result<PageRespDTO<AreaRespDTO>> pageArea(@RequestBody PageAreaReqDTO pageAreaReqDTO) {
        return areaService.pageArea(pageAreaReqDTO);
    }

    @ApiOperation(value = "根据地区名称树状列出地区及其后代", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/treeArea")
    @SneakyThrows
    public Result<AreaRespDTO> treeArea(@RequestBody TreeAreaReqDTO treeAreaReqDTO) {
        return areaService.treeArea(treeAreaReqDTO);
    }

    @ApiOperation(value = "根据地区名称列出地区及其祖先", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/ListAncestorByAreaname")
    @SneakyThrows
    public Result<List<AreaRespDTO>> listAncestorByAreaname(@RequestBody ListAncestorByAreanameReqDTO listAncestorByAreanameReqDTO) {
        return areaService.listAncestorByAreaname(listAncestorByAreanameReqDTO);
    }

    @ApiOperation(value = "根据地区名称获取地区详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/getAreaByAreaname")
    @SneakyThrows
    public Result<AreaRespDTO> getAreaByAreaname(@RequestBody GetAreaByAreanameReqDTO getAreaByCodeReqDTO) {
        return areaService.getAreaByAreaname(getAreaByCodeReqDTO);
    }

    @ApiOperation(value = "根据地区名称判断地区是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/existsAreaByAreaname")
    @SneakyThrows
    public Result<Boolean> existsAreaByAreaname(@RequestBody GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        return areaService.existsAreaByAreaname(getAreaByAreanameReqDTO);
    }

    @ApiOperation(value = "新建地区", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/addArea")
    @SneakyThrows
    public Result<Void> addArea(@RequestBody AddAreaReqDTO addAreaReqDTO) {
        return areaService.addArea(addAreaReqDTO);
    }

    @ApiOperation(value = "更新地区", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/updateAreaByAreaname")
    @SneakyThrows
    public Result<Void> updateAreaByAreaname(@RequestBody UpdateAreaByAreanameReqDTO updateAreaByAreanameReqDTO) {
        return areaService.updateAreaByAreaname(updateAreaByAreanameReqDTO);
    }

    @ApiOperation(value = "启用地区", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/enableArea")
    @SneakyThrows
    public Result<Void> enableArea(@RequestBody GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        return areaService.enableArea(getAreaByAreanameReqDTO);
    }

    @ApiOperation(value = "禁用地区", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/area/disableArea")
    @SneakyThrows
    public Result<Void> disableArea(@RequestBody GetAreaByAreanameReqDTO getAreaByAreanameReqDTO) {
        return areaService.disableArea(getAreaByAreanameReqDTO);
    }

}
