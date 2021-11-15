package com.ghf.exchange.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * @author jiangyuanlin@163.com
 */

@EnableSwagger2
@EnableKnife4j
@Configuration
public class SwaggerConfig {

    /**
     * 业务支撑系统
     *
     * @return
     */
    @Bean
    public Docket bossApi() {
        String basePackage = "com.ghf.exchange.boss";
        String groupName = "业务支撑系统";
        return intDocket(basePackage, groupName);
    }

    /**
     * OTC交易系统
     *
     * @return
     */
    @Bean
    public Docket otcApi() {
        String basePackage = "com.ghf.exchange.otc";
        String groupName = "OTC交易系统";
        return intDocket(basePackage, groupName);
    }

    private Docket intDocket(String basePackage, String groupName) {
        return new Docket(DocumentationType.SWAGGER_2)

                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(true)

                .apiInfo(apiInfo())

                .ignoredParameterTypes(WebRequest.class, File.class, InputStream.class, Resource.class, URI.class, URL.class, View.class, ModelAndView.class, Pageable.class,
                        org.springframework.security.oauth2.provider.OAuth2Authentication.class,
                        Principal.class, Sort.class)

                .select()

                //.apis(RequestHandlerSelectors.basePackage("com"))
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                // 指定路径处理，PathSelectors.any()表明不过滤任何路径
                .paths(PathSelectors.any())
                .build()
                //分组名称
                .groupName(groupName)

                .protocols(new LinkedHashSet<String>() {{
                    add("https");
                    add("http");
                }})
                .useDefaultResponseMessages(false)
                .consumes(new HashSet<String>() {{
                    add(MimeTypeUtils.APPLICATION_JSON_VALUE);
                }})
                .produces(new HashSet<String>() {{
                    add(MimeTypeUtils.APPLICATION_JSON_VALUE);
                }})
                .directModelSubstitute(java.sql.Timestamp.class, java.sql.Date.class)
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(Instant.class, String.class)
                //.genericModelSubstitutes(Result.class)
                .securitySchemes(Arrays.asList(new ApiKey("Authorization", "Authorization", "header"))
                )
                .securityContexts(securityContexts());
    }

    @ApiModel
    @Data
    static class Page {
        @ApiModelProperty(position = 0, value = "页码,第一页为0")
        private int page;

        @ApiModelProperty(position = 1, value = "每页的数据量")
        private int size;

        @ApiModelProperty(position = 2, value = "排序字段,可以是多个,格式为\"{属性}[,{方向}]\",如果只填写属性，方向默认为asc", example = "createTime,DESC")
        private List<String> sort;
    }

    private AlternateTypePropertyBuilder property(Class<?> type, String name) {
        return new AlternateTypePropertyBuilder().withName(name).withType(type).withCanRead(true).withCanWrite(true);
    }

