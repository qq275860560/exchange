
package com.ghf.exchange.boss.authorization.userrole.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.userrole.dto.GetUserRoleByUsernameAndRolenameReqDTO;
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
public class UserRoleServiceTest {
    @Lazy
    @Resource
    private UserRoleService userRoleService;

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
    public void enableUserRole() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        userRoleService.list(new BooleanBuilder()).stream().forEach(e -> {
            GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO = new GetUserRoleByUsernameAndRolenameReqDTO();
            getUserRoleByUsernameAndRolenameReqDTO.setUsername(e.getUsername());
            getUserRoleByUsernameAndRolenameReqDTO.setRolename(e.getRolename());
            userRoleService.enableUserRole(getUserRoleByUsernameAndRolenameReqDTO);
        });
        TimeUnit.SECONDS.sleep(720);
        Assert.assertTrue(1 == 1);

    }

}
