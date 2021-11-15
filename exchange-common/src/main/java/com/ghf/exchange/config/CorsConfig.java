package com.ghf.exchange.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jiangyuanlin@163.com
 */
@Lazy
@Configuration
@Slf4j
public class CorsConfig {

    @Lazy
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        Filter filter = (ServletRequest req, ServletResponse res, FilterChain chain) ->
        {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "*,Content-Type,token,Authorization");
            response.setHeader("Access-Control-Expose-Headers",
                    "Content-Disposition,downloadFileName,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
                response.setStatus(HttpStatus.OK.value());
                return;
            }

            chain.doFilter(request, response);
        };

        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}