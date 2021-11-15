
package com.ghf.exchange.boss.authorization.role.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.permission.dto.PermissionRespDTO;
import com.ghf.exchange.boss.authorization.role.dto.*;
import com.ghf.exchange.boss.authorization.role.entity.QRole;
import com.ghf.exchange.boss.authorization.role.entity.Role;
import com.ghf.exchange.boss.authorization.rolepermission.dto.ListPermissionByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.rolepermission.entity.QRolePermission;
import com.ghf.exchange.boss.authorization.rolepermission.service.RolePermissionService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.util.AutoMapUtils;
import com.querydsl.core.BooleanBuilder;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class RoleServiceTest {
    @Lazy
    @Resource
    private RoleService roleService;

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

    @Lazy
    @Resource
    private RolePermissionService rolePermissionService;

    @SneakyThrows
    @Test
    public void addRole() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-addRole-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        addRoleReqDTO.setRemark("测试remark");
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String rolename = addRoleReqDTO.getRolename();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRole.role.rolename.eq(rolename));
        Role role = roleService.get(predicate);
        if (ObjectUtils.isEmpty(role.getPermissionnames())) {
            role.setPermissionnames(",");
            role.setPermissiondescs(",");
        }

        Set<String> permissionnameSet2 = Arrays.asList(role.getPermissionnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());
        Set<String> permissiondescSet2 = Arrays.asList(role.getPermissiondescs().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        RoleRespDTO roleOutput = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();

        Set<String> permissionnameSet = roleOutput.getPermissionnameSet();
        Set<String> permissiondescSet = roleOutput.getPermissiondescSet();

        Assert.assertTrue(isSetEqual(permissionnameSet, permissionnameSet2));
        Assert.assertTrue(isSetEqual(permissiondescSet, permissiondescSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void addRole2() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-addRole2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        addRoleReqDTO.setRemark("测试remark");
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(3);

        RoleRespDTO roleOutput = roleService.getRoleByRolename(GetRoleByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        Set<String> permissionnameSet2 = new HashSet<>();
        Set<String> permissiondescSet2 = new HashSet<>();
        List<PermissionRespDTO> list = rolePermissionService.listPermissionByRolename(ListPermissionByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        list.forEach(e -> {
            permissionnameSet2.add(e.getPermissionname());
            permissiondescSet2.add(e.getPermissiondesc());
        });

        Assert.assertTrue(isSetEqual(roleOutput.getPermissionnameSet(), permissionnameSet2));
        Assert.assertTrue(isSetEqual(roleOutput.getPermissiondescSet(), permissiondescSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(addRoleReqDTO.getRolename()));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    public static boolean isSetEqual(Set set1, Set set2) {

        if (set1 == null && set2 == null) {
            return true; // Both are null
        }

        if (ObjectUtils.isEmpty(set1) && ObjectUtils.isEmpty(set2)) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()
                || set1.size() == 0 || set2.size() == 0) {
            return false;
        }

        Iterator ite1 = set1.iterator();
        Iterator ite2 = set2.iterator();

        boolean isFullEqual = true;

        while (ite2.hasNext()) {
            if (!set1.contains(ite2.next())) {
                isFullEqual = false;
            }
        }

        return isFullEqual;
    }

    @SneakyThrows
    @Test
    public void updateRole() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-updateRole-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String rolename = addRoleReqDTO.getRolename();

        UpdateRoleByRolenameReqDTO updateRoleReqDTO = AutoMapUtils.map(roleService.get(QRole.role.rolename.eq(rolename)), UpdateRoleByRolenameReqDTO.class);
        updateRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
        }});

        roleService.updateRoleByRolename(updateRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRole.role.rolename.eq(rolename));
        Role role = roleService.get(predicate);
        if (ObjectUtils.isEmpty(role.getPermissionnames())) {
            role.setPermissionnames(",");
        }

        Set<String> permissionnameSet2 = Arrays.asList(role.getPermissionnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        RoleRespDTO roleOutput = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();

        Set<String> permissionnameSet = roleOutput.getPermissionnameSet();

        Assert.assertTrue(isSetEqual(permissionnameSet, permissionnameSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void updateRole11() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-updateRole11-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String rolename = addRoleReqDTO.getRolename();

        UpdateRoleByRolenameReqDTO updateRoleReqDTO = AutoMapUtils.map(roleService.get(QRole.role.rolename.eq(rolename)), UpdateRoleByRolenameReqDTO.class);
        updateRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");

        }});

        roleService.updateRoleByRolename(updateRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        RoleRespDTO roleOutput = roleService.getRoleByRolename(GetRoleByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        Set<String> permissionnameSet2 = new HashSet<>();
        Set<String> permissiondescSet2 = new HashSet<>();
        List<PermissionRespDTO> list = rolePermissionService.listPermissionByRolename(ListPermissionByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        list.forEach(e -> {
            permissionnameSet2.add(e.getPermissionname());
            permissiondescSet2.add(e.getPermissiondesc());
        });

        Assert.assertTrue(isSetEqual(roleOutput.getPermissionnameSet(), permissionnameSet2));
        Assert.assertTrue(isSetEqual(roleOutput.getPermissiondescSet(), permissiondescSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void updateRole2() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-updateRole31-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String rolename = addRoleReqDTO.getRolename();

        UpdateRoleByRolenameReqDTO updateRoleReqDTO = AutoMapUtils.map(roleService.get(QRole.role.rolename.eq(rolename)), UpdateRoleByRolenameReqDTO.class);
        updateRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});

        roleService.updateRoleByRolename(updateRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRole.role.rolename.eq(rolename));
        Role role = roleService.get(predicate);
        if (ObjectUtils.isEmpty(role.getPermissionnames())) {
            role.setPermissionnames(",");
        }

        Set<String> permissionnameSet2 = Arrays.asList(role.getPermissionnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        RoleRespDTO roleOutput = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();

        Set<String> permissionnameSet = roleOutput.getPermissionnameSet();

        Assert.assertTrue(isSetEqual(permissionnameSet, permissionnameSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void updateRole3() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-updateRole3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
        }});
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String rolename = addRoleReqDTO.getRolename();

        UpdateRoleByRolenameReqDTO updateRoleReqDTO = AutoMapUtils.map(roleService.get(QRole.role.rolename.eq(rolename)), UpdateRoleByRolenameReqDTO.class);
        updateRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});

        roleService.updateRoleByRolename(updateRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRole.role.rolename.eq(rolename));
        Role role = roleService.get(predicate);
        if (ObjectUtils.isEmpty(role.getPermissionnames())) {
            role.setPermissionnames(",");
        }

        Set<String> permissionnameSet2 = Arrays.asList(role.getPermissionnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        RoleRespDTO roleOutput = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();

        Set<String> permissionnameSet = roleOutput.getPermissionnameSet();

        Assert.assertTrue(isSetEqual(permissionnameSet, permissionnameSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void updateRole31() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-updateRole3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
        }});
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String rolename = addRoleReqDTO.getRolename();

        UpdateRoleByRolenameReqDTO updateRoleReqDTO = AutoMapUtils.map(roleService.get(QRole.role.rolename.eq(rolename)), UpdateRoleByRolenameReqDTO.class);
        updateRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});

        roleService.updateRoleByRolename(updateRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        RoleRespDTO roleOutput = roleService.getRoleByRolename(GetRoleByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        Set<String> permissionnameSet2 = new HashSet<>();
        Set<String> permissiondescSet2 = new HashSet<>();
        List<PermissionRespDTO> list = rolePermissionService.listPermissionByRolename(ListPermissionByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        list.forEach(e -> {
            permissionnameSet2.add(e.getPermissionname());
            permissiondescSet2.add(e.getPermissiondesc());
        });

        Assert.assertTrue(isSetEqual(roleOutput.getPermissionnameSet(), permissionnameSet2));
        Assert.assertTrue(isSetEqual(roleOutput.getPermissiondescSet(), permissiondescSet2));

        //清理
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void pageRole() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-pageRole-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        roleService.addRole(addRoleReqDTO);

        PageRoleReqDTO pageRoleReqDTO = new PageRoleReqDTO();
        Result<PageRespDTO<RoleRespDTO>> result = roleService.pageRole(pageRoleReqDTO);

        Assert.assertTrue(result.getData().getTotal() > 0);

        //清理
        String rolename = addRoleReqDTO.getRolename();
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void listRole() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-listRole-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        roleService.addRole(addRoleReqDTO);

        ListRoleReqDTO listRoleReqDTO = new ListRoleReqDTO();
        Result<List<RoleRespDTO>> result = roleService.listRole(listRoleReqDTO);

        Assert.assertTrue(result.getData().size() > 0);

        //清理
        String rolename = addRoleReqDTO.getRolename();
        roleService.delete(QRole.role.rolename.eq(rolename));

        rolePermissionService.getJpaQueryFactory().delete(QRolePermission.rolePermission).where(QRolePermission.rolePermission.rolename.eq(addRoleReqDTO.getRolename()));

    }

    @SneakyThrows
    @Test
    public void addRolePermission() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddRoleReqDTO addRoleReqDTO = new AddRoleReqDTO();
        addRoleReqDTO.setRolename("test-addRolePermission-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        addRoleReqDTO.setPermissionnameSet(new HashSet<String>() {{
            add("PERMISSION_FYSSYHAN");
            add("PERMISSION_YWZCXT");
        }});
        roleService.addRole(addRoleReqDTO);

        TimeUnit.SECONDS.sleep(20);

        RoleRespDTO roleOutput = roleService.getRoleByRolename(GetRoleByRolenameReqDTO.builder().rolename(addRoleReqDTO.getRolename()).build()).getData();

        //另一种方式获取
        Set<String> permissionnameSet = new HashSet<>(16, 0.75f);
        Set<String> permissionndescSet = new HashSet<>(16, 0.75f);

        ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO = new ListPermissionByRolenameReqDTO();
        listPermissionByRolenameReqDTO.setRolename(addRoleReqDTO.getRolename());
        rolePermissionService.listPermissionByRolename(listPermissionByRolenameReqDTO).getData().stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .forEach(e -> {
                    permissionnameSet.add(e.getPermissionname());
                    permissionndescSet.add(e.getPermissiondesc());
                });

        Assert.assertTrue(isSetEqual(permissionnameSet, roleOutput.getPermissionnameSet()));
        Assert.assertTrue(isSetEqual(permissionndescSet, roleOutput.getPermissiondescSet()));

        //清理
        String rolename = addRoleReqDTO.getRolename();
        roleService.delete(QRole.role.rolename.eq(rolename));

    }
}
