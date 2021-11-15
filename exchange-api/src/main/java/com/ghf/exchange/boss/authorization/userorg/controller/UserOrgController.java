package com.ghf.exchange.boss.authorization.userorg.controller;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.*;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
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
@Api(value = "用户组织关联接口", tags = {"用户组织关联接口"})
@RestController
@Lazy
@Slf4j
public class UserOrgController {
    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @ApiOperation(value = "分页搜索用户组织关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/pageUserOrg")
    @SneakyThrows
    public Result<PageRespDTO<UserOrgRespDTO>> pageUserOrg(@RequestBody PageUserOrgReqDTO pageUserOrgReqDTO) {
        return userOrgService.pageUserOrg(pageUserOrgReqDTO);
    }

    @ApiOperation(value = "根据用户名列出用户组织关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/listUserOrgByUsername")
    @SneakyThrows
    public Result<List<UserOrgRespDTO>> listUserOrgByUsername(@RequestBody ListUserOrgByUsernameReqDTO listUserOrgByUsernameReqDTO) {
        return userOrgService.listUserOrgByUsername(listUserOrgByUsernameReqDTO);
    }

    @ApiOperation(value = "根据组织名称列出用户组织关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/listUserOrgByOrgname")
    @SneakyThrows
    public Result<List<UserOrgRespDTO>> listUserOrgByOrgname(@RequestBody ListUserOrgByOrgnameReqDTO listUserOrgByOrgnameReqDTO) {
        return userOrgService.listUserOrgByOrgname(listUserOrgByOrgnameReqDTO);
    }

    @ApiOperation(value = "根据用户名和组织名称获取用户组织关联详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/getUserOrgByUsernameAndOrgname")
    @SneakyThrows
    public Result<UserOrgRespDTO> getUserOrgByUsernameAndOrgname(@RequestBody GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        return userOrgService.getUserOrgByUsernameAndOrgname(getUserOrgByUsernameAndOrgnameReqDTO);
    }

    @ApiOperation(value = "根据用户名和组织名称判断用户组织关联是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/existsUserOrgByUsernameAndOrgname")
    @SneakyThrows
    public Result<Boolean> existsUserOrgByUsernameAndOrgname(@RequestBody GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        return userOrgService.existsUserOrgByUsernameAndOrgname(getUserOrgByUsernameAndOrgnameReqDTO);
    }

    @ApiOperation(value = "新建用户组织关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/addUserOrg")
    @SneakyThrows
    public Result<Void> addUserOrg(@RequestBody AddUserOrgReqDTO addUserOrgReqDTO) {
        return userOrgService.addUserOrg(addUserOrgReqDTO);
    }

    @ApiOperation(value = "启用用户组织关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/enableUserOrg")
    @SneakyThrows
    public Result<Void> enableUserOrg(@RequestBody GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        return userOrgService.enableUserOrg(getUserOrgByUsernameAndOrgnameReqDTO);
    }

    @ApiOperation(value = "禁用用户组织关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/disableUserOrg")
    @SneakyThrows
    public Result<Void> disableUserOrg(@RequestBody GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        return userOrgService.disableUserOrg(getUserOrgByUsernameAndOrgnameReqDTO);
    }

    @ApiOperation(value = "根据用户名列出组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/listOrgByUsername")
    @SneakyThrows
    public Result<List<OrgRespDTO>> listOrgByUsername(@RequestBody ListOrgByUsernameReqDTO listOrgByUsernameReqDTO) {
        return userOrgService.listOrgByUsername(listOrgByUsernameReqDTO);
    }

    @ApiOperation(value = "根据组织名称列出用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/userOrg/listUserByOrgname")
    @SneakyThrows
    public Result<List<UserRespDTO>> listUserByOrgname(@RequestBody ListUserByOrgnameReqDTO listUserByOrgnameReqDTO) {
        return userOrgService.listUserByOrgname(listUserByOrgnameReqDTO);
    }

}
