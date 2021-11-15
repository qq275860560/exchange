package com.ghf.exchange.boss.common.dict.controller;

import com.ghf.exchange.boss.common.dict.dto.*;
import com.ghf.exchange.boss.common.dict.service.DictService;
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
@Api(value = "数据字典接口", tags = {"数据字典接口"})
@RestController
@Lazy
@Slf4j
public class DictController {
    @Lazy
    @Resource
    private DictService dictService;

    @ApiOperation(value = "分页搜索数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/pageDict")
    @SneakyThrows
    public Result<PageRespDTO<DictRespDTO>> pageDict(@RequestBody PageDictReqDTO pageDictReqDTO) {
        return dictService.pageDict(pageDictReqDTO);
    }

    @ApiOperation(value = "根据数据字典类型列出数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/listDictByDicttype")
    @SneakyThrows
    public Result<List<DictRespDTO>> listDictByDicttype(@RequestBody ListDictByDicttypeReqDTO listDictByDicttypeReqDTO) {
        return dictService.listDictByDicttype(listDictByDicttypeReqDTO);
    }

    @ApiOperation(value = "根据数据字典类型数组批量列出数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/batchListDictByDicttypes")
    @SneakyThrows
    public Result<List<BatchListDictByDicttypesRespDTO>> batchListDictByDicttypes(@RequestBody BatchListDictByDicttypesReqDTO batchListDictByDicttypesReqDTO) {
        return dictService.batchListDictByDicttypes(batchListDictByDicttypesReqDTO);
    }

    @ApiOperation(value = "根据数据字典类型和数据字典键获取数据字典详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/getDictByDicttypeAndDictkey")
    @SneakyThrows
    public Result<DictRespDTO> getDictByDicttypeAndDictkey(@RequestBody GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        return dictService.getDictByDicttypeAndDictkey(getDictByDicttypeAndDictkeyReqDTO);
    }

    @ApiOperation(value = "根据数据字典类型和数据字典键判断数据字典是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/existsDictByDicttypeAndDictkey")
    @SneakyThrows
    public Result<Boolean> existsDictByDicttypeAndDictkey(@RequestBody GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        return dictService.existsDictByDicttypeAndDictkey(getDictByDicttypeAndDictkeyReqDTO);
    }

    @ApiOperation(value = "新建数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/addDict")
    @SneakyThrows
    public Result<Void> addDict(@RequestBody AddDictReqDTO addDictReqDTO) {
        return dictService.addDict(addDictReqDTO);
    }

    @ApiOperation(value = "更新数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/updateDictByDicttypeAndDictkey")
    @SneakyThrows
    public Result<Void> updateDictByDicttypeAndDictkey(@RequestBody UpdateDictByDicttypeAndDictkeyReqDTO updateDictByDicttypeAndDictkeyReqDTO) {
        return dictService.updateDictByDicttypeAndDictkey(updateDictByDicttypeAndDictkeyReqDTO);
    }

    @ApiOperation(value = "启用数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/enableDict")
    @SneakyThrows
    public Result<Void> enableDict(@RequestBody GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        return dictService.enableDict(getDictByDicttypeAndDictkeyReqDTO);
    }

    @ApiOperation(value = "禁用数据字典", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/dict/disableDict")
    @SneakyThrows
    public Result<Void> disableDict(@RequestBody GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        return dictService.disableDict(getDictByDicttypeAndDictkeyReqDTO);
    }

}
