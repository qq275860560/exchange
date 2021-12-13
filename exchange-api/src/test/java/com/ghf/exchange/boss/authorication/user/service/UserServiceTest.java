
package com.ghf.exchange.boss.authorication.user.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.*;
import com.ghf.exchange.boss.authorication.user.entity.QUser;
import com.ghf.exchange.boss.authorication.user.entity.User;
import com.ghf.exchange.boss.authorization.userorg.entity.QUserOrg;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.entity.QUserRole;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
public class UserServiceTest {
    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private UserRoleService userRoleService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

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
    public void addUser() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-addUser-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String username = addUserReqDTO.getUsername();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUser.user.username.eq(username));
        User user = userService.get(predicate);
        if (ObjectUtils.isEmpty(user.getOrgnames())) {
            user.setOrgnames(",");
            user.setOrgdescs(",");
        }
        if (ObjectUtils.isEmpty(user.getRolenames())) {
            user.setRolenames(",");
            user.setRoledescs(",");
        }

        Set<String> rolenameSet2 = Arrays.asList(user.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());
        Set<String> roledescSet2 = Arrays.asList(user.getRoledescs().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();

        Set<String> rolenameSet = userOutput.getRolenameSet();
        Set<String> roledescSet = userOutput.getRoledescSet();

        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));
        Assert.assertTrue(isSetEqual(roledescSet, roledescSet2));

        //清理
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    @Test
    public void addUser2() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-addUser2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
        }});
        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        TimeUnit.SECONDS.sleep(20);

        String username = addUserReqDTO.getUsername();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUser.user.username.eq(username));
        User user = userService.get(predicate);
        if (ObjectUtils.isEmpty(user.getOrgnames())) {
            user.setOrgnames(",");
            user.setOrgdescs(",");
        }
        if (ObjectUtils.isEmpty(user.getRolenames())) {
            user.setRolenames(",");
            user.setRoledescs(",");
        }
        Set<String> orgnameSet2 = Arrays.asList(user.getOrgnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());
        Set<String> orgdescSet2 = Arrays.asList(user.getOrgdescs().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        Set<String> rolenameSet2 = Arrays.asList(user.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());
        Set<String> roledescSet2 = Arrays.asList(user.getRoledescs().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        Set<String> orgnameSet = userOutput.getOrgnameSet();
        Set<String> orgdescSet = userOutput.getOrgdescSet();
        Set<String> rolenameSet = userOutput.getRolenameSet();
        Set<String> roledescSet = userOutput.getRoledescSet();

        Assert.assertTrue(isSetEqual(orgnameSet, orgnameSet2));
        Assert.assertTrue(isSetEqual(orgdescSet, orgdescSet2));
        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));
        Assert.assertTrue(isSetEqual(roledescSet, roledescSet2));

        //清理
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

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
    public void updateUserByUsername() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-updateUserByUsername-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
        }});
        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String username = addUserReqDTO.getUsername();

        UpdateUserByUsernameReqDTO updateUserByUsernameReqDTO = ModelMapperUtil.map(userService.get(QUser.user.username.eq(username)), UpdateUserByUsernameReqDTO.class);
        updateUserByUsernameReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
            add("ORG_DFGSL");
        }});
        updateUserByUsernameReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
            add("ROLE_STUDENT");
        }});
        userService.updateUserByUsername(updateUserByUsernameReqDTO);

        TimeUnit.SECONDS.sleep(3);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUser.user.username.eq(username));
        User user = userService.get(predicate);
        if (ObjectUtils.isEmpty(user.getOrgnames())) {
            user.setOrgnames(",");
        }
        if (ObjectUtils.isEmpty(user.getRolenames())) {
            user.setRolenames(",");
        }
        Set<String> orgnameSet2 = Arrays.asList(user.getOrgnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        Set<String> rolenameSet2 = Arrays.asList(user.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        Set<String> orgnameSet = userOutput.getOrgnameSet();
        Set<String> rolenameSet = userOutput.getRolenameSet();

        Assert.assertTrue(isSetEqual(orgnameSet, orgnameSet2));
        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    @Test
    public void updateUserByUsername2() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-updateUserByUsername2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
        }});
        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String username = addUserReqDTO.getUsername();

        UpdateUserByUsernameReqDTO updateUserByUsernameReqDTO = ModelMapperUtil.map(userService.get(QUser.user.username.eq(username)), UpdateUserByUsernameReqDTO.class);
        updateUserByUsernameReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
        }});
        updateUserByUsernameReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.updateUserByUsername(updateUserByUsernameReqDTO);

        TimeUnit.SECONDS.sleep(3);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUser.user.username.eq(username));
        User user = userService.get(predicate);
        if (ObjectUtils.isEmpty(user.getOrgnames())) {
            user.setOrgnames(",");
        }
        if (ObjectUtils.isEmpty(user.getRolenames())) {
            user.setRolenames(",");
        }
        Set<String> orgnameSet2 = Arrays.asList(user.getOrgnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        Set<String> rolenameSet2 = Arrays.asList(user.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        Set<String> orgnameSet = userOutput.getOrgnameSet();
        Set<String> rolenameSet = userOutput.getRolenameSet();

        Assert.assertTrue(isSetEqual(orgnameSet, orgnameSet2));
        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    @Test
    public void updateUserByUsername3() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-updateUserByUsername3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
        }});
        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String username = addUserReqDTO.getUsername();

        UpdateUserByUsernameReqDTO updateUserByUsernameReqDTO = ModelMapperUtil.map(userService.get(QUser.user.username.eq(username)), UpdateUserByUsernameReqDTO.class);
        updateUserByUsernameReqDTO.setOrgnameSet(new HashSet<String>() {{

        }});
        updateUserByUsernameReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
        }});
        userService.updateUserByUsername(updateUserByUsernameReqDTO);

        TimeUnit.SECONDS.sleep(3);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUser.user.username.eq(username));
        User user = userService.get(predicate);
        if (ObjectUtils.isEmpty(user.getOrgnames())) {
            user.setOrgnames(",");
        }
        if (ObjectUtils.isEmpty(user.getRolenames())) {
            user.setRolenames(",");
        }
        Set<String> orgnameSet2 = Arrays.asList(user.getOrgnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        Set<String> rolenameSet2 = Arrays.asList(user.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        Set<String> orgnameSet = userOutput.getOrgnameSet();
        Set<String> rolenameSet = userOutput.getRolenameSet();

        log.info("orgnameSet={}", orgnameSet);
        log.info("orgnameSet2={}", orgnameSet2);
        Assert.assertTrue(isSetEqual(orgnameSet, orgnameSet2));
        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    @Test
    public void updateUserByUsernameMobile() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-updateUserByUsernameMobile-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setMobile(RandomStringUtils.randomNumeric(8));

        addUserReqDTO.setOrgnameSet(new HashSet<String>() {{
            add("ORG_SGSL");
        }});
        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String username = addUserReqDTO.getUsername();

        UpdateUserByUsernameReqDTO updateUserByUsernameReqDTO = ModelMapperUtil.map(userService.get(QUser.user.username.eq(username)), UpdateUserByUsernameReqDTO.class);

        updateUserByUsernameReqDTO.setMobile(addUserReqDTO.getMobile());
        updateUserByUsernameReqDTO.setOrgnameSet(new HashSet<String>() {{

        }});
        updateUserByUsernameReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
        }});
        userService.updateUserByUsername(updateUserByUsernameReqDTO);

        TimeUnit.SECONDS.sleep(3);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUser.user.username.eq(username));
        User user = userService.get(predicate);
        if (ObjectUtils.isEmpty(user.getOrgnames())) {
            user.setOrgnames(",");
        }
        if (ObjectUtils.isEmpty(user.getRolenames())) {
            user.setRolenames(",");
        }
        Set<String> orgnameSet2 = Arrays.asList(user.getOrgnames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        Set<String> rolenameSet2 = Arrays.asList(user.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        Set<String> orgnameSet = userOutput.getOrgnameSet();
        Set<String> rolenameSet = userOutput.getRolenameSet();

        log.info("orgnameSet={}", orgnameSet);
        log.info("orgnameSet2={}", orgnameSet2);
        Assert.assertTrue(isSetEqual(orgnameSet, orgnameSet2));
        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    @Test
    public void pageUser() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-pageUser-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        PageUserReqDTO pageUserReqDTO = new PageUserReqDTO();
        Result<PageRespDTO<UserRespDTO>> result = userService.pageUser(pageUserReqDTO);

        Assert.assertTrue(result.getData().getTotal() > 0);

        //清理
        String username = addUserReqDTO.getUsername();
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    @Test
    public void listUser() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddUserReqDTO addUserReqDTO = new AddUserReqDTO();
        addUserReqDTO.setUsername("test-listUser-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addUserReqDTO.setPassword("123456");

        addUserReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }});
        userService.addUser(addUserReqDTO);

        ListUserReqDTO listUserReqDTO = new ListUserReqDTO();
        Result<List<UserRespDTO>> result = userService.listUser(listUserReqDTO);

        Assert.assertTrue(result.getData().size() > 0);

        //清理
        String username = addUserReqDTO.getUsername();
        userService.delete(QUser.user.username.eq(username));

        userRoleService.getJpaQueryFactory().delete(QUserRole.userRole).where(QUserRole.userRole.username.eq(addUserReqDTO.getUsername()));
        userOrgService.getJpaQueryFactory().delete(QUserOrg.userOrg).where(QUserOrg.userOrg.username.eq(addUserReqDTO.getUsername()));

    }

    @SneakyThrows
    //@Test
    public void batchUpdateUser() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        List<String> usernameList = Arrays.asList("admin", "user", "province", "city", "commerce", "enterprise", "hongkongandmacao", "student", "test", "zhansan");

        for (String username : usernameList) {
            GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
            getUserByUsernameReqDTO.setUsername(username);
            UserRespDTO userOutput = userService.getUserByUsername(getUserByUsernameReqDTO).getData();

            if (userOutput == null) {
                continue;
            }
            UpdateUserByUsernameReqDTO updateUserByUsernameReqDTO = ModelMapperUtil.map(userOutput, UpdateUserByUsernameReqDTO.class);
            userService.updateUserByUsername(updateUserByUsernameReqDTO);
        }
        TimeUnit.SECONDS.sleep(720);

        Assert.assertTrue(1 == 1);

    }

}
