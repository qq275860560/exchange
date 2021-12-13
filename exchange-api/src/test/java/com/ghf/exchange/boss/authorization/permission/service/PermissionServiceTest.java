
package com.ghf.exchange.boss.authorization.permission.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.permission.dto.*;
import com.ghf.exchange.boss.authorization.permission.entity.QPermission;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class PermissionServiceTest {
    @Lazy
    @Resource
    private PermissionService permissionService;

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

    @SneakyThrows
    @Test
    public void addPermission6() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddPermissionReqDTO addPermissionReqDTO = new AddPermissionReqDTO();
        addPermissionReqDTO.setPermissionname("test-addPermission-permissionname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO.setPermissiondesc("test-addPermission-permissiondesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        permissionService.addPermission(addPermissionReqDTO);
        TimeUnit.SECONDS.sleep(3);

        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO.setPermissionname(addPermissionReqDTO.getPermissionname());
        PermissionRespDTO targeOutput = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        Assert.assertTrue(targeOutput.getFullPermissionname().equals("," + addPermissionReqDTO.getPermissionname() + ","));

        AddPermissionReqDTO addPermissionReqDTO2 = new AddPermissionReqDTO();
        addPermissionReqDTO2.setPermissionname("test-addPermission-permissionname2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO2.setPermissiondesc("test-addPermission-permissiondesc2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO2.setParentPermissionname(addPermissionReqDTO.getPermissionname());
        permissionService.addPermission(addPermissionReqDTO2);
        TimeUnit.SECONDS.sleep(3);

        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO2 = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO2.setPermissionname(addPermissionReqDTO2.getPermissionname());
        PermissionRespDTO targeOutput2 = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO2).getData();
        Assert.assertTrue(targeOutput2.getFullPermissionname().equals("," + addPermissionReqDTO.getPermissionname() + "," + addPermissionReqDTO2.getPermissionname() + ","));

        AddPermissionReqDTO addPermissionReqDTO3 = new AddPermissionReqDTO();
        addPermissionReqDTO3.setPermissionname("test-addPermission-permissionname3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO3.setPermissiondesc("test-addPermission-permissiondesc3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO3.setParentPermissionname(addPermissionReqDTO2.getPermissionname());
        permissionService.addPermission(addPermissionReqDTO3);
        TimeUnit.SECONDS.sleep(3);

        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO3 = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO3.setPermissionname(addPermissionReqDTO3.getPermissionname());
        PermissionRespDTO targeOutput3 = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO3).getData();
        log.info("targeOutput3.getFullPermissionname()={}", targeOutput3.getFullPermissionname());
        Assert.assertTrue(targeOutput3.getFullPermissionname().equals("," + addPermissionReqDTO.getPermissionname() + "," + addPermissionReqDTO2.getPermissionname() + "," + addPermissionReqDTO3.getPermissionname() + ","));

        permissionService.delete(QPermission.permission.permissionname.eq(addPermissionReqDTO.getPermissionname()));
        permissionService.delete(QPermission.permission.permissionname.eq(addPermissionReqDTO2.getPermissionname()));
        permissionService.delete(QPermission.permission.permissionname.eq(addPermissionReqDTO3.getPermissionname()));
    }

    @SneakyThrows
    @Test
    public void updatePermission6() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddPermissionReqDTO addPermissionReqDTO = new AddPermissionReqDTO();
        addPermissionReqDTO.setPermissionname("test-addPermission-permissionname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO.setPermissiondesc("test-addPermission-permissiondesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        permissionService.addPermission(addPermissionReqDTO);
        TimeUnit.SECONDS.sleep(3);

        AddPermissionReqDTO addPermissionReqDTO2 = new AddPermissionReqDTO();
        addPermissionReqDTO2.setPermissionname("test-addPermission-permissionname2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO2.setPermissiondesc("test-addPermission-permissiondesc2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO2.setParentPermissionname(addPermissionReqDTO.getPermissionname());
        permissionService.addPermission(addPermissionReqDTO2);
        TimeUnit.SECONDS.sleep(3);

        AddPermissionReqDTO addPermissionReqDTO3 = new AddPermissionReqDTO();
        addPermissionReqDTO3.setPermissionname("test-addPermission-permissionname3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO3.setPermissiondesc("test-addPermission-permissiondesc3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO3.setParentPermissionname(addPermissionReqDTO2.getPermissionname());
        permissionService.addPermission(addPermissionReqDTO3);
        TimeUnit.SECONDS.sleep(3);

        UpdatePermissionByPermissionnameReqDTO updatePermissionByPermissionnameReqDTO = new UpdatePermissionByPermissionnameReqDTO();
        updatePermissionByPermissionnameReqDTO.setPermissionname(addPermissionReqDTO.getPermissionname());
        updatePermissionByPermissionnameReqDTO.setPermissiondesc("test-updatePermission-permissiondesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        permissionService.updatePermissionByPermissionname(updatePermissionByPermissionnameReqDTO);
        TimeUnit.SECONDS.sleep(3);

        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO.setPermissionname(addPermissionReqDTO.getPermissionname());
        PermissionRespDTO targeOutput = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        log.info("targeOutput.getFullPermissiondesc()={}", targeOutput.getFullPermissiondesc());
        Assert.assertTrue(targeOutput.getFullPermissiondesc().equals("," + updatePermissionByPermissionnameReqDTO.getPermissiondesc() + ","));

        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO2 = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO2.setPermissionname(addPermissionReqDTO2.getPermissionname());
        PermissionRespDTO targeOutput2 = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO2).getData();
        log.info("targeOutput2.getFullPermissiondesc()={}", targeOutput2.getFullPermissiondesc());
        Assert.assertTrue(targeOutput2.getFullPermissiondesc().equals("," + updatePermissionByPermissionnameReqDTO.getPermissiondesc() + "," + addPermissionReqDTO2.getPermissiondesc() + ","));

        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO3 = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO3.setPermissionname(addPermissionReqDTO3.getPermissionname());
        PermissionRespDTO targeOutput3 = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO3).getData();
        log.info("targeOutput3.getFullPermissiondesc()={}", targeOutput3.getFullPermissiondesc());
        Assert.assertTrue(targeOutput3.getFullPermissiondesc().equals("," + updatePermissionByPermissionnameReqDTO.getPermissiondesc() + "," + addPermissionReqDTO2.getPermissiondesc() + "," + addPermissionReqDTO3.getPermissiondesc() + ","));

        permissionService.delete(QPermission.permission.permissionname.eq(addPermissionReqDTO.getPermissionname()));
        permissionService.delete(QPermission.permission.permissionname.eq(addPermissionReqDTO2.getPermissionname()));
        permissionService.delete(QPermission.permission.permissionname.eq(addPermissionReqDTO3.getPermissionname()));
    }

    @SneakyThrows
    //@Test
    public void batchUpdatePermission() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        String permissionname = "PERMISSION_WSGSL";
        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO.setPermissionname(permissionname);
        PermissionRespDTO permissionOutput = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();

        UpdatePermissionByPermissionnameReqDTO updatePermissionByPermissionnameReqDTO = ModelMapperUtil.map(permissionOutput, UpdatePermissionByPermissionnameReqDTO.class);
        permissionService.updatePermissionByPermissionname(updatePermissionByPermissionnameReqDTO);
        TimeUnit.SECONDS.sleep(720);

        Assert.assertTrue(1 == 1);

    }

    @SneakyThrows
    @Test
    public void treePermission() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddPermissionReqDTO addPermissionReqDTO = new AddPermissionReqDTO();
        addPermissionReqDTO.setPermissionname("test-addPermission-permissionname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addPermissionReqDTO.setPermissiondesc("test-addPermission-permissiondesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        permissionService.addPermission(addPermissionReqDTO);

        String permissionname = "";
        TreePermissionReqDTO treePermissionReqDTO = new TreePermissionReqDTO();
        treePermissionReqDTO.setPermissionname(permissionname);
        PermissionRespDTO permissionOutput = permissionService.treePermission(treePermissionReqDTO).getData();

        Assert.assertTrue(!ObjectUtils.isEmpty(permissionOutput.getPermissionname()));

    }

}
