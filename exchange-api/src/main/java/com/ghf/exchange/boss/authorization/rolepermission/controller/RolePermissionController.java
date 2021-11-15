package com.ghf.exchange.boss.authorization.rolepermission.controller;

import com.ghf.exchange.boss.authorization.permission.dto.PermissionRespDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.rolepermission.dto.*;
import com.ghf.exchange.boss.authorization.rolepermission.service.RolePermissionService;
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
@Api(value = "角色权限关联接口", tags = {"角色权限关联接口"})
@RestController
@Lazy
@Slf4j
public class RolePermissionController {

    @Lazy
    @Resource
    private RolePermissionService rolePermissionService;

    @ApiOperation(value = "分页搜索角色权限关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/pageRolePermission")
    @SneakyThrows
    public Result<PageRespDTO<RolePermissionRespDTO>> pageRolePermission(@RequestBody PageRolePermissionReqDTO pageRolePermissionReqDTO) {
        return rolePermissionService.pageRolePermission(pageRolePermissionReqDTO);
    }

    @ApiOperation(value = "根据角色名列出角色权限关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listRolePermissionByRolename")
    @SneakyThrows
    public Result<List<RolePermissionRespDTO>> listRolePermissionByRolename(@RequestBody ListRolePermissionByRolenameReqDTO listRolePermissionByRolenameReqDTO) {
        return rolePermissionService.listRolePermissionByRolename(listRolePermissionByRolenameReqDTO);
    }

    @ApiOperation(value = "根据权限名称列出角色权限关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listRolePermissionByPermissionname")
    @SneakyThrows
    public Result<List<RolePermissionRespDTO>> listRolePermissionByPermissionname(@RequestBody ListRolePermissionByPermissionnameReqDTO listRolePermissionByPermissionnameReqDTO) {
        return rolePermissionService.listRolePermissionByPermissionname(listRolePermissionByPermissionnameReqDTO);
    }

    @ApiOperation(value = "根据角色名和权限名称获取角色权限关联详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/getRolePermissionByRolenameAndPermissionname")
    @SneakyThrows
    public Result<RolePermissionRespDTO> getRolePermissionByRolenameAndPermissionname(@RequestBody GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        return rolePermissionService.getRolePermissionByRolenameAndPermissionname(getRolePermissionByRolenameAndPermissionnameReqDTO);
    }

    @ApiOperation(value = "根据角色名和权限名称判断角色权限关联是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/existsRolePermissionByRolenameAndPermissionname")
    @SneakyThrows
    public Result<Boolean> existsRolePermissionByRolenameAndPermissionname(@RequestBody GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        return rolePermissionService.existsRolePermissionByRolenameAndPermissionname(getRolePermissionByRolenameAndPermissionnameReqDTO);
    }

    @ApiOperation(value = "新建角色权限关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/addRolePermission")
    @SneakyThrows
    public Result<Void> addRolePermission(@RequestBody AddRolePermissionReqDTO addRolePermissionReqDTO) {
        return rolePermissionService.addRolePermission(addRolePermissionReqDTO);
    }

    @ApiOperation(value = "启用角色权限关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/enableRolePermission")
    @SneakyThrows
    public Result<Void> enableRolePermission(@RequestBody GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        return rolePermissionService.enableRolePermission(getRolePermissionByRolenameAndPermissionnameReqDTO);
    }

    @ApiOperation(value = "禁用角色权限关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/disableRolePermission")
    @SneakyThrows
    public Result<Void> disableRolePermission(@RequestBody GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        return rolePermissionService.disableRolePermission(getRolePermissionByRolenameAndPermissionnameReqDTO);
    }

    @ApiOperation(value = "根据角色名列出权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listPermissionByRolename")
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByRolename(@RequestBody ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO) {
        return rolePermissionService.listPermissionByRolename(listPermissionByRolenameReqDTO);
    }

    @ApiOperation(value = "根据权限名称列出角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listRoleByPermissionname")
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRoleByPermissionname(@RequestBody ListRoleByPermissionnameReqDTO listRoleByPermissionnameReqDTO) {
        return rolePermissionService.listRoleByPermissionname(listRoleByPermissionnameReqDTO);
    }

    @ApiOperation(value = "根据组织名称列出权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listPermissionByOrgname")
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByOrgname(@RequestBody ListPermissionByOrgnameReqDTO listPermissionByOrgnameReqDTO) {
        return rolePermissionService.listPermissionByOrgname(listPermissionByOrgnameReqDTO);
    }

    @ApiOperation(value = "根据用户名列出权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listPermissionByUsername")
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByUsername(@RequestBody ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO) {
        return rolePermissionService.listPermissionByUsername(listPermissionByUsernameReqDTO);
    }

    @ApiOperation(value = "根据用户名树状列出权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/treePermissionByUsername")
    @SneakyThrows
    public Result<List<PermissionRespDTO>> treePermissionByUsername(@RequestBody ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO) {
        return rolePermissionService.treePermissionByUsername(listPermissionByUsernameReqDTO);
    }

    @ApiOperation(value = "列出当前登录用户的权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/listCurrentLoginUserPermission")
    @SneakyThrows
    Result<List<PermissionRespDTO>> listCurrentLoginUserPermission(@RequestBody ListCurrentLoginUserPermissionReqDTO listCurrentLoginUserPermissionReqDTO) {
        return rolePermissionService.listCurrentLoginUserPermission(listCurrentLoginUserPermissionReqDTO);
    }

    @ApiOperation(value = "树状列出当前登录用户的权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/treeCurrentLoginUserPermission")
    @SneakyThrows
    Result<List<PermissionRespDTO>> treeCurrentLoginUserPermission(@RequestBody ListCurrentLoginUserPermissionReqDTO listCurrentLoginUserPermissionReqDTO) {
        return rolePermissionService.treeCurrentLoginUserPermission(listCurrentLoginUserPermissionReqDTO);
    }

    @ApiOperation(value = "根据用户名和权限名称判断是否存在共同角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/rolePermission/existsCommonRoleByUsernameAndPermissionname")
    @SneakyThrows
    public Result<Boolean> existsCommonRoleByUsernameAndPermissionname(@RequestBody ExistsCommonRoleByUsernameAndPermissionnameReqDTO existsCommonRoleByUsernameAndPermissiondescReqDTO) {
        return rolePermissionService.existsCommonRoleByUsernameAndPermissionname(existsCommonRoleByUsernameAndPermissiondescReqDTO);
    }

}
