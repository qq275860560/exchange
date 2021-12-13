package com.ghf.exchange.boss.authorication.user.listener;

import com.ghf.exchange.boss.authorication.user.dto.AddUserReqDTO;
import com.ghf.exchange.boss.authorication.user.dto.UpdateUserByUsernameEvent;
import com.ghf.exchange.boss.authorication.user.dto.UpdateUserByUsernameReqDTO;
import com.ghf.exchange.boss.authorication.user.dto.UpdateUserLastLoginTimeAndLastLoginIpReqDTO;
import com.ghf.exchange.boss.authorication.user.entity.QUser;
import com.ghf.exchange.boss.authorication.user.entity.User;
import com.ghf.exchange.boss.authorication.user.event.AddUserEvent;
import com.ghf.exchange.boss.authorication.user.event.UpdateUserLastLoginTimeAndLastLoginIpEvent;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.userorg.dto.AddUserOrgReqDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.GetUserOrgByUsernameAndOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.dto.ListOrgByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.dto.AddUserRoleReqDTO;
import com.ghf.exchange.boss.authorization.userrole.dto.GetUserRoleByUsernameAndRolenameReqDTO;
import com.ghf.exchange.boss.authorization.userrole.dto.ListRoleByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class UserListener {

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private UserRoleService userRoleService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    public PasswordEncoder passwordEncoder;

    @Lazy
    @Resource
    private AuthenticationManager authenticationManager;

    @Lazy
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void onUpdateUserLastLoginTimeAndLastLoginIp(UpdateUserLastLoginTimeAndLastLoginIpEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        UpdateUserLastLoginTimeAndLastLoginIpReqDTO updateUserLastLoginTimeAndLastLoginIpReqDTO = ((UpdateUserLastLoginTimeAndLastLoginIpReqDTO) event.getSource());

        String username = updateUserLastLoginTimeAndLastLoginIpReqDTO.getUsername();
        String lastLoginIp = updateUserLastLoginTimeAndLastLoginIpReqDTO.getLastLoginIp();
        Date lastLoginTime = updateUserLastLoginTimeAndLastLoginIpReqDTO.getLastLoginTime();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = userService.get(predicate);

        //初始化
        user.setLastLoginIp(lastLoginIp);
        user.setLastLoginTime(lastLoginTime);
        //更新到数据库
        userService.update(user);
    }

    @Async
    @EventListener
    public void onAddUserEvent(AddUserEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("User", "UserRole", "UserOrg");
        String username = ((AddUserReqDTO) event.getSource()).getUsername();

        Set<String> orgnameSet = ((AddUserReqDTO) event.getSource()).getOrgnameSet();
        for (String orgname : orgnameSet) {
            if (!ObjectUtils.isEmpty(orgname)) {
                AddUserOrgReqDTO addUserOrgReqDTO = new AddUserOrgReqDTO();
                addUserOrgReqDTO.setOrgname(orgname);
                addUserOrgReqDTO.setUsername(username);
                userOrgService.addUserOrg(addUserOrgReqDTO);
            }
        }

        Set<String> rolenameSet = ((AddUserReqDTO) event.getSource()).getRolenameSet();
        for (String rolename : rolenameSet) {
            if (!ObjectUtils.isEmpty(rolename)) {
                AddUserRoleReqDTO addUserRoleReqDTO = new AddUserRoleReqDTO();
                addUserRoleReqDTO.setRolename(rolename);
                addUserRoleReqDTO.setUsername(username);
                userRoleService.addUserRole(addUserRoleReqDTO);
            }
        }

    }

    @Async
    @EventListener
    public void onUpdateUserEvent(UpdateUserByUsernameEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("User", "UserRole", "UserOrg");
        String username = ((UpdateUserByUsernameReqDTO) event.getSource()).getUsername();
        Set<String> orgnameSet = ((UpdateUserByUsernameReqDTO) event.getSource()).getOrgnameSet();

        ListOrgByUsernameReqDTO listOrgByUsernameReqDTO = ListOrgByUsernameReqDTO.builder().username(username).build();
        Set<String> oldOrgnameSet = userOrgService.listOrgByUsername(listOrgByUsernameReqDTO).getData().stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getOrgname()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());
        Iterator<String> oldOrgnameIterator = oldOrgnameSet.iterator();
        while (oldOrgnameIterator.hasNext()) {
            String oldOrgname = oldOrgnameIterator.next();
            //本来存在，现在不存在，相当于执行禁用操作
            if (!orgnameSet.contains(oldOrgname)) {
                oldOrgnameIterator.remove();
                GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO = GetUserOrgByUsernameAndOrgnameReqDTO.builder().orgname(oldOrgname).username(username).build();
                userOrgService.disableUserOrg(getUserOrgByUsernameAndOrgnameReqDTO);
            }
        }
        Iterator<String> orgnameIterator = orgnameSet.iterator();
        while (orgnameIterator.hasNext()) {
            String orgname = orgnameIterator.next();
            //本来不存在，现在存在，相当于执行新增操作或启用操作
            if (!oldOrgnameSet.contains(orgname)) {
                GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO = new GetUserOrgByUsernameAndOrgnameReqDTO();
                getUserOrgByUsernameAndOrgnameReqDTO.setOrgname(orgname);
                getUserOrgByUsernameAndOrgnameReqDTO.setUsername(username);
                boolean b = userOrgService.existsUserOrgByUsernameAndOrgname(getUserOrgByUsernameAndOrgnameReqDTO).getData();
                if (b) {
                    userOrgService.enableUserOrg(getUserOrgByUsernameAndOrgnameReqDTO);
                } else {
                    AddUserOrgReqDTO addUserOrgReqDTO = new AddUserOrgReqDTO();
                    addUserOrgReqDTO.setUsername(username);
                    addUserOrgReqDTO.setOrgname(orgname);
                    userOrgService.addUserOrg(addUserOrgReqDTO);
                }
            }
        }
        Set<String> rolenameSet = ((UpdateUserByUsernameReqDTO) event.getSource()).getRolenameSet();

        ListRoleByUsernameReqDTO listRoleByUsernameReqDTO = new ListRoleByUsernameReqDTO();
        listRoleByUsernameReqDTO.setUsername(username);
        Set<String> oldRolenameSet = userRoleService.listRoleByUsername(listRoleByUsernameReqDTO).getData().stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getRolename()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());
        Iterator<String> oldRolenameIterator = oldRolenameSet.iterator();
        while (oldRolenameIterator.hasNext()) {
            String oldRolename = oldRolenameIterator.next();
            //本来存在，现在不存在，相当于执行禁用操作
            if (!rolenameSet.contains(oldRolename)) {
                oldRolenameIterator.remove();
                GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO = new GetUserRoleByUsernameAndRolenameReqDTO();
                getUserRoleByUsernameAndRolenameReqDTO.setRolename(oldRolename);
                getUserRoleByUsernameAndRolenameReqDTO.setUsername(username);
                userRoleService.disableUserRole(getUserRoleByUsernameAndRolenameReqDTO);
            }
        }
        Iterator<String> rolenameIterator = rolenameSet.iterator();
        while (rolenameIterator.hasNext()) {
            String rolename = rolenameIterator.next();
            //本来不存在，现在存在，相当于执行新增操作或启用操作
            if (!oldRolenameSet.contains(rolename)) {
                GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO = new GetUserRoleByUsernameAndRolenameReqDTO();
                getUserRoleByUsernameAndRolenameReqDTO.setRolename(rolename);
                getUserRoleByUsernameAndRolenameReqDTO.setUsername(username);
                boolean b = userRoleService.existsUserRoleByUsernameAndRolename(getUserRoleByUsernameAndRolenameReqDTO).getData();
                if (b) {
                    userRoleService.enableUserRole(getUserRoleByUsernameAndRolenameReqDTO);
                } else {
                    AddUserRoleReqDTO addUserRoleReqDTO = new AddUserRoleReqDTO();
                    addUserRoleReqDTO.setUsername(username);
                    addUserRoleReqDTO.setRolename(rolename);
                    userRoleService.addUserRole(addUserRoleReqDTO);
                }
            }
        }
    }

}