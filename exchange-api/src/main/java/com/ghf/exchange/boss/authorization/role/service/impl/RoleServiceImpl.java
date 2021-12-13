package com.ghf.exchange.boss.authorization.role.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.role.dto.*;
import com.ghf.exchange.boss.authorization.role.entity.QRole;
import com.ghf.exchange.boss.authorization.role.entity.Role;
import com.ghf.exchange.boss.authorization.role.enums.RoleStatusEnum;
import com.ghf.exchange.boss.authorization.role.event.UpdateRolePermissionEvent;
import com.ghf.exchange.boss.authorization.role.repository.RoleRepository;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
import com.ghf.exchange.boss.authorization.rolepermission.service.RolePermissionService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<Role, Long> implements RoleService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private RoleService roleService;

    @Lazy
    @Resource
    private RolePermissionService rolePermissionService;
    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public RoleServiceImpl(RoleRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Role", key = "'pageRole:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.rolename).concat(':').concat(#p0.permissionname) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.roledesc)  && T(org.springframework.util.StringUtils).isEmpty(#p0.permissiondesc)   && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<RoleRespDTO>> pageRole(PageRoleReqDTO pageRoleReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageRoleReqDTO.getRolename())) {
            predicate.and(QRole.role.rolename.contains(pageRoleReqDTO.getRolename()));
        }
        if (!ObjectUtils.isEmpty(pageRoleReqDTO.getRoledesc())) {
            predicate.and(QRole.role.roledesc.contains(pageRoleReqDTO.getRoledesc()));
        }
        if (!ObjectUtils.isEmpty(pageRoleReqDTO.getPermissionname())) {
            predicate.and(QRole.role.permissionnames.contains("," + pageRoleReqDTO.getPermissionname() + ","));
        }
        PageRespDTO<RoleRespDTO> pageRespDTO = roleService.page(predicate, pageRoleReqDTO, RoleRespDTO.class);

        pageRespDTO.getList().forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getPermissionnames())) {
                e.setPermissionnameSet(Arrays.stream(e.getPermissionnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getPermissiondescs())) {
                e.setPermissiondescSet(Arrays.stream(e.getPermissiondescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }

        });
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Role", key = "'listRole:'.concat(#p0.rolename).concat(':').concat(#p0.permissionname)", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.roledesc)  && T(org.springframework.util.StringUtils).isEmpty(#p0.permissiondesc)    ")
    @Override
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRole(ListRoleReqDTO listRoleReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listRoleReqDTO.getRolename())) {
            predicate.and(QRole.role.rolename.eq(listRoleReqDTO.getRolename()));
        }
        if (!ObjectUtils.isEmpty(listRoleReqDTO.getRoledesc())) {
            predicate.and(QRole.role.roledesc.eq(listRoleReqDTO.getRoledesc()));
        }
        if (!ObjectUtils.isEmpty(listRoleReqDTO.getPermissionname())) {
            predicate.and(QRole.role.permissionnames.contains("," + listRoleReqDTO.getPermissionname() + ","));
        }
        //此接口只能获取启用状态的
        predicate.and(QRole.role.status.eq(RoleStatusEnum.ENABLE.getCode()));
        List<RoleRespDTO> list = roleService.list(predicate, RoleRespDTO.class);
        list.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getPermissionnames())) {
                e.setPermissionnameSet(Arrays.stream(e.getPermissionnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getPermissiondescs())) {
                e.setPermissiondescSet(Arrays.stream(e.getPermissiondescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }

        });
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Role", key = "'getRoleByRolename:'+#p0.rolename")
    @Override
    @SneakyThrows
    public Result<RoleRespDTO> getRoleByRolename(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        //TODO 权限判断
        String rolename = getRoleByRolenameReqDTO.getRolename();
        Predicate predicate = QRole.role.rolename.eq(rolename);
        Role role = roleService.get(predicate);

        //返回
        RoleRespDTO roleRespDTO = ModelMapperUtil.map(role, RoleRespDTO.class);
        if (!ObjectUtils.isEmpty(roleRespDTO.getPermissionnames())) {
            roleRespDTO.setPermissionnameSet(Arrays.stream(roleRespDTO.getPermissionnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(roleRespDTO.getPermissiondescs())) {
            roleRespDTO.setPermissiondescSet(Arrays.stream(roleRespDTO.getPermissiondescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        return new Result<>(roleRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsRoleByRolename(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        //TODO 权限判断
        String rolename = getRoleByRolenameReqDTO.getRolename();
        Predicate predicate = QRole.role.rolename.eq(rolename);
        boolean b = roleService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Role", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addRole(AddRoleReqDTO addRoleReqDTO) {
        Role role = ModelMapperUtil.map(addRoleReqDTO, Role.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        role.setCreateUserId(currentLoginUser.getId());
        role.setCreateUserName(currentLoginUser.getUsername());

        role.setCreateTime(new Date());
        //判断唯一性
        String rolename = role.getRolename();
        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        boolean b = roleService.existsRoleByRolename(getRoleByRolenameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ROLE_EXISTS);
        }
        //初始化id
        role.setId(IdUtil.generateLongId());
        role.setStatus(RoleStatusEnum.ENABLE.getCode());
        //新增到数据库
        roleService.add(role);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateRolePermissionEvent(UpdateRolePermissionEvent.Payload.builder().rolename(addRoleReqDTO.getRolename()).permissionnameSet(addRoleReqDTO.getPermissionnameSet()).build()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Role", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateRoleByRolename(UpdateRoleByRolenameReqDTO updateRoleReqDTO) {
        String rolename = updateRoleReqDTO.getRolename();
        String roledesc = updateRoleReqDTO.getRoledesc();
        String remark = updateRoleReqDTO.getRemark();
        //加载
        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        RoleRespDTO afterRoleRespDTO = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();
        Role afterRole = ModelMapperUtil.map(afterRoleRespDTO, Role.class);
        if (afterRole == null) {
            return new Result<>(ResultCodeEnum.ROLE_NOT_EXISTS);
        }
        //初始化
        afterRole.setRoledesc(roledesc);
        afterRole.setRemark(remark);
        //更新到数据库
        roleService.update(afterRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateRolePermissionEvent(UpdateRolePermissionEvent.Payload.builder().rolename(updateRoleReqDTO.getRolename()).permissionnameSet(updateRoleReqDTO.getPermissionnameSet()).build()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Role", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateRolePermissionnamesByRolename(UpdateRolePermissionnamesByRolenameReqDTO updateRolePermissionnamesByRolenameReqDTO) {
        String rolename = updateRolePermissionnamesByRolenameReqDTO.getRolename();
        String permissionnames = updateRolePermissionnamesByRolenameReqDTO.getPermissionnames();
        String permissiondescs = updateRolePermissionnamesByRolenameReqDTO.getPermissiondescs();
        //加载
        GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
        getRoleByRolenameReqDTO.setRolename(rolename);
        RoleRespDTO afterRoleRespDTO = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();
        Role afterRole = ModelMapperUtil.map(afterRoleRespDTO, Role.class);
        if (afterRole == null) {
            return new Result<>(ResultCodeEnum.ROLE_NOT_EXISTS);
        }

        //初始化
        afterRole.setPermissionnames(permissionnames);
        afterRole.setPermissiondescs(permissiondescs);
        //更新到数据库
        roleService.update(afterRole);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("Role", "User", "UserRole", "OrgRole", "RolePermission");

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Role", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableRole(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        String rolename = getRoleByRolenameReqDTO.getRolename();
        //加载
        RoleRespDTO afterRoleRespDTO = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();
        Role afterRole = ModelMapperUtil.map(afterRoleRespDTO, Role.class);
        if (afterRole == null) {
            return new Result<>(ResultCodeEnum.ROLE_NOT_EXISTS);
        }
        //初始化
        afterRole.setStatus(RoleStatusEnum.ENABLE.getCode());
        //更新到数据库
        roleService.update(afterRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateRolePermissionEvent(UpdateRolePermissionEvent.Payload.builder().rolename(getRoleByRolenameReqDTO.getRolename()).permissionnameSet(afterRoleRespDTO.getPermissionnameSet()).build()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Role", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableRole(GetRoleByRolenameReqDTO getRoleByRolenameReqDTO) {
        String rolename = getRoleByRolenameReqDTO.getRolename();
        //加载
        RoleRespDTO afterRoleRespDTO = roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();
        Role afterRole = ModelMapperUtil.map(afterRoleRespDTO, Role.class);
        if (afterRole == null) {
            return new Result<>(ResultCodeEnum.ROLE_NOT_EXISTS);
        }
        //初始化
        afterRole.setStatus(RoleStatusEnum.DISABLE.getCode());
        //更新到数据库
        roleService.update(afterRole);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("Role", "User", "UserRole", "OrgRole", "RolePermission");

        return new Result<>(ResultCodeEnum.OK);
    }
}