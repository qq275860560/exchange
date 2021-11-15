package com.ghf.exchange.boss.authorication.client.controller;

import com.ghf.exchange.boss.authorication.client.dto.*;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
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
@Api(value = "客户端接口", tags = {"客户端接口"})
@RestController
@Lazy
@Slf4j
public class ClientController {
    @Lazy
    @Resource
    private ClientService clientService;

    @ApiOperation(value = "分页搜索客户端", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/client/pageClient")
    @SneakyThrows
    public Result<PageRespDTO<ClientRespDTO>> pageClient(@RequestBody PageClientReqDTO pageClientReqDTO) {
        return clientService.pageClient(pageClientReqDTO);
    }

    @ApiOperation(value = "列出客户端", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/client/listClient")
    @SneakyThrows
    public Result<List<ClientRespDTO>> listClient(@RequestBody ListClientReqDTO listClientReqDTO) {
        return clientService.listClient(listClientReqDTO);
    }

    @ApiOperation(value = "根据客户端id获取客户端详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/client/getClientByClientId")
    @SneakyThrows
    public Result<ClientRespDTO> getClientByClientId(@RequestBody GetClientByClientIdReqDTO getClientByClientIdReqDTO) {
        return clientService.getClientByClientId(getClientByClientIdReqDTO);
    }

    @ApiOperation(value = "获取当前登录客户端详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/client/getCurrentLoginClient")
    @SneakyThrows
    public Result<ClientRespDTO> getCurrentLoginClient() {
        return clientService.getCurrentLoginClient();
    }

    @ApiOperation(value = "保存客户端", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/client/addClient")
    @SneakyThrows
    public Result<Void> addClient(@RequestBody AddClientReqDTO addClientReqDTO) {
        return clientService.addClient(addClientReqDTO);
    }

    @ApiOperation(value = "更新当前客户端密码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/client/updateClientPassword")
    @SneakyThrows
    public Result<Void> updateClientPassword(@RequestBody UpdateClientPasswordReqDTO updateClientPassword) {
        return clientService.updateClientPassword(updateClientPassword);
    }

}
