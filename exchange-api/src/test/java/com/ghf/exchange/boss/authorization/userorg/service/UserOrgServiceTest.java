
package com.ghf.exchange.boss.authorization.userorg.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.userorg.dto.GetUserOrgByUsernameAndOrgnameReqDTO;
import com.querydsl.core.BooleanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class UserOrgServiceTest {
    @Lazy
    @Resource
    private UserOrgService userOrgService;
    @Lazy
    @Resource
    private UserService userService;

    /**
     * 平台管理员账号（测试）
     */
    public static final String ADMIN_USER_NAME = "admin";

    /**
     * 平台管理员密码（测试）
     */
    public static final String ADMIN_PASSWORD = "123456";

    @Test
    public void test() {

    }

    @SneakyThrows
    //@Test
    public void enableUserOrg() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        userOrgService.list(new BooleanBuilder()).stream().forEach(e -> {
            GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO = new GetUserOrgByUsernameAndOrgnameReqDTO();
            getUserOrgByUsernameAndOrgnameReqDTO.setUsername(e.getUsername());
            getUserOrgByUsernameAndOrgnameReqDTO.setOrgname(e.getOrgname());
            userOrgService.enableUserOrg(getUserOrgByUsernameAndOrgnameReqDTO);
        });
        TimeUnit.SECONDS.sleep(720);
        Assert.assertTrue(1 == 1);

    }

}
