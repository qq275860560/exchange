package com.ghf.exchange.boss.authorization.role.service;

import com.ghf.exchange.boss.authorization.role.dto.*;
import com.ghf.exchange.boss.authorization.role.entity.Role;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface RoleService extends BaseService<Role, Long> {
    /**
     * 分页搜索角色
     *
     * @param pageRoleReqDTO
     * @return
     */
    Result<PageRespDTO<RoleRespDTO>> pageRole(PageRoleReqDTO pageRoleReqDTO);

    /**
     * 列出角色
     *
     * @param listRoleReqDTO
     * @return
     */
    Result<List<RoleRespDTO>> listRole(ListRoleReqDTO listRoleReqDTO);

    /**
     * 根据角色名称获取角色详情
     *
     * @param getRoleByRolenameReqDTO
     * @return
     */
    Result<RoleRespDTO> getRoleByRolename(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO);

    /**
     * 根据角色名称判断角色是否存在
     *
     * @param getRoleByRolenameReqDTO
     * @return
     */
    Result<Boolean> existsRoleByRolename(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO);

    /**
     * 新建角色
     *
     * @param addRoleReqDTO
     * @return
     */
    Result<Void> addRole(AddRoleReqDTO addRoleReqDTO);

    /**
     * 更新角色
     *
     * @param updateRoleReqDTO
     * @return
     */
    Result<Void> updateRoleByRolename(UpdateRoleByRolenameReqDTO updateRoleReqDTO);

    /**
     * 根据角色名更新权限列表
     *
     * @param updateRolePermissionnamesByRolenameReqDTO
     * @return
     */
    Result<Void> updateRolePermissionnamesByRolename(UpdateRolePermissionnamesByRolenameReqDTO updateRolePermissionnamesByRolenameReqDTO);

    /**
     * 启用角色
     *
     * @param getRoleByRolenameReqDTO
     * @return
     */
    Result<Void> enableRole(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO);

    /**
     * 禁用角色
     *
     * @param getRoleByRolenameReqDTO
     * @return
     */
    Result<Void> disableRole(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO);

}