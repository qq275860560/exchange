package com.ghf.exchange.boss.authorization.role.controller;

import com.ghf.exchange.boss.authorization.role.dto.*;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
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
@Api(value = "角色接口", tags = {"角色接口"})
@RestController
@Lazy
@Slf4j
public class RoleController {

    @Lazy
    @Resource
    private RoleService roleService;

    @ApiOperation(value = "分页搜索角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/pageRole")
    @SneakyThrows
    public Result<PageRespDTO<RoleRespDTO>> pageRole(@RequestBody PageRoleReqDTO pageRoleReqDTO) {
        return roleService.pageRole(pageRoleReqDTO);
    }

    @ApiOperation(value = "列出角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/listRole")
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRole(@RequestBody ListRoleReqDTO listRoleReqDTO) {
        return roleService.listRole(listRoleReqDTO);
    }

    @ApiOperation(value = "根据角色名称获取角色详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/getRoleByRolename")
    @SneakyThrows
    public Result<RoleRespDTO> getRoleByRolename(@RequestBody GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        return roleService.getRoleByRolename(getRoleByRolenameReqDTO);
    }

    @ApiOperation(value = "根据角色名称判断角色是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/existsRoleByRolename")
    @SneakyThrows
    public Result<Boolean> existsRoleByRolename(@RequestBody GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        return roleService.existsRoleByRolename(getRoleByRolenameReqDTO);
    }

    @ApiOperation(value = "新建角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/addRole")
    @SneakyThrows
    public Result<Void> addRole(@RequestBody AddRoleReqDTO addRoleReqDTO) {
        return roleService.addRole(addRoleReqDTO);
    }

    @ApiOperation(value = "更新角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/updateRoleByRolename")
    @SneakyThrows
    public Result<Void> updateRoleByRolename(@RequestBody UpdateRoleByRolenameReqDTO updateRoleReqDTO) {
        return roleService.updateRoleByRolename(updateRoleReqDTO);
    }

    @ApiOperation(value = "启用角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/enableRole")
    @SneakyThrows
    public Result<Void> enableRole(@RequestBody GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        return roleService.enableRole(getRoleByRolenameReqDTO);
    }

    @ApiOperation(value = "禁用角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/role/disableRole")
    @SneakyThrows
    public Result<Void> disableRole(@RequestBody GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        return roleService.disableRole(getRoleByRolenameReqDTO);
    }
}
