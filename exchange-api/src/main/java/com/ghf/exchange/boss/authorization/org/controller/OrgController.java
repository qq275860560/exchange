package com.ghf.exchange.boss.authorization.org.controller;

import com.ghf.exchange.boss.authorization.org.dto.*;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
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
@Api(value = "组织接口", tags = {"组织接口"})
@RestController
@Lazy
@Slf4j
public class OrgController {

    @Lazy
    @Resource
    private OrgService orgService;

    @ApiOperation(value = "分页搜索组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/pageOrg")
    @SneakyThrows
    public Result<PageRespDTO<OrgRespDTO>> pageOrg(@RequestBody PageOrgReqDTO pageOrgReqDTO) {
        return orgService.pageOrg(pageOrgReqDTO);
    }

    @ApiOperation(value = "根据组织名称树状列出组织及其后代", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/treeOrg")
    @SneakyThrows
    public Result<OrgRespDTO> treeOrg(@RequestBody TreeOrgReqDTO treeOrgReqDTO) {
        return orgService.treeOrg(treeOrgReqDTO);
    }

    @ApiOperation(value = "根据组织名称列出组织及其祖先", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/ListAncestorByOrgname")
    @SneakyThrows
    public Result<List<OrgRespDTO>> listAncestorByOrgname(@RequestBody ListAncestorByOrgnameReqDTO listAncestorByOrgnameReqDTO) {
        return orgService.listAncestorByOrgname(listAncestorByOrgnameReqDTO);
    }

    @ApiOperation(value = "根据组织名称获取组织详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/getOrgByOrgname")
    @SneakyThrows
    public Result<OrgRespDTO> getOrgByOrgname(@RequestBody GetOrgByOrgnameReqDTO getOrgByCodeReqDTO) {
        return orgService.getOrgByOrgname(getOrgByCodeReqDTO);
    }

    @ApiOperation(value = "根据组织名称判断组织是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/existsOrgByOrgname")
    @SneakyThrows
    public Result<Boolean> existsOrgByOrgname(@RequestBody GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        return orgService.existsOrgByOrgname(getOrgByOrgnameReqDTO);
    }

    @ApiOperation(value = "新建组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/addOrg")
    @SneakyThrows
    public Result<Void> addOrg(@RequestBody AddOrgReqDTO addOrgReqDTO) {
        return orgService.addOrg(addOrgReqDTO);
    }

    @ApiOperation(value = "更新组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/updateOrgByOrgname")
    @SneakyThrows
    public Result<Void> updateOrgByOrgname(@RequestBody UpdateOrgByOrgnameReqDTO updateOrgReqDTO) {
        return orgService.updateOrgByOrgname(updateOrgReqDTO);
    }

    @ApiOperation(value = "启用组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/enableOrg")
    @SneakyThrows
    public Result<Void> enableOrg(@RequestBody GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        return orgService.enableOrg(getOrgByOrgnameReqDTO);
    }

    @ApiOperation(value = "禁用组织", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/org/disableOrg")
    @SneakyThrows
    public Result<Void> disableOrg(@RequestBody GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        return orgService.disableOrg(getOrgByOrgnameReqDTO);
    }
}
