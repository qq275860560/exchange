package com.ghf.exchange.boss.authorization.org.service;

import com.ghf.exchange.boss.authorization.org.dto.*;
import com.ghf.exchange.boss.authorization.org.entity.Org;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrgService extends BaseService<Org, Long> {
    /**
     * 分页搜索组织
     *
     * @param pageOrgReqDTO
     * @return
     */
    Result<PageRespDTO<OrgRespDTO>> pageOrg(PageOrgReqDTO pageOrgReqDTO);

    /**
     * 根据组织名称树状列出组织及其后代
     *
     * @param treeOrgReqDTO
     * @return
     */
    Result<OrgRespDTO> treeOrg(TreeOrgReqDTO treeOrgReqDTO);

    /**
     * 根据组织名称列出组织及其祖先
     *
     * @param listAncestorByOrgnameReqDTO
     * @return
     */
    Result<List<OrgRespDTO>> listAncestorByOrgname(ListAncestorByOrgnameReqDTO listAncestorByOrgnameReqDTO);

    /**
     * 根据组织名称获取组织详情
     *
     * @param getOrgByCodeReqDTO
     * @return
     */
    Result<OrgRespDTO> getOrgByOrgname(GetOrgByOrgnameReqDTO getOrgByCodeReqDTO);

    /**
     * 根据组织中文名称获取组织详情
     *
     * @param getOrgByOrgdescReqDTO
     * @return
     */
    Result<OrgRespDTO> getOrgByOrgdesc(GetOrgByOrgdescReqDTO getOrgByOrgdescReqDTO);

    /**
     * 根据组织名称判断组织是否存在
     *
     * @param getOrgByOrgnameReqDTO
     * @return
     */
    Result<Boolean> existsOrgByOrgname(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO);

    /**
     * 新建组织
     *
     * @param addOrgReqDTO
     * @return
     */
    Result<Void> addOrg(AddOrgReqDTO addOrgReqDTO);

    /**
     * 更新组织
     *
     * @param updateOrgByOrgnameReqDTO
     * @return
     */
    Result<Void> updateOrgByOrgname(UpdateOrgByOrgnameReqDTO updateOrgByOrgnameReqDTO);

    /**
     * 根据组织名更新角色列表
     *
     * @param updateOrgRolenamesByOrgnameReqDTO
     * @return
     */
    Result<Void> updateOrgRolenamesByOrgname(UpdateOrgRolenamesByOrgnameReqDTO updateOrgRolenamesByOrgnameReqDTO);

    /**
     * 启用组织
     *
     * @param getOrgByOrgnameReqDTO
     * @return
     */
    Result<Void> enableOrg(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO);

    /**
     * 禁用组织
     *
     * @param getOrgByOrgnameReqDTO
     * @return
     */
    Result<Void> disableOrg(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO);

}