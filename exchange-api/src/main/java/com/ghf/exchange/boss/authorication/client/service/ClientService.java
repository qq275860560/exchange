package com.ghf.exchange.boss.authorication.client.service;

import com.ghf.exchange.boss.authorication.client.dto.*;
import com.ghf.exchange.boss.authorication.client.entity.Client;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface ClientService extends BaseService<Client, Long> {

    /**
     * 分页搜索客户端
     *
     * @param pageClientReqDTO
     * @return
     */

    Result<PageRespDTO<ClientRespDTO>> pageClient(PageClientReqDTO pageClientReqDTO);

    /**
     * 列出客户端
     *
     * @param listClientReqDTO
     * @return
     */
    Result<List<ClientRespDTO>> listClient(ListClientReqDTO listClientReqDTO);

    /**
     * 获取当前登录客户端
     *
     * @return
     */
    Result<ClientRespDTO> getCurrentLoginClient();

    /**
     * 获取当前登录客户端
     *
     * @param getClientByClientIdReqDTO
     * @return
     */
    Result<ClientRespDTO> getClientByClientId(GetClientByClientIdReqDTO getClientByClientIdReqDTO);

    /**
     * 根据客户端id判断客户端是否存在
     *
     * @param getClientByClientIdReqDTO
     * @return
     */
    Result<Boolean> existsClientByClientId(GetClientByClientIdReqDTO getClientByClientIdReqDTO);

    /**
     * 获取spring security客户端详情
     * <p>
     * 客户端密码 在登录阶段时，要调用此接口获取到客户端密码，之后跟加密后的登录密码比较
     * 根据客户端ID查询密码，此密码非明文密码，而是PasswordEncoder对明文加密后的密码，因为 spring
     * security框架中数据库默认保存的是PasswordEncoder对明文加密后的密码
     * 客户端发送的密码加密后会跟这个函数返回的密码相匹配，如果成功，则认证成功，并保存到session中，
     * 对于oauth2的密码模式和认证码模式程序任何地方可以通过以下代码获取当前的用户名称 String
     * username=(String)SecurityContextHolder.getContext().getAuthentication().getName();
     * 对于oauth2的客户端模式程序任何地方可以通过以下代码获取当前的客户端id和资源所有者名称(客户端模式的资源所有者为空)
     * OAuth2Authentication oAuth2Authentication =
     * (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();
     * String username=
     * oAuth2Authentication.getUserAuthentication()==null?null:oAuth2Authentication.getUserAuthentication().getName();
     * String clientId=oAuth2Authentication.getOAuth2Request().getClientId();
     * log.info("资源用户名称=" + username+",客户端id="+clientId);
     * 再根据客户端id和资源所有者名称查询数据库获得其他信息
     * <p>
     * <p>
     * 认证码接收地址(回调地址)集合 在认证码模式中，当用户同意发送授权码时，需要把认证码告知客户端，此时客户端必须提供一个支持get请求的url作为回调地址
     * 授权服务器会直接在 回调地址后面追加code=XXX参数进行重定向 回调地址通常只有一个，但也支持多个，但只有跟用户同意授权的那个认证码才有效
     * <p>
     * <p>
     * <p>
     * 授权类型集合 通常网关（本应用客户端）支持客户端模式和密码模式,第三方客户端支持客户端模式和认证码模式
     * <p>
     * <p>
     * <p>
     * SCOPE集合 在客户端访问系统时，需要对uri进行权限校验， 当客户端的scopes包含资源对应的所有SCOPE时，访问资源才能成功
     * 浏览器发送/oauth/authorize请求时scope参数值不需要前缀SCOPE_
     * <p>
     * <p>
     * <p>
     * 自动同意SCOPE集合
     * 在认证码模式中，当用户申请授权码时，授权系统会把客户端的申请的所有SCOPE告知用户，如果某一个SCOPE设置为自动同意，则不会告知
     * <p>
     * <p>
     * <p>
     * <p>
     * token的过期时间(单位为秒)
     *
     * @param clientId
     * @return
     */
    ClientDetails getClientDetailsByClientId(String clientId);

    /**
     * 登录
     *
     * @param loginReqDTO
     * @return
     */
    Result<LoginClientRespDTO> loginClient(LoginClientReqDTO loginReqDTO);

    /**
     * 保存客户端
     *
     * @param addClientReqDTO
     * @return
     */
    Result<Void> addClient(AddClientReqDTO addClientReqDTO);

    /**
     * 更新当前客户端密码
     *
     * @param updateClientPassword
     * @return
     */
    Result<Void> updateClientPassword(UpdateClientPasswordReqDTO updateClientPassword);

}