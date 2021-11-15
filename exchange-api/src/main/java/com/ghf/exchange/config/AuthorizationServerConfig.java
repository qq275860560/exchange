package com.ghf.exchange.config;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private ClientService clientService;

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    public JwtAccessTokenConverter jwtAccessTokenConverter;
    @Resource
    public JwtTokenStore jwtTokenStore;

    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.withClientDetails(clientId -> {
            log.debug("登录或认证:获取客户端对应的SCOPE");
            return clientService.getClientDetailsByClientId(clientId);
        });
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        endpoints.reuseRefreshTokens(true);
        endpoints.userDetailsService(userDetailsService);
        endpoints.authenticationManager(authenticationManager);
        endpoints.accessTokenConverter(jwtAccessTokenConverter);
        endpoints.tokenStore(jwtTokenStore);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        /* /oauth/confirm_access中有client_id和client_secret的会走ClientCredentialsTokenEndpointFilter */
        oauthServer.allowFormAuthenticationForClients();
        /* url:/oauth/token_key,exposes public key for token verification if  using JWT tokens */
        oauthServer.tokenKeyAccess("permitAll()");
        /* oauthServer.checkTokenAccess("isAuthenticated()");   url:/oauth/check_token */
        /* allow check token,访问tokenkey时需要经过认证*/
        oauthServer.checkTokenAccess("permitAll()");
    }

}
