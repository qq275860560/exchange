package com.ghf.exchange.boss.authorization.orgrole.service;

import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.orgrole.dto.*;
import com.ghf.exchange.boss.authorization.orgrole.entity.OrgRole;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface OrgRoleService extends BaseService<OrgRole, Long> {
    /**
     * 分页搜索组织角色关联
     *
     * @param pageOrgRoleReqDTO
     * @return
     */
    Result<PageRespDTO<OrgRoleRespDTO>> pageOrgRole(PageOrgRoleReqDTO pageOrgRoleReqDTO);

    /**
     * 根据组织英文名称列出组织角色关联
     *
     * @param listOrgRoleByOrgnameReqDTO
     * @return
     */
    Result<List<OrgRoleRespDTO>> listOrgRoleByOrgname(ListOrgRoleByOrgnameReqDTO listOrgRoleByOrgnameReqDTO);

    /**
     * 根据角色英文名称列出组织角色关联
     *
     * @param listOrgRoleByRolenameReqDTO
     * @return
     */
    Result<List<OrgRoleRespDTO>> listOrgRoleByRolename(ListOrgRoleByRolenameReqDTO listOrgRoleByRolenameReqDTO);

    /**
     * 根据组织英文名称和角色英文名获取组织角色关联详情
     *
     * @param getOrgRoleByOrgnameAndRolenameReqDTO
     * @return
     */
    Result<OrgRoleRespDTO> getOrgRoleByOrgnameAndRolename(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO);

    /**
     * 根据组织英文名称和角色英文名称判断组织角色关联是否存在
     *
     * @param getOrgRoleByOrgnameAndRolenameReqDTO
     * @return
     */
    Result<Boolean> existsOrgRoleByOrgnameAndRolename(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO);

    /**
     * 新建组织角色关联
     *
     * @param addOrgRoleReqDTO
     * @return
     */
    Result<Void> addOrgRole(AddOrgRoleReqDTO addOrgRoleReqDTO);

    /**
     * 启用组织角色关联
     *
     * @param getOrgRoleByOrgnameAndRolenameReqDTO
     * @return
     */
    Result<Void> enableOrgRole(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO);

    /**
     * 禁用组织角色关联
     *
     * @param getOrgRoleByOrgnameAndRolenameReqDTO
     * @return
     */
    Result<Void> disableOrgRole(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO);

    /**
     * 根据组织英文名称列出角色
     *
     * @param listRoleByOrgnameReqDTO
     * @return
     */
    Result<List<RoleRespDTO>> listRoleByOrgname(ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO);

    /**
     * 根据角色英文名称列出组织
     *
     * @param listOrgByRolenameReqDTO
     * @return
     */
    Result<List<OrgRespDTO>> listOrgByRolename(ListOrgByRolenameReqDTO listOrgByRolenameReqDTO);

}