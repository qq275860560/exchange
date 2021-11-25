package com.ghf.exchange.boss.authorication.client.service.impl;

import com.ghf.exchange.boss.authorication.client.dto.*;
import com.ghf.exchange.boss.authorication.client.entity.Client;
import com.ghf.exchange.boss.authorication.client.entity.QClient;
import com.ghf.exchange.boss.authorication.client.enums.ClientGrantTypeEnum;
import com.ghf.exchange.boss.authorication.client.enums.ClientStatusEnum;
import com.ghf.exchange.boss.authorication.client.event.LoginEvent;
import com.ghf.exchange.boss.authorication.client.repository.ClientRepository;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class ClientServiceImpl extends BaseServiceImpl<Client, Long> implements ClientService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;

    @Lazy
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public ClientServiceImpl(ClientRepository repository) {
        super(repository);
    }

    @Lazy
    @Resource
    public PasswordEncoder passwordEncoder;

    @Cacheable(cacheNames = "Client", key = "'pageClient:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.clientId)  && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    public Result<PageRespDTO<ClientRespDTO>> pageClient(
            PageClientReqDTO pageClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageClientReqDTO.getClientId())) {
            predicate.and(QClient.client.clientId.contains(pageClientReqDTO.getClientId()));
        }
        PageRespDTO<ClientRespDTO> pageRespDTO = this.page(predicate, pageClientReqDTO, ClientRespDTO.class);

        pageRespDTO.getList().forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getScopes())) {
                e.setScopeSet(Arrays.stream(e.getScopes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
        });
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Client", key = "'listClient:'+#p0.clientId")
    @Override
    public Result<List<ClientRespDTO>> listClient(ListClientReqDTO listClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listClientReqDTO.getClientId())) {
            predicate.and(QClient.client.clientId.contains(listClientReqDTO.getClientId()));
        }
        //此接口只能获取启用状态的
        predicate.and(QClient.client.status.eq(ClientStatusEnum.ENABLE.getCode()));
        List<ClientRespDTO> list = clientService.list(predicate, ClientRespDTO.class);
        list.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getScopes())) {
                e.setScopeSet(Arrays.stream(e.getScopes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
        });
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<ClientRespDTO> getCurrentLoginClient() {

        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        //获取登陆客户端id
        String clientId = oAuth2Authentication.getOAuth2Request().getClientId();

        Predicate predicate = QClient.client.clientId.eq(clientId);
        Client client = this.get(predicate);
        //返回
        ClientRespDTO clientRespDTO = AutoMapUtils.map(client, ClientRespDTO.class);
        if (!ObjectUtils.isEmpty(clientRespDTO.getScopes())) {
            clientRespDTO.setScopeSet(Arrays.stream(clientRespDTO.getScopes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        return new Result<>(clientRespDTO);

    }

    @Cacheable(cacheNames = "Client", key = "'getClientByClientId:'+#p0.clientId")
    @Override
    @SneakyThrows
    public Result<ClientRespDTO> getClientByClientId(GetClientByClientIdReqDTO getClientByClientIdReqDTO) {
        //TODO 权限判断
        String clientId = getClientByClientIdReqDTO.getClientId();
        Predicate predicate = QClient.client.clientId.eq(clientId);
        Client client = this.get(predicate);
        //返回
        ClientRespDTO clientRespDTO = AutoMapUtils.map(client, ClientRespDTO.class);
        if (!ObjectUtils.isEmpty(clientRespDTO.getScopes())) {
            clientRespDTO.setScopeSet(Arrays.stream(clientRespDTO.getScopes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        return new Result<>(clientRespDTO);

    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsClientByClientId(GetClientByClientIdReqDTO getClientByClientIdReqDTO) {
        //TODO 权限判断
        String clientId = getClientByClientIdReqDTO.getClientId();
        Predicate predicate = QClient.client.clientId.eq(clientId);
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    @Override
    public ClientDetails getClientDetailsByClientId(String clientId) {
        Predicate predicate = QClient.client.clientId.eq(clientId);
        Client client = this.get(predicate);

        ;

        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId(clientId);
        baseClientDetails.setClientSecret(client.getClientSecret());
        // 接收认证码的url
        if (!ObjectUtils.isEmpty(client.getRegisteredRedirectUris())) {
            Set<String> registeredRedirectUris = new HashSet<>(
                    Arrays.asList(client.getRegisteredRedirectUris().split(",")));

            baseClientDetails.setRegisteredRedirectUri(registeredRedirectUris);
        }
        if (!ObjectUtils.isEmpty(client.getAuthorizedGrantTypes())) {
            Set<String> authorizedGrantTypes = new HashSet<>(
                    Arrays.asList(client.getAuthorizedGrantTypes().split(",")));

            baseClientDetails.setAuthorizedGrantTypes(authorizedGrantTypes);
        }
        // 客户端的权限
        if (!ObjectUtils.isEmpty(client.getScopes())) {
            Set<String> scopes = new HashSet<>(Arrays.asList(client.getScopes().split(",")));

            baseClientDetails.setScope(scopes);
            // 默认自动授权
            baseClientDetails.setAutoApproveScopes(scopes);
        }

        if (client.getAccessTokenValiditySeconds() != 0) {
            baseClientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        }
        return baseClientDetails;

    }

    @Override
    public Result<LoginClientRespDTO> loginClient(LoginClientReqDTO loginClientReqDTO) {

        Map<String, String> requestParameters = new HashMap<String, String>(16) {{
            //授权类型,此接口默认客户端模式登录
            put(OAuth2Utils.GRANT_TYPE, ClientGrantTypeEnum.CLIENT_CREDENTIALS.getCode());

        }};

        String clientId = loginClientReqDTO.getClientId();
        String secret = loginClientReqDTO.getClientSecret();
        String grantType = requestParameters.get(OAuth2Utils.GRANT_TYPE);
        ClientDetails clientDetails = clientService.getClientDetailsByClientId(clientId);
        Set<String> scopes = clientDetails.getScope();
        TokenRequest tokenRequest = new TokenRequest(requestParameters, clientId, scopes, grantType);

        Predicate predicate = QClient.client.clientId.eq(clientId);
        Client client = clientService.get(predicate);

        if (client == null) {
            return new Result<>(ResultCodeEnum.CLIENT_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(secret, client.getClientSecret())) {
            return new Result<>(ResultCodeEnum.PASSWORD_ERROR);
        }
        if (client.getStatus() == ClientStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.CLIENT_STATUS_DISABLE);
        }

        OAuth2Request storedOauth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(storedOauth2Request, null);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        OAuth2AccessToken oAuth2AccessToken = jwtAccessTokenConverter.enhance(token, oAuth2Authentication);

        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new LoginEvent(loginClientReqDTO));

        //TODO封装返回对象，包括个人信息菜单按钮请求url权限
        LoginClientRespDTO loginRespDTO = AutoMapUtils.map(client, LoginClientRespDTO.class);
        loginRespDTO.setAccessToken(oAuth2AccessToken.getValue());
        loginRespDTO.setTokenType(oAuth2AccessToken.getTokenType());

        //返回

        if (!ObjectUtils.isEmpty(loginRespDTO.getScopes())) {
            loginRespDTO.setScopeSet(Arrays.stream(loginRespDTO.getScopes().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        return new Result<>(loginRespDTO);

    }

    @CacheEvict(cacheNames = "Client", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addClient(AddClientReqDTO addClientReqDTO) {
        Client client = AutoMapUtils.map(addClientReqDTO, Client.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        client.setCreateUserId(currentLoginUser.getId());
        client.setCreateUserName(currentLoginUser.getUsername());

        client.setCreateTime(new Date());
        //判断唯一性
        String clientId = client.getClientId();
        GetClientByClientIdReqDTO getClientByClientIdReqDTO = new GetClientByClientIdReqDTO();
        getClientByClientIdReqDTO.setClientId(clientId);
        boolean b = this.existsClientByClientId(getClientByClientIdReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.CLIENT_EXISTS);
        }
        //初始化id
        client.setId(IdUtil.generateLongId());
        //密码脱敏处理
        client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        //客户端默认设置为启用
        client.setStatus(ClientStatusEnum.ENABLE.getCode());
        //新增到数据库
        this.add(client);
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Client", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> updateClientPassword(UpdateClientPasswordReqDTO updateClientPasswordReqDTO) {
        String oldPassword = updateClientPasswordReqDTO.getOldClientSecret();
        String newPassword = updateClientPasswordReqDTO.getNewClientSecret();
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        //获取登陆客户端id
        String clientId = oAuth2Authentication.getOAuth2Request().getClientId();

        Predicate predicate = QClient.client.clientId.eq(clientId);
        Client client = this.get(predicate);

        if (!passwordEncoder.matches(oldPassword, client.getClientSecret())) {
            return new Result<>(ResultCodeEnum.CLIENT_OLD_PASSWORD_ERROR);
        }
        //初始化
        client.setClientSecret(passwordEncoder.encode(newPassword));
        //更新到数据库
        this.update(client);
        return new Result<>(ResultCodeEnum.OK);

    }
}
