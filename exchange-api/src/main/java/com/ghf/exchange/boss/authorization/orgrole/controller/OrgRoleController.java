package com.ghf.exchange.boss.authorization.orgrole.controller;

import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.orgrole.dto.*;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
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
@Api(value = "组织角色关联接口", tags = {"组织角色关联接口"})
@RestController
@Lazy
@Slf4j
public class OrgRoleController {

    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @ApiOperation(value = "分页搜索组织角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/pageOrgRole")
    @SneakyThrows
    public Result<PageRespDTO<OrgRoleRespDTO>> pageOrgRole(@RequestBody PageOrgRoleReqDTO pageOrgRoleReqDTO) {
        return orgRoleService.pageOrgRole(pageOrgRoleReqDTO);
    }

    @ApiOperation(value = "根据组织名称列出组织角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/listOrgRoleByOrgname")
    @SneakyThrows
    public Result<List<OrgRoleRespDTO>> listOrgRoleByOrgname(@RequestBody ListOrgRoleByOrgnameReqDTO listOrgRoleByOrgnameReqDTO) {
        return orgRoleService.listOrgRoleByOrgname(listOrgRoleByOrgnameReqDTO);
    }

    @ApiOperation(value = "根据角色名列出组织角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/listOrgRoleByRolename")
    @SneakyThrows
    public Result<List<OrgRoleRespDTO>> listOrgRoleByRolename(@RequestBody ListOrgRoleByRolenameReqDTO listOrgRoleByRolenameReqDTO) {
        return orgRoleService.listOrgRoleByRolename(listOrgRoleByRolenameReqDTO);
    }

    @ApiOperation(value = "根据组织名称和角色名获取组织角色关联详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/getOrgRoleByOrgnameAndRolename")
    @SneakyThrows
    public Result<OrgRoleRespDTO> getOrgRoleByOrgnameAndRolename(@RequestBody GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        return orgRoleService.getOrgRoleByOrgnameAndRolename(getOrgRoleByOrgnameAndRolenameReqDTO);
    }

    @ApiOperation(value = "根据组织名称和角色名判断组织角色关联是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/existsOrgRoleByOrgnameAndRolename")
    @SneakyThrows
    public Result<Boolean> existsOrgRoleByOrgnameAndRolename(@RequestBody GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        return orgRoleService.existsOrgRoleByOrgnameAndRolename(getOrgRoleByOrgnameAndRolenameReqDTO);
    }

    @ApiOperation(value = "新建组织角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/addOrgRole")
    @SneakyThrows
    public Result<Void> addOrgRole(@RequestBody AddOrgRoleReqDTO addOrgRoleReqDTO) {
        return orgRoleService.addOrgRole(addOrgRoleReqDTO);
    }

    @ApiOperation(value = "启用组织角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/enableOrgRole")
    @SneakyThrows
    public Result<Void> enableOrgRole(@RequestBody GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        return orgRoleService.enableOrgRole(getOrgRoleByOrgnameAndRolenameReqDTO);
    }

    @ApiOperation(value = "禁用组织角色关联", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/disableOrgRole")
    @SneakyThrows
    public Result<Void> disableOrgRole(@RequestBody GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        return orgRoleService.disableOrgRole(getOrgRoleByOrgnameAndRolenameReqDTO);
    }

    @ApiOperation(value = "根据组织名称列出角色", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/listRoleByOrgname")
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRoleByOrgname(@RequestBody ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO) {
        return orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO);
    }

    @ApiOperation(value = "根据角色名列出组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/orgRole/listOrgByRolename")
    @SneakyThrows
    public Result<List<OrgRespDTO>> listOrgByRolename(@RequestBody ListOrgByRolenameReqDTO listOrgByRolenameReqDTO) {
        return orgRoleService.listOrgByRolename(listOrgByRolenameReqDTO);
    }

}
