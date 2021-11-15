package com.ghf.exchange.boss.authorization.userrole.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.GetUserByUsernameReqDTO;
import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.role.dto.GetRoleByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.dto.*;
import com.ghf.exchange.boss.authorization.userrole.entity.QUserRole;
import com.ghf.exchange.boss.authorization.userrole.entity.UserRole;
import com.ghf.exchange.boss.authorization.userrole.enums.UserRoleStatusEnum;
import com.ghf.exchange.boss.authorization.userrole.event.UpdateUserRoleEvent;
import com.ghf.exchange.boss.authorization.userrole.repository.UserRoleRepository;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole, Long> implements UserRoleService {
    @Lazy
    @Resource
    private RoleService roleService;
    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private UserRoleService userRoleService;
    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;
    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;
    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public UserRoleServiceImpl(UserRoleRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "UserRole", key = "'pageUserRole:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.username) && T(org.springframework.util.StringUtils).isEmpty(#p0.rolename) && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<UserRoleRespDTO>> pageUserRole(PageUserRoleReqDTO pageUserRoleReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageUserRoleReqDTO.getUsername())) {
            predicate.and(QUserRole.userRole.username.contains(pageUserRoleReqDTO.getUsername()));
        }
        if (!ObjectUtils.isEmpty(pageUserRoleReqDTO.getRolename())) {
            predicate.and(QUserRole.userRole.rolename.contains(pageUserRoleReqDTO.getRolename()));
        }
        PageRespDTO<UserRoleRespDTO> pageResult = userRoleService.page(predicate, pageUserRoleReqDTO, UserRoleRespDTO.class);
        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "UserRole", key = "'listUserRoleByUsername:'+#p0.username")
    @Override
    @SneakyThrows
    public Result<List<UserRoleRespDTO>> listUserRoleByUsername(ListUserRoleByUsernameReqDTO listUserRoleByUsernameReqDTO) {
        String username = listUserRoleByUsernameReqDTO.getUsername();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserRole.userRole.username.eq(username));
        //此接口只能获取启用状态的
        predicate.and(QUserRole.userRole.status.eq(UserRoleStatusEnum.ENABLE.getCode()));
        List<UserRoleRespDTO> list = userRoleService.list(predicate, UserRoleRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "UserRole", key = "'listUserRoleByRolename:'+#p0.rolename")
    @Override
    @SneakyThrows
    public Result<List<UserRoleRespDTO>> listUserRoleByRolename(ListUserRoleByRolenameReqDTO listUserRoleByRolenameReqDTO) {
        String rolename = listUserRoleByRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserRole.userRole.rolename.eq(rolename));
        //此接口只能获取启用状态的
        predicate.and(QUserRole.userRole.status.eq(UserRoleStatusEnum.ENABLE.getCode()));
        List<UserRoleRespDTO> list = userRoleService.list(predicate, UserRoleRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "UserRole", key = "'getUserRoleByUsernameAndRolename:'.concat(#p0.username).concat(':').concat(#p0.rolename)")
    @Override
    @SneakyThrows
    public Result<UserRoleRespDTO> getUserRoleByUsernameAndRolename(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        //TODO 权限判断
        String username = getUserRoleByUsernameAndRolenameReqDTO.getUsername();
        String rolename = getUserRoleByUsernameAndRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserRole.userRole.username.eq(username));
        predicate.and(QUserRole.userRole.rolename.eq(rolename));
        UserRole userRole = userRoleService.get(predicate);
        //返回
        UserRoleRespDTO userRoleRespDTO = AutoMapUtils.map(userRole, UserRoleRespDTO.class);
        return new Result<>(userRoleRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsUserRoleByUsernameAndRolename(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        //TODO 权限判断
        String username = getUserRoleByUsernameAndRolenameReqDTO.getUsername();
        String rolename = getUserRoleByUsernameAndRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserRole.userRole.username.eq(username));
        predicate.and(QUserRole.userRole.rolename.eq(rolename));
        boolean b = userRoleService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "UserRole", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addUserRole(AddUserRoleReqDTO addUserRoleReqDTO) {
        UserRole userRole = AutoMapUtils.map(addUserRoleReqDTO, UserRole.class);
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        userRole.setCreateUserId(currentLoginUser.getId());
        userRole.setCreateUserName(currentLoginUser.getUsername());

        userRole.setCreateTime(new Date());
        //判断唯一性
        String username = userRole.getUsername();
        String rolename = userRole.getRolename();
        GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO = new GetUserRoleByUsernameAndRolenameReqDTO();
        getUserRoleByUsernameAndRolenameReqDTO.setUsername(username);
        getUserRoleByUsernameAndRolenameReqDTO.setRolename(rolename);
        boolean b = userRoleService.existsUserRoleByUsernameAndRolename(getUserRoleByUsernameAndRolenameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_ROLE_EXISTS);
        }
        //初始化id
        userRole.setId(IdUtil.generateLongId());
        userRole.setStatus(UserRoleStatusEnum.ENABLE.getCode());
        //新增到数据库
        userRoleService.add(userRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserRoleEvent(addUserRoleReqDTO.getUsername()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "UserRole", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableUserRole(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        String username = getUserRoleByUsernameAndRolenameReqDTO.getUsername();
        String rolename = getUserRoleByUsernameAndRolenameReqDTO.getRolename();
        //加载
        UserRoleRespDTO afterUserRoleRespDTO = userRoleService.getUserRoleByUsernameAndRolename(getUserRoleByUsernameAndRolenameReqDTO).getData();
        UserRole afterUserRole = AutoMapUtils.map(afterUserRoleRespDTO, UserRole.class);
        if (afterUserRole == null) {
            return new Result<>(ResultCodeEnum.USER_ROLE_NOT_EXISTS);
        }
        //初始化
        afterUserRole.setStatus(UserRoleStatusEnum.ENABLE.getCode());
        //更新到数据库
        userRoleService.update(afterUserRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserRoleEvent(getUserRoleByUsernameAndRolenameReqDTO.getUsername()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "UserRole", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableUserRole(GetUserRoleByUsernameAndRolenameReqDTO getUserRoleByUsernameAndRolenameReqDTO) {
        String username = getUserRoleByUsernameAndRolenameReqDTO.getUsername();
        String rolename = getUserRoleByUsernameAndRolenameReqDTO.getRolename();
        //加载
        UserRoleRespDTO afterUserRoleRespDTO = userRoleService.getUserRoleByUsernameAndRolename(getUserRoleByUsernameAndRolenameReqDTO).getData();
        UserRole afterUserRole = AutoMapUtils.map(afterUserRoleRespDTO, UserRole.class);
        if (afterUserRole == null) {
            return new Result<>(ResultCodeEnum.USER_ROLE_NOT_EXISTS);
        }
        //初始化
        afterUserRole.setStatus(UserRoleStatusEnum.DISABLE.getCode());
        //更新到数据库
        userRoleService.update(afterUserRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserRoleEvent(getUserRoleByUsernameAndRolenameReqDTO.getUsername()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRoleByUsername(ListRoleByUsernameReqDTO listRoleByUsernameReqDTO) {
        String username = listRoleByUsernameReqDTO.getUsername();
        ListUserRoleByUsernameReqDTO listUserRoleByUsernameReqDTO = new ListUserRoleByUsernameReqDTO();
        listUserRoleByUsernameReqDTO.setUsername(username);
        List<UserRoleRespDTO> list = userRoleService.listUserRoleByUsername(listUserRoleByUsernameReqDTO).getData();
        List<RoleRespDTO> resultList = list.stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> {
            String rolename = e.getRolename();
            GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
            getRoleByRolenameReqDTO.setRolename(rolename);
            return roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();
        }).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());
        return new Result<>(resultList);

    }

    @Override
    @SneakyThrows
    public Result<List<UserRespDTO>> listUserByRolename(ListUserByRolenameReqDTO listUserByRolenameReqDTO) {
        String rolename = listUserByRolenameReqDTO.getRolename();
        ListUserRoleByRolenameReqDTO listUserRoleByRolenameReqDTO = new ListUserRoleByRolenameReqDTO();
        listUserRoleByRolenameReqDTO.setRolename(rolename);
        List<UserRoleRespDTO> list = userRoleService.listUserRoleByRolename(listUserRoleByRolenameReqDTO).getData();
        List<UserRespDTO> resultList = list.stream().map(e -> {
            String username = e.getUsername();
            GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
            getUserByUsernameReqDTO.setUsername(username);
            return userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        }).collect(Collectors.toList());
        return new Result<>(resultList);
    }

}