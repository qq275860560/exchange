package com.ghf.exchange.boss.authorization.permission.service;

import com.ghf.exchange.boss.authorization.permission.dto.*;
import com.ghf.exchange.boss.authorization.permission.entity.Permission;
import com.ghf.exchange.boss.authorization.rolepermission.dto.ListPermissionByRequestUrlReqDTO;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface PermissionService extends BaseService<Permission, Long> {
    /**
     * 分页搜索权限
     *
     * @param pagePermissionReqDTO
     * @return
     */
    Result<PageRespDTO<PermissionRespDTO>> pagePermission(PagePermissionReqDTO pagePermissionReqDTO);

    /**
     * 根据权限名称树状列出权限及其后代
     *
     * @param treePermissionReqDTO
     * @return
     */
    Result<PermissionRespDTO> treePermission(TreePermissionReqDTO treePermissionReqDTO);

    /**
     * 根据权限名称列出权限及其祖先
     *
     * @param listAncestorByPermissionnameReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> listAncestorByPermissionname(ListAncestorByPermissionnameReqDTO listAncestorByPermissionnameReqDTO);

    /**
     * 根据权限名称获取权限详情
     *
     * @param getPermissionByCodeReqDTO
     * @return
     */
    Result<PermissionRespDTO> getPermissionByPermissionname(GetPermissionByPermissionnameReqDTO getPermissionByCodeReqDTO);

    /**
     * 根据请求url列出权限
     *
     * @param listPermissionByRequestUrlReqDTO
     * @return
     */
    Result<List<PermissionRespDTO>> listPermissionByRequestUrl(ListPermissionByRequestUrlReqDTO listPermissionByRequestUrlReqDTO);

    /**
     * 根据权限名称判断权限是否存在
     *
     * @param getPermissionByPermissionnameReqDTO
     * @return
     */
    Result<Boolean> existsPermissionByPermissionname(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO);

    /**
     * 新建权限
     *
     * @param addPermissionReqDTO
     * @return
     */
    Result<Void> addPermission(AddPermissionReqDTO addPermissionReqDTO);

    /**
     * 更新权限
     *
     * @param updatePermissionReqDTO
     * @return
     */
    Result<Void> updatePermissionByPermissionname(UpdatePermissionByPermissionnameReqDTO updatePermissionReqDTO);

    /**
     * 启用权限
     *
     * @param getPermissionByPermissionnameReqDTO
     * @return
     */
    Result<Void> enablePermission(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO);

    /**
     * 禁用权限
     *
     * @param getPermissionByPermissionnameReqDTO
     * @return
     */
    Result<Void> disablePermission(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO);

}