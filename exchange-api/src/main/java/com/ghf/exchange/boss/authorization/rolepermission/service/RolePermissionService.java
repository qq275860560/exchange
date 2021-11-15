package com.ghf.exchange.boss.authorization.rolepermission.service;

import com.ghf.exchange.boss.authorization.permission.dto.PermissionRespDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.rolepermission.dto.*;
import com.ghf.exchange.boss.authorization.rolepermission.entity.RolePermission;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface RolePermissionService extends BaseService<RolePermission, Long> {
    /**
     * 分页搜索角色权限关联
     *
     * @param pageRolePermissionReqDTO
     * @return
     */
    Result<PageRespDTO<RolePermissionRespDTO>> pageRolePermission(PageRolePermissionReqDTO pageRolePermissionReqDTO);

    /**
     * 根据角色英文名称列出角色权限关联
     *
     * @param listRolePermissionByRolenameReqDTO
     * @return
     */
    Result<List<RolePermissionRespDTO>> listRolePermissionByRolename(ListRolePermissionByRolenameReqDTO listRolePermissionByRolenameReqDTO);

    /**
     * 根据权限英文名称列出角色权限关联
     *
     * @param listRolePermissionByPermissionnameReqDTO
     * @return
     */
    Result<List<RolePermissionRespDTO>> listRolePermissionByPermissionname(ListRolePermissionByPermissionnameReqDTO listRolePermissionByPermissionnameReqDTO);

    /**
     * 根据角色英文名称和权限英文名称获取角色权限关联详情
     *
     * @param getRolePermissionByRolenameAndPermissionnameReqDTO
     * @return
     */
    Result<RolePermissionRespDTO> getRolePermissionByRolenameAndPermissionname(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO);

    /**
     * 根据角色英文名称和权限英文名称判断角色权限关联是否存在
     *
     * @param getRolePermissionByRolenameAndPermissionnameReqDTO
     * @return
     */
    Result<Boolean> existsRolePermissionByRolenameAndPermissionname(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO);

    /**
     * 新建角色权限关联
     *
     * @param addRolePermissionReqDTO
     * @return
     */
    Result<Void> addRolePermission(AddRolePermissionReqDTO addRolePermissionReqDTO);

    /**
     * 启用角色权限关联
     *
     * @param getRolePermissionByRolenameAndPermissionnameReqDTO
     * @return
     */
    Result<Void> enableRolePermission(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO);

    /**
     * 禁用角色权限关联
     *
     * @param getRolePermissionByRolenameAndPermissionnameReqDTO
     * @return
     */
    Result<Void> disableRolePermission(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO);

    /**
     * 根据角色英文名称列出权限
     *
     * @param listPermissionByRolenameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> listPermissionByRolename(ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO);

    /**
     * 根据角色英文名称列出权限
     *
     * @param listPermissionByRolenameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> treePermissionByRolename(ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO);

    /**
     * 根据权限英文名称列出角色
     *
     * @param listRoleByPermissionnameReqDTO
     * @return
     */
    Result<List<RoleRespDTO>> listRoleByPermissionname(ListRoleByPermissionnameReqDTO listRoleByPermissionnameReqDTO);

    /**
     * 根据组织英文名称列出权限
     *
     * @param listPermissionByOrgnameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> listPermissionByOrgname(ListPermissionByOrgnameReqDTO listPermissionByOrgnameReqDTO);

    /**
     * 根据组织英文名称树状列出权限
     *
     * @param listPermissionByOrgnameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> treePermissionByOrgname(ListPermissionByOrgnameReqDTO listPermissionByOrgnameReqDTO);

    /**
     * 根据用户英文名称列出权限
     *
     * @param listPermissionByUsernameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> listPermissionByUsername(ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO);

    /**
     * 根据用户英文名称树状列出权限
     *
     * @param listPermissionByUsernameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> treePermissionByUsername(ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO);

    /**
     * 列出当前登录用户的权限
     *
     * @param listCurrentLoginUserPermissionReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> listCurrentLoginUserPermission(ListCurrentLoginUserPermissionReqDTO listCurrentLoginUserPermissionReqDTO);

    /**
     * 树状列出当前登录用户的权限
     *
     * @param listCurrentLoginUserPermissionReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> treeCurrentLoginUserPermission(ListCurrentLoginUserPermissionReqDTO listCurrentLoginUserPermissionReqDTO);

    ;

    /**
     * 根据用户英文名称和权限英文名称判断是否存在共同角色
     *
     * @param existsCommonRoleByUsernameAndPermissiondescReqDTO
     * @return
     */
    Result<Boolean> existsCommonRoleByUsernameAndPermissionname(ExistsCommonRoleByUsernameAndPermissionnameReqDTO existsCommonRoleByUsernameAndPermissiondescReqDTO);

    /**
     * 确定登录用户是否具备url的访问角色
     *
     * @param request
     * @param authentication
     * @return
     */
    boolean decideRole(HttpServletRequest request, Authentication authentication);
}