    private List<SecurityContext> securityContexts() {

        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build()
        );

    }

    List<SecurityReference> defaultAuth() {

        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{new AuthorizationScope("admin", ""),
                new AuthorizationScope("user", ""), new AuthorizationScope("all", ""),

                new AuthorizationScope("global", "")};

        return new ArrayList<SecurityReference>() {
            {
                add(new SecurityReference("Authorization", authorizationScopes));
            }
        };
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("接口文档")
                .description("<br/><p>关于oauth2认证方式(参考<a href='https://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html'>阮一峰</a>博客实现):</p>"

                       /* + "<p>1.浏览器可使用oauth2的简化模式获取access_token,然后再追加请求头部Authorization，示例如下:</p>"
                        + "<p>session=$(curl  -i -X POST 'http://localhost:8080/login?username=admin&password=123456'    | grep Set-Cookie | awk -F ' ' '{print $2}') </p>"
                        + "<p>echo $session</p>"
                        + "<p>token=$(curl -i -X GET 'http://localhost:8080/oauth/authorize?client_id=admin&response_type=token&redirect_uri=http://www.baidu.com'   -H \"Cookie:$session\"   | grep Location | cut -d'=' -f2 | cut -d'&' -f1) </p>"
                        + "<p>echo $token</p>"
                        + "<p>curl -i -X POST 'http://localhost:8080/api/user/pageUser' -H \"Authorization:bearer $token\" </p>"

                        + "<br/><p>2.资源服务器,app客户端或者windows客户端可使用oauth2的密码方式获取access_token,然后再追加请求头部Authorization，示例如下:</p>"
                        + "<p>token=$(curl  -X POST 'http://admin:123456@localhost:8080/oauth/token?grant_type=password&username=admin&password=123456' | awk -F '\"' '{print $4}')</p>"
                        + "<p>echo $token</p>"
                        + "<p>curl -i -X POST 'http://localhost:8080/api/user/pageUser' -H \"Authorization:bearer $token\"</p>"

                        + "<br/><p>3.后台服务可使用oauth2的客户端模式获取access_token,然后再追加请求头部Authorization，示例如下:</p>"
                        + "<p>token=$(curl  -X POST 'http://exchange-api:123456@localhost:8080/oauth/token?grant_type=client_credentials' | grep access_token  | awk -F 'access_token' '{print $2}'  | awk -F '"' '{print $3}')</p>"
                        + "<p>echo $token</p>"
                        + "<p>curl -i -X POST 'http://localhost:8080/api/user/pageUser'   -d '{}'  -H \"Content-Type: application/json\"  -H \"Authorization:bearer $token\"</p>"


                        + "<br/><p>4.外部系统可使用oauth2的认证码模式获取access_token,然后再追加请求头部Authorization，示例如下:</p>"
                        + "<p>session=$(curl  -i -X POST 'http://localhost:8080/login?username=admin&password=123456'    | grep Set-Cookie | awk -F ' ' '{print $2}') </p>"
                        + "<p>echo $session</p>"

                        + "<p>code=$(curl -i -X GET 'http://localhost:8080/oauth/authorize?client_id=admin&response_type=code&redirect_uri=http://www.baidu.com'    -H \"Cookie:$session\"   | grep Location | cut -d'=' -f2 ) </p>"
                        + "<p>echo $code</p>"

                        + "<p>token=$(curl -i -X GET \"http://localhost:8080/oauth/token?grant_type=authorization_code&client_id=admin&client_secret=123456&scope=USER&redirect_uri=http://www.baidu.com&code=$code\"  | grep access_token | awk -F '\"' '{print $4}') </p>"
                        + "<p>echo $token</p>"
                        + "<p>curl -i -X POST 'http://localhost:8080/api/user/pageUser' -H \"Authorization:bearer $token\" </p>"
                        */

                        + "<p>token=$(curl -i -X POST 'http://localhost:8080/api/user/login' -d '{\"username\":\"admin\",\"password\":\"123456\"}' -H \"Content-Type: application/json\" | grep accessToken | awk -F 'accessToken' '{print $2}' | awk -F '\"' '{print $3}')</p>"
                        + "<p>echo $token</p>"
                        + "<p>curl -i -X POST 'http://localhost:8080/api/user/pageUser' -d '{}' -H \"Content-Type: application/json\" -H \"Authorization:bearer $token\" </p>"

                        + "<br/>也可以在api文档中点击【用户管理】->【登录】->输入账号密码->获取token,然后在【Authorization】填写'bearer $token'</p>"
                        + "<br/><p>日期类型:</p><p>统一使用yyyy-MM-dd HH:mm:ss格式，不足部分补上0</p>"

                        + "<br/><p>输出响应:</p><p>数据部分列表字段为content,总记录数字段为totalElements，例如{\"code\":200,\"data\":{\"content\":[{\"id\":\"1\",\"name\":\"admin\"}],\"totalElements\":1}}</p>"
                        + "<br/><p>错误码:</p><p>默认情况code=200代表业务执行正常，code=400代表业务执行错误，错误时msg字段必须填写错误原因.有时候调用方需要具体的错误码方便业务处理，建议把已知错误原因时的code字段填写-1,-2,-3,-4等数字，msg字段填写错误原因，举例:{\"code\":200,\"msg\":\"业务执行正常\"},{\"code\":-1,\"msg\":\"编号不能为空\"},{\"code\":-2,\"msg\":\"账号已经存在\"},{\"code\":400,\"msg\":\"未知错误，请联系管理员\"}</p>"

                        + "<br/><p>请求内容格式:</p><p>常用的请求类型application/json，multipart/form-data,application/x-www-form-urlencoded,application/octet-stream，甚至有些时候会把参数作为url路径的一部分，完全rest化，这些格式都可能使用，不做强制要求</p>"

                        + "<br/><p>返回内容格式:</p><p>除了下载导出等使用application/octet-stream，其他文本型接口尽可能使用application/json，避免使用xml等格式</p>"

                        + "<br/><p>常用示例，chrome通过f12打开控制台，把以下链接粘贴到地址栏，回车查看源码</p>"
                        + "<br/><p>http://localhost:8080/POST形式调用登陆接口示例.html</p>"
                        + "<br/><p>http://localhost:8080/POST形式调用普通接口示例.html</p>"
                        + "<br/><p>http://localhost:8080/POST形式上传文件示例.html</p>"
                        + "<br/><p>http://localhost:8080/POST形式下载文件示例.html</p>"
                        + "<br/><p>http://localhost:8080/POST形式调用验证码接口示例.html</p>"

                )
                .version("1.0.0")
                .build();
    }

}
