package com.ghf.exchange.boss.authorization.userrole.service;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.userrole.dto.*;
import com.ghf.exchange.boss.authorization.userrole.entity.UserRole;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface UserRoleService extends BaseService<UserRole, Long> {
    /**
     * 分页搜索用户角色关联
     *
     * @param pageUserRoleReqDTO
     * @return
     */
    Result<PageRespDTO<UserRoleRespDTO>> pageUserRole(PageUserRoleReqDTO pageUserRoleReqDTO);

    /**
     * 根据用户英文名称列出用户角色关联
     *
     * @param listUserRoleByUsernameReqDTO
     * @return
     */
    Result<List<UserRoleRespDTO>> listUserRoleByUsername(ListUserRoleByUsernameReqDTO listUserRoleByUsernameReqDTO);

    /**
     * 根据角色英文名称列出用户角色关联
     *
     * @param listUserRoleByRolenameReqDTO
     * @return
     */
    Result<List<UserRoleRespDTO>> listUserRoleByRolename(ListUserRoleByRolenameReqDTO listUserRoleByRolenameReqDTO);

    /**
     * 根据用户英文名称和角色英文名称获取用户角色关联详情
     *
     * @param getUserRoleByUsernameAndRolenameReqDTO
     * @return
     */
    Result<UserRoleRespDTO> getUserRoleByUsernameAndRolename(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO);

    /**
     * 根据用户英文名称和角色英文名称判断用户角色关联是否存在
     *
     * @param getUserRoleByUsernameAndRolenameReqDTO
     * @return
     */
    Result<Boolean> existsUserRoleByUsernameAndRolename(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO);

    /**
     * 新建用户角色关联
     *
     * @param addUserRoleReqDTO
     * @return
     */
    Result<Void> addUserRole(AddUserRoleReqDTO addUserRoleReqDTO);

    /**
     * 启用用户角色关联
     *
     * @param getUserRoleByUsernameAndRolenameReqDTO
     * @return
     */
    Result<Void> enableUserRole(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO);

    /**
     * 禁用用户角色关联
     *
     * @param getUserRoleByUsernameAndRolenameReqDTO
     * @return
     */
    Result<Void> disableUserRole(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO);

    /**
     * 根据用户英文名称列出角色
     *
     * @param listRoleByUsernameReqDTO
     * @return
     */
    Result<List<RoleRespDTO>> listRoleByUsername(ListRoleByUsernameReqDTO listRoleByUsernameReqDTO);

    /**
     * 根据角色英文名称列出用户
     *
     * @param listUserByRolenameReqDTO
     * @return
     */
    Result<List<UserRespDTO>> listUserByRolename(ListUserByRolenameReqDTO listUserByRolenameReqDTO);

}