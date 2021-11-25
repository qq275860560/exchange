package com.ghf.exchange.config;

import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.util.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    @SneakyThrows
    public void configure(HttpSecurity http) {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors().disable();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.formLogin();

        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            Result<Void> result = new Result<>(ResultCodeEnum.UNAUTHORIZED);
            ResponseUtil.send(response, result);
        }).accessDeniedHandler((request, response, accessDeniedException) -> {
            Result<Void> result = new Result<>(ResultCodeEnum.FORBIDDEN);
            ResponseUtil.send(response, result);
        });

        http.requestMatchers().antMatchers("/api/**").and().authorizeRequests()
                //直接放行
                .antMatchers("/api/user/login", "/api/validateCode/generateValidateCode", "/api/validateCode/checkValidateCode").permitAll()
                // 需要认证需要客户端范围需要用户角色
                .antMatchers("/api/**", "/websocket/**").access("@rolePermissionService.decideRole(request,authentication)");
    }

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler() {
        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

    @Override
    @SneakyThrows
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.expressionHandler(oAuth2WebSecurityExpressionHandler());
        resources.authenticationEntryPoint((request, response, authException) -> {
            Result<Void> result = new Result<>(ResultCodeEnum.UNAUTHORIZED);
            ResponseUtil.send(response, result);
        }).accessDeniedHandler((request, response, accessDeniedException) -> {
            Result<Void> result = new Result<>(ResultCodeEnum.FORBIDDEN);
            ResponseUtil.send(response, result);
        });

    }

}
