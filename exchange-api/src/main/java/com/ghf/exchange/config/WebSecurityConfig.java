package com.ghf.exchange.config;

import com.ghf.exchange.boss.authorication.user.entity.QUser;
import com.ghf.exchange.boss.authorication.user.entity.User;
import com.ghf.exchange.boss.authorication.user.enums.UserStatusEnum;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.orgrole.dto.ListRoleByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.userorg.dto.ListOrgByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.dto.ListRoleByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@RestController
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @SneakyThrows
    public void configure(WebSecurity web) {
        // 不需要认证不需要授权
        web.ignoring().antMatchers("/**/*.js", "/**/*.css", "/**/*.html", "/**/*.jpg");
    }

    @Bean
    @Lazy
    @Override
    @SneakyThrows
    public AuthenticationManager authenticationManagerBean() {
        return super.authenticationManagerBean();
    }

    @Override
    @SneakyThrows
    protected void configure(HttpSecurity http) {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.cors().disable();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.formLogin().loginPage("/loginPage.html").loginProcessingUrl("/login").and().logout().logoutSuccessHandler(
                (request, response, authentication) -> {
                    String callback = request.getParameter("callback");
                    if (callback == null) {
                        callback = request.getHeader("Referer");
                    }
                    if (callback == null) {
                        callback = "/loginPage.html";
                    }
                    response.sendRedirect(callback);
                }
        );

        http.authorizeRequests().antMatchers("/api/user/login", "/oauth/**").authenticated()
                .anyRequest().permitAll();

    }

    @Bean
    @Lazy
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserOrgService userOrgService;

    @Resource
    private OrgRoleService orgRoleService;

    @Override
    @Bean
    @Lazy
    public UserDetailsService userDetailsService() {
        return username -> {
            log.debug("登录或认证:获取用户对应的角色权限");

            Predicate predicate = QUser.user.username.eq(username);
            User user = userService.get(predicate);
            String password = user.getPassword();

            // 帐号是否可用
            boolean enabled = user.getStatus() == UserStatusEnum.ENABLE.getCode();
            // 帐户是否过期
            boolean accountNonExpired = true;
            // 帐户密码是否过期，一般有的密码要求性高的系统会使用到，比较每隔一段时间就要求用户重置密码
            boolean credentialsNonExpired = true;
            // 帐户是否被冻结
            boolean accountNonLocked = true;

            //获取角色
            ListRoleByUsernameReqDTO listRoleByUsernameReqDTO = new ListRoleByUsernameReqDTO();
            listRoleByUsernameReqDTO.setUsername(username);
            Set<String> rolenameSet1 = userRoleService.listRoleByUsername(listRoleByUsernameReqDTO).getData().stream()
                    .filter(e -> e != null)
                    .map(e -> e.getRolename())
                    .collect(Collectors.toSet());

            ListOrgByUsernameReqDTO listOrgByUsernameReqDTO = new ListOrgByUsernameReqDTO();
            listOrgByUsernameReqDTO.setUsername(username);
            List<OrgRespDTO> orgRespDTOList = userOrgService.listOrgByUsername(listOrgByUsernameReqDTO).getData();

            Set<String> rolenameSet2 = orgRespDTOList.stream().map(e -> {
                String orgname = e.getOrgname();
                ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
                listRoleByOrgnameReqDTO.setOrgname(orgname);
                return orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData();
            }).flatMap(Collection::stream).map(e -> e.getRolename()).collect(Collectors.toSet());

            rolenameSet1.addAll(rolenameSet2);

            String rolenames = rolenameSet1.stream().collect(Collectors.joining(","));

            // 初始化用户的权限
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(rolenames);
            // controller方法参数通过@AuthenticationPrincipal可以获得该对象
            return new org.springframework.security.core.userdetails.User(username, password, enabled, accountNonExpired,
                    credentialsNonExpired, accountNonLocked, grantedAuthorities);

        };
    }

    @Resource
    private Environment environment;

    @Bean
    @Lazy
    @SneakyThrows
    public KeyPair getKeyPair() {
        return new KeyPair(
                KeyFactory.getInstance("RSA")
                        .generatePublic(new X509EncodedKeySpec(
                                Base64Utils.decode(environment.getProperty("publicKeyBase64EncodeString").getBytes()))),
                KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(
                        Base64Utils.decode(environment.getProperty("privateKeyBase64EncodeString").getBytes()))));
    }

    @Bean
    @Lazy
    public JwtAccessTokenConverter jwtAccessTokenConverter(KeyPair keyPair) {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair);

        DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new DefaultUserAuthenticationConverter() {
            @Override
            public Map<String, ?> convertUserAuthentication(Authentication authentication) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("sub", authentication.getName());
                response.put("user_name", authentication.getName());
                if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                    response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
                }
                return response;
            }
        };
        defaultUserAuthenticationConverter.setUserDetailsService(userDetailsService());
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(defaultUserAuthenticationConverter);
        jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConverter);

        return jwtAccessTokenConverter;
    }

    @Bean
    @Lazy
    public JwtTokenStore jwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

}

/*
    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    @SneakyThrows
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) getKeyPair().getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

    @Resource
    private JwtTokenStore tokenStore;

    @PostMapping("/introspect")
    @ResponseBody
    public Map<String, Object> introspect(@RequestParam("token") String token) {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(token);
        Map<String, Object> attributes = new HashMap<>(16, 0.75f);
        if (accessToken == null || accessToken.isExpired()) {
            attributes.put("active", false);
            return attributes;
        }

        OAuth2Authentication authentication = this.tokenStore.readAuthentication(token);

        attributes.put("active", true);
        attributes.put("exp", accessToken.getExpiration().getTime());
        attributes.put("scope", accessToken.getScope().stream().collect(Collectors.joining(" ")));
        attributes.put("sub", authentication.getName());
        attributes.put("user_name", authentication.getName());

        return attributes;
    }
*/