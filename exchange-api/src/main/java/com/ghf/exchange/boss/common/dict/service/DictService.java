package com.ghf.exchange.boss.common.dict.service;

import com.ghf.exchange.boss.common.dict.dto.*;
import com.ghf.exchange.boss.common.dict.entity.Dict;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface DictService extends BaseService<Dict, Long> {
    /**
     * 分页搜索数据字典
     *
     * @param pageDictReqDTO
     * @return
     */
    Result<PageRespDTO<DictRespDTO>> pageDict(PageDictReqDTO pageDictReqDTO);

    /**
     * 根据数据字典类型列出数据字典
     *
     * @param listDictByDicttypeReqDTO
     * @return
     */
    Result<List<DictRespDTO>> listDictByDicttype(ListDictByDicttypeReqDTO listDictByDicttypeReqDTO);

    /**
     * 根据数据字典类型数组批量列出数据字典
     *
     * @param batchListDictByDicttypesReqDTO
     * @return
     */
    Result<List<BatchListDictByDicttypesRespDTO>> batchListDictByDicttypes(BatchListDictByDicttypesReqDTO batchListDictByDicttypesReqDTO);

    /**
     * 根据数据字典类型和数据字典键获取数据字典详情
     *
     * @param getDictByDicttypeAndDictkeyReqDTO
     * @return
     */
    Result<DictRespDTO> getDictByDicttypeAndDictkey(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO);

    /**
     * 根据数据字典类型和数据字典值获取数据字典详情
     *
     * @param getDictByDicttypeAndDictvalueReqDTO
     * @return
     */
    Result<DictRespDTO> getDictByDicttypeAndDictvalue(GetDictByDicttypeAndDictvalueReqDTO getDictByDicttypeAndDictvalueReqDTO);

    /**
     * 根据数据字典类型和数据字典键判断数据字典是否存在
     *
     * @param getDictByDicttypeAndDictkeyReqDTO
     * @return
     */
    Result<Boolean> existsDictByDicttypeAndDictkey(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO);

    /**
     * 新建数据字典
     *
     * @param addDictReqDTO
     * @return
     */
    Result<Void> addDict(AddDictReqDTO addDictReqDTO);

    /**
     * 更新数据字典
     *
     * @param updateDictByDicttypeAndDictkeyReqDTO
     * @return
     */
    Result<Void> updateDictByDicttypeAndDictkey(UpdateDictByDicttypeAndDictkeyReqDTO updateDictByDicttypeAndDictkeyReqDTO);

    /**
     * 启用数据字典
     *
     * @param getDictByDicttypeAndDictkeyReqDTO
     * @return
     */
    Result<Void> enableDict(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO);

    /**
     * 禁用数据字典
     *
     * @param getDictByDicttypeAndDictkeyReqDTO
     * @return
     */
    Result<Void> disableDict(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO);

}