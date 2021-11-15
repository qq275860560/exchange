package com.ghf.exchange.boss.authorization.userorg.service;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.*;
import com.ghf.exchange.boss.authorization.userorg.entity.UserOrg;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface UserOrgService extends BaseService<UserOrg, Long> {
    /**
     * 分页搜索用户组织关联
     *
     * @param pageUserOrgReqDTO
     * @return
     */
    Result<PageRespDTO<UserOrgRespDTO>> pageUserOrg(PageUserOrgReqDTO pageUserOrgReqDTO);

    /**
     * 根据用户英文名称列出用户组织关联
     *
     * @param listUserOrgByUsernameReqDTO
     * @return
     */
    Result<List<UserOrgRespDTO>> listUserOrgByUsername(ListUserOrgByUsernameReqDTO listUserOrgByUsernameReqDTO);

    /**
     * 根据组织英文名称列出用户组织关联
     *
     * @param listUserOrgByOrgnameReqDTO
     * @return
     */
    Result<List<UserOrgRespDTO>> listUserOrgByOrgname(ListUserOrgByOrgnameReqDTO listUserOrgByOrgnameReqDTO);

    /**
     * 根据用户英文名称和组织英文名称获取用户组织关联详情
     *
     * @param getUserOrgByUsernameAndOrgnameReqDTO
     * @return
     */
    Result<UserOrgRespDTO> getUserOrgByUsernameAndOrgname(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO);

    /**
     * 根据用户英文名称和组织英文名称判断用户组织关联是否存在
     *
     * @param getUserOrgByUsernameAndOrgnameReqDTO
     * @return
     */
    Result<Boolean> existsUserOrgByUsernameAndOrgname(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO);

    /**
     * 新建用户组织关联
     *
     * @param addUserOrgReqDTO
     * @return
     */
    Result<Void> addUserOrg(AddUserOrgReqDTO addUserOrgReqDTO);

    /**
     * 启用用户组织关联
     *
     * @param getUserOrgByUsernameAndOrgnameReqDTO
     * @return
     */
    Result<Void> enableUserOrg(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO);

    /**
     * 禁用用户组织关联
     *
     * @param getUserOrgByUsernameAndOrgnameReqDTO
     * @return
     */
    Result<Void> disableUserOrg(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO);

    /**
     * 根据用户英文名称列出组织
     *
     * @param listOrgByUsernameReqDTO
     * @return
     */
    Result<List<OrgRespDTO>> listOrgByUsername(ListOrgByUsernameReqDTO listOrgByUsernameReqDTO);

    /**
     * 根据组织英文名称列出用户
     *
     * @param listUserByOrgnameReqDTO
     * @return
     */
    Result<List<UserRespDTO>> listUserByOrgname(ListUserByOrgnameReqDTO listUserByOrgnameReqDTO);

    /**
     * 根据用户英文名称和组织英文名称判断用户是否在组织或组织的祖宗上(组织或组织的祖宗组织是否存在该用户)
     *
     * @param existUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO
     * @return
     */
    Result<Boolean> existUserOrgOrUserAncestorOrgByUserAndOrgname(ExistUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO existUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO);

    /**
     * 根据用户英文名称和组织英文名称判断用户是否在组织或组织的后代上(组织或组织的后代组织是否存在该用户)
     *
     * @param existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO
     * @return
     */
    Result<Boolean> existUserOrgOrUserDescendantOrgByUserAndOrgname(ExistUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO);

    /**
     * 根据组织英文名称列表获取所有用户
     *
     * @param orgNames 组织名列表
     * @return
     */
    Result<List<Long>> listUserOrgByOrgNames(List<String> orgNames);
}