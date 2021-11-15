package com.ghf.exchange.boss.common.area.service;

import com.ghf.exchange.boss.common.area.dto.*;
import com.ghf.exchange.boss.common.area.entity.Area;
import com.ghf.exchange.dto.BaseIdDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface AreaService extends BaseService<Area, Long> {
    /**
     * 分页搜索地区
     *
     * @param pageAreaReqDTO
     * @return
     */
    Result<PageRespDTO<AreaRespDTO>> pageArea(PageAreaReqDTO pageAreaReqDTO);

    /**
     * 根据地区名称树状列出地区及其后代
     *
     * @param treeAreaReqDTO
     * @return
     */
    Result<AreaRespDTO> treeArea(TreeAreaReqDTO treeAreaReqDTO);

    /**
     * 根据地区名称列出地区及其祖先
     *
     * @param listAncestorByAreanameReqDTO
     * @return
     */
    Result<List<AreaRespDTO>> listAncestorByAreaname(ListAncestorByAreanameReqDTO listAncestorByAreanameReqDTO);

    /**
     * 根据地区名称获取地区详情
     *
     * @param getAreaByCodeReqDTO
     * @return
     */
    Result<AreaRespDTO> getAreaByAreaname(GetAreaByAreanameReqDTO getAreaByCodeReqDTO);

    /**
     * 根据地区id获取地区详情
     *
     * @param baseIdDto
     * @return
     */
    Result<AreaRespDTO> getAreaByAreaId(BaseIdDTO baseIdDto);

    /**
     * 根据地区中文名称获取地区详情
     *
     * @param getAreaByAreadescReqDTO
     * @return
     */
    Result<AreaRespDTO> getAreaByAreaDesc(GetAreaByAreadescReqDTO getAreaByAreadescReqDTO);

    /**
     * 根据地区名称判断地区是否存在
     *
     * @param getAreaByAreanameReqDTO
     * @return
     */
    Result<Boolean> existsAreaByAreaname(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO);

    /**
     * 新建地区
     *
     * @param addAreaReqDTO
     * @return
     */
    Result<Void> addArea(AddAreaReqDTO addAreaReqDTO);

    /**
     * 更新地区
     *
     * @param updateAreaByAreanameReqDTO
     * @return
     */
    Result<Void> updateAreaByAreaname(UpdateAreaByAreanameReqDTO updateAreaByAreanameReqDTO);

    /**
     * 启用地区
     *
     * @param getAreaByAreanameReqDTO
     * @return
     */
    Result<Void> enableArea(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO);

    /**
     * 禁用地区
     *
     * @param getAreaByAreanameReqDTO
     * @return
     */
    Result<Void> disableArea(GetAreaByAreanameReqDTO getAreaByAreanameReqDTO);

}