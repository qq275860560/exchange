package com.ghf.exchange.boss.authorization.permission.controller;

import com.ghf.exchange.boss.authorization.permission.dto.*;
import com.ghf.exchange.boss.authorization.permission.service.PermissionService;
import com.ghf.exchange.boss.authorization.rolepermission.dto.ListPermissionByRequestUrlReqDTO;
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
@Api(value = "权限接口", tags = {"权限接口"})
@RestController
@Lazy
@Slf4j
public class PermissionController {

    @Lazy
    @Resource
    private PermissionService permissionService;

    @ApiOperation(value = "分页搜索权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/pagePermission")
    @SneakyThrows
    public Result<PageRespDTO<PermissionRespDTO>> pagePermission(@RequestBody PagePermissionReqDTO pagePermissionReqDTO) {
        return permissionService.pagePermission(pagePermissionReqDTO);
    }

    @ApiOperation(value = "根据权限名称树状列出权限及其后代", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/treePermission")
    @SneakyThrows
    public Result<PermissionRespDTO> treePermission(@RequestBody TreePermissionReqDTO treePermissionReqDTO) {
        return permissionService.treePermission(treePermissionReqDTO);
    }

    @ApiOperation(value = "根据权限名称获取权限详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/getPermissionByPermissionname")
    @SneakyThrows
    public Result<PermissionRespDTO> getPermissionByPermissionname(@RequestBody GetPermissionByPermissionnameReqDTO getPermissionByCodeReqDTO) {
        return permissionService.getPermissionByPermissionname(getPermissionByCodeReqDTO);
    }

    @ApiOperation(value = "根据权限名称判断权限是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/existsPermissionByPermissionname")
    @SneakyThrows
    public Result<Boolean> existsPermissionByPermissionname(@RequestBody GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        return permissionService.existsPermissionByPermissionname(getPermissionByPermissionnameReqDTO);
    }

    @ApiOperation(value = "新建权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/addPermission")
    @SneakyThrows
    public Result<Void> addPermission(@RequestBody AddPermissionReqDTO addPermissionReqDTO) {
        return permissionService.addPermission(addPermissionReqDTO);
    }

    @ApiOperation(value = "更新权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/updatePermissionByPermissionname")
    @SneakyThrows
    public Result<Void> updatePermissionByPermissionname(@RequestBody UpdatePermissionByPermissionnameReqDTO updatePermissionReqDTO) {
        return permissionService.updatePermissionByPermissionname(updatePermissionReqDTO);
    }

    @ApiOperation(value = "启用权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/enablePermission")
    @SneakyThrows
    public Result<Void> enablePermission(@RequestBody GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        return permissionService.enablePermission(getPermissionByPermissionnameReqDTO);
    }

    @ApiOperation(value = "禁用权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/disablePermission")
    @SneakyThrows
    public Result<Void> disablePermission(@RequestBody GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        return permissionService.disablePermission(getPermissionByPermissionnameReqDTO);
    }

    @ApiOperation(value = "根据请求url列出权限", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/permission/listPermissionByRequestUrl")
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByRequestUrl(@RequestBody ListPermissionByRequestUrlReqDTO listPermissionByRequestUrlReqDTO) {
        return permissionService.listPermissionByRequestUrl(listPermissionByRequestUrlReqDTO);
    }
}
