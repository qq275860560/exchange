package com.ghf.exchange.boss.authorization.userrole.controller;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.userrole.dto.*;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
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
@Api(value = "用户角色关联接口", tags = {"用户角色关联接口"})
@RestController
@Lazy
@Slf4j
public class UserRoleController {
    @Lazy
    @Resource
    private UserRoleService userRoleService;

    @ApiOperation(value = "分页搜索用户角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/pageUserRole")
    @SneakyThrows
    public Result<PageRespDTO<UserRoleRespDTO>> pageUserRole(@RequestBody PageUserRoleReqDTO pageUserRoleReqDTO) {
        return userRoleService.pageUserRole(pageUserRoleReqDTO);
    }

    @ApiOperation(value = "根据用户名列出用户角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/listUserRoleByUsername")
    @SneakyThrows
    public Result<List<UserRoleRespDTO>> listUserRoleByUsername(@RequestBody ListUserRoleByUsernameReqDTO listUserRoleByUsernameReqDTO) {
        return userRoleService.listUserRoleByUsername(listUserRoleByUsernameReqDTO);
    }

    @ApiOperation(value = "根据角色名列出用户角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/listUserRoleByRolename")
    @SneakyThrows
    public Result<List<UserRoleRespDTO>> listUserRoleByRolename(@RequestBody ListUserRoleByRolenameReqDTO listUserRoleByRolenameReqDTO) {
        return userRoleService.listUserRoleByRolename(listUserRoleByRolenameReqDTO);
    }

    @ApiOperation(value = "根据用户名和角色名获取用户角色关联详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/getUserRoleByUsernameAndRolename")
    @SneakyThrows
    public Result<UserRoleRespDTO> getUserRoleByUsernameAndRolename(@RequestBody GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        return userRoleService.getUserRoleByUsernameAndRolename(getUserRoleByUsernameAndRolenameReqDTO);
    }

    @ApiOperation(value = "根据用户名和角色名判断用户角色关联是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/existsUserRoleByUsernameAndRolename")
    @SneakyThrows
    public Result<Boolean> existsUserRoleByUsernameAndRolename(@RequestBody GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        return userRoleService.existsUserRoleByUsernameAndRolename(getUserRoleByUsernameAndRolenameReqDTO);
    }

    @ApiOperation(value = "新建用户角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/addUserRole")
    @SneakyThrows
    public Result<Void> addUserRole(@RequestBody AddUserRoleReqDTO addUserRoleReqDTO) {
        return userRoleService.addUserRole(addUserRoleReqDTO);
    }

    @ApiOperation(value = "启用用户角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/enableUserRole")
    @SneakyThrows
    public Result<Void> enableUserRole(@RequestBody GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        return userRoleService.enableUserRole(getUserRoleByUsernameAndRolenameReqDTO);
    }

    @ApiOperation(value = "禁用用户角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/disableUserRole")
    @SneakyThrows
    public Result<Void> disableUserRole(@RequestBody GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        return userRoleService.disableUserRole(getUserRoleByUsernameAndRolenameReqDTO);
    }

    @ApiOperation(value = "根据用户名列出角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/listRoleByUsername")
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRoleByUsername(@RequestBody ListRoleByUsernameReqDTO listRoleByUsernameReqDTO) {
        return userRoleService.listRoleByUsername(listRoleByUsernameReqDTO);
    }

    @ApiOperation(value = "根据角色名列出用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userRole/listUserByRolename")
    @SneakyThrows
    public Result<List<UserRespDTO>> listUserByRolename(@RequestBody ListUserByRolenameReqDTO listUserByRolenameReqDTO) {
        return userRoleService.listUserByRolename(listUserByRolenameReqDTO);
    }
}
