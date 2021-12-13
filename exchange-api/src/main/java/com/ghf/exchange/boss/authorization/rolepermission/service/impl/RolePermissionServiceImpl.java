package com.ghf.exchange.boss.authorization.rolepermission.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.orgrole.dto.ListRoleByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.permission.dto.GetPermissionByPermissionnameReqDTO;
import com.ghf.exchange.boss.authorization.permission.dto.PermissionRespDTO;
import com.ghf.exchange.boss.authorization.permission.service.PermissionService;
import com.ghf.exchange.boss.authorization.role.dto.GetRoleByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
import com.ghf.exchange.boss.authorization.rolepermission.dto.*;
import com.ghf.exchange.boss.authorization.rolepermission.entity.QRolePermission;
import com.ghf.exchange.boss.authorization.rolepermission.entity.RolePermission;
import com.ghf.exchange.boss.authorization.rolepermission.enums.RolePermissionStatusEnum;
import com.ghf.exchange.boss.authorization.rolepermission.event.UpdateRolePermissionEvent;
import com.ghf.exchange.boss.authorization.rolepermission.repository.RolePermissionRepository;
import com.ghf.exchange.boss.authorization.rolepermission.service.RolePermissionService;
import com.ghf.exchange.boss.authorization.userorg.dto.ListOrgByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
import com.ghf.exchange.boss.authorization.userrole.dto.ListRoleByUsernameReqDTO;
import com.ghf.exchange.boss.authorization.userrole.service.UserRoleService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service("rolePermissionService")
@Lazy
@Slf4j
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermission, Long> implements RolePermissionService {

    @Lazy
    @Resource
    private RoleService roleService;
    @Lazy
    @Resource
    private OrgService orgService;
    @Lazy
    @Resource
    private OrgRoleService orgRoleService;
    @Lazy
    @Resource
    private UserRoleService userRoleService;
    @Lazy
    @Resource
    private UserOrgService userOrgService;
    @Lazy
    @Resource
    private RolePermissionService rolePermissionService;
    @Lazy
    @Resource
    private PermissionService permissionService;
    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public RolePermissionServiceImpl(RolePermissionRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "RolePermission", key = "'pageRolePermission:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction) ", condition = " T(org.springframework.util.StringUtils).isEmpty(#p0.rolename) && T(org.springframework.util.StringUtils).isEmpty(#p0.permissionname) && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<RolePermissionRespDTO>> pageRolePermission(PageRolePermissionReqDTO pageRolePermissionReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageRolePermissionReqDTO.getRolename())) {
            predicate.and(QRolePermission.rolePermission.rolename.contains(pageRolePermissionReqDTO.getRolename()));
        }
        if (!ObjectUtils.isEmpty(pageRolePermissionReqDTO.getPermissionname())) {
            predicate.and(QRolePermission.rolePermission.permissionname.contains(pageRolePermissionReqDTO.getPermissionname()));
        }
        PageRespDTO<RolePermissionRespDTO> pageRespDTO = rolePermissionService.page(predicate, pageRolePermissionReqDTO, RolePermissionRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "RolePermission", key = "'listRolePermissionByRolename:'+#p0.rolename")
    @Override
    @SneakyThrows
    public Result<List<RolePermissionRespDTO>> listRolePermissionByRolename(ListRolePermissionByRolenameReqDTO listRolePermissionByRolenameReqDTO) {
        String rolename = listRolePermissionByRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRolePermission.rolePermission.rolename.eq(rolename));
        //此接口只能获取启用状态的
        predicate.and(QRolePermission.rolePermission.status.eq(RolePermissionStatusEnum.ENABLE.getCode()));
        List<RolePermissionRespDTO> list = rolePermissionService.list(predicate, RolePermissionRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "RolePermission", key = "'listRolePermissionByPermissionname:'+#p0.permissionname")
    @Override
    @SneakyThrows
    public Result<List<RolePermissionRespDTO>> listRolePermissionByPermissionname(ListRolePermissionByPermissionnameReqDTO listRolePermissionByPermissionnameReqDTO) {
        String permissionname = listRolePermissionByPermissionnameReqDTO.getPermissionname();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRolePermission.rolePermission.permissionname.eq(permissionname));
        //此接口只能获取启用状态的
        predicate.and(QRolePermission.rolePermission.status.eq(RolePermissionStatusEnum.ENABLE.getCode()));
        List<RolePermissionRespDTO> list = rolePermissionService.list(predicate, RolePermissionRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "RolePermission", key = "'getRolePermissionByRolenameAndPermissionname:'.concat(#p0.rolename).concat(':').concat(#p0.permissionname)")
    @Override
    @SneakyThrows
    public Result<RolePermissionRespDTO> getRolePermissionByRolenameAndPermissionname(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        //TODO 权限判断
        String rolename = getRolePermissionByRolenameAndPermissionnameReqDTO.getRolename();
        String permissionname = getRolePermissionByRolenameAndPermissionnameReqDTO.getPermissionname();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRolePermission.rolePermission.rolename.eq(rolename));
        predicate.and(QRolePermission.rolePermission.permissionname.eq(permissionname));
        RolePermission rolePermission = rolePermissionService.get(predicate);
        //返回
        RolePermissionRespDTO rolePermissionRespDTO = ModelMapperUtil.map(rolePermission, RolePermissionRespDTO.class);
        return new Result<>(rolePermissionRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsRolePermissionByRolenameAndPermissionname(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        //TODO 权限判断
        String rolename = getRolePermissionByRolenameAndPermissionnameReqDTO.getRolename();
        String permissionname = getRolePermissionByRolenameAndPermissionnameReqDTO.getPermissionname();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QRolePermission.rolePermission.rolename.eq(rolename));
        predicate.and(QRolePermission.rolePermission.permissionname.eq(permissionname));
        boolean b = rolePermissionService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "RolePermission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addRolePermission(AddRolePermissionReqDTO addRolePermissionReqDTO) {
        RolePermission rolePermission = ModelMapperUtil.map(addRolePermissionReqDTO, RolePermission.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        rolePermission.setCreateUserId(currentLoginUser.getId());
        rolePermission.setCreateUserName(currentLoginUser.getUsername());

        rolePermission.setCreateTime(new Date());
        //判断唯一性
        String rolename = rolePermission.getRolename();
        String permissionname = rolePermission.getPermissionname();
        GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO = new GetRolePermissionByRolenameAndPermissionnameReqDTO();
        getRolePermissionByRolenameAndPermissionnameReqDTO.setRolename(rolename);
        getRolePermissionByRolenameAndPermissionnameReqDTO.setPermissionname(permissionname);
        boolean b = rolePermissionService.existsRolePermissionByRolenameAndPermissionname(getRolePermissionByRolenameAndPermissionnameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ROLE_PERMISSION_EXISTS);
        }
        //初始化id
        rolePermission.setId(IdUtil.generateLongId());
        rolePermission.setStatus(RolePermissionStatusEnum.ENABLE.getCode());
        //新增到数据库
        rolePermissionService.add(rolePermission);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateRolePermissionEvent(addRolePermissionReqDTO.getRolename()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "RolePermission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableRolePermission(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        String rolename = getRolePermissionByRolenameAndPermissionnameReqDTO.getRolename();
        String permissionname = getRolePermissionByRolenameAndPermissionnameReqDTO.getPermissionname();
        //加载
        RolePermissionRespDTO afterRolePermissionRespDTO = rolePermissionService.getRolePermissionByRolenameAndPermissionname(getRolePermissionByRolenameAndPermissionnameReqDTO).getData();
        RolePermission afterRolePermission = ModelMapperUtil.map(afterRolePermissionRespDTO, RolePermission.class);
        if (afterRolePermission == null) {
            return new Result<>(ResultCodeEnum.ROLE_PERMISSION_NOT_EXISTS);
        }
        //初始化
        afterRolePermission.setStatus(RolePermissionStatusEnum.ENABLE.getCode());
        //更新到数据库
        rolePermissionService.update(afterRolePermission);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateRolePermissionEvent(getRolePermissionByRolenameAndPermissionnameReqDTO.getRolename()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "RolePermission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableRolePermission(GetRolePermissionByRolenameAndPermissionnameReqDTO getRolePermissionByRolenameAndPermissionnameReqDTO) {
        String rolename = getRolePermissionByRolenameAndPermissionnameReqDTO.getRolename();
        String permissionname = getRolePermissionByRolenameAndPermissionnameReqDTO.getPermissionname();
        //加载
        RolePermissionRespDTO afterRolePermissionRespDTO = rolePermissionService.getRolePermissionByRolenameAndPermissionname(getRolePermissionByRolenameAndPermissionnameReqDTO).getData();
        RolePermission afterRolePermission = ModelMapperUtil.map(afterRolePermissionRespDTO, RolePermission.class);
        if (afterRolePermission == null) {
            return new Result<>(ResultCodeEnum.ROLE_PERMISSION_NOT_EXISTS);
        }
        //初始化
        afterRolePermission.setStatus(RolePermissionStatusEnum.DISABLE.getCode());
        //更新到数据库
        rolePermissionService.update(afterRolePermission);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateRolePermissionEvent(getRolePermissionByRolenameAndPermissionnameReqDTO.getRolename()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByRolename(ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO) {
        String rolename = listPermissionByRolenameReqDTO.getRolename();
        ListRolePermissionByRolenameReqDTO listRolePermissionByRolenameReqDTO = new ListRolePermissionByRolenameReqDTO();
        listRolePermissionByRolenameReqDTO.setRolename(rolename);
        List<RolePermissionRespDTO> list = rolePermissionService.listRolePermissionByRolename(listRolePermissionByRolenameReqDTO).getData();
        List<PermissionRespDTO> resultList = list.stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> {
            String permissionname = e.getPermissionname();
            GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
            getPermissionByPermissionnameReqDTO.setPermissionname(permissionname);
            return permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        }).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());
        return new Result<>(resultList);

    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> treePermissionByRolename(ListPermissionByRolenameReqDTO listPermissionByRolenameReqDTO) {
        List<PermissionRespDTO> originList = rolePermissionService.listPermissionByRolename(listPermissionByRolenameReqDTO).getData();

        List<PermissionRespDTO> rootNodes = new ArrayList<>();

        Iterator<PermissionRespDTO> it = originList.iterator();
        while (it.hasNext()) {
            PermissionRespDTO next = it.next();
            if (ObjectUtils.isEmpty(next.getParentPermissionname())) {
                rootNodes.add(next);

            }
        }

        //遍历，找到二级节点
        for (PermissionRespDTO permissionRespDTO : rootNodes) {
            List<PermissionRespDTO> child = getPermissionChildren(originList, permissionRespDTO.getPermissionname());
            permissionRespDTO.setChildren(child);
        }

        return new Result<>(rootNodes);
    }

    private List<PermissionRespDTO> getPermissionChildren(List<PermissionRespDTO> originList, String parentPermissionname) {
        //子节点列表
        List<PermissionRespDTO> childList = new ArrayList<>();
        Iterator<PermissionRespDTO> it = originList.iterator();
        while (it.hasNext()) {
            PermissionRespDTO next = it.next();
            //确定当前层节点，此接口只能获取启用状态的
            if (parentPermissionname.equals(next.getParentPermissionname())) {
                childList.add(next);
                it.remove();
            }
        }

        //遍历 递归获取子节点的子节点
        for (PermissionRespDTO permissionRespDTO : childList) {
            List<PermissionRespDTO> child = getPermissionChildren(originList, permissionRespDTO.getPermissionname());
            permissionRespDTO.setChildren(child);
        }
        return childList;
    }

    @Override
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRoleByPermissionname(ListRoleByPermissionnameReqDTO listRoleByPermissionnameReqDTO) {
        String permissionname = listRoleByPermissionnameReqDTO.getPermissionname();
        ListRolePermissionByPermissionnameReqDTO listRolePermissionByPermissionnameReqDTO = new ListRolePermissionByPermissionnameReqDTO();
        listRolePermissionByPermissionnameReqDTO.setPermissionname(permissionname);
        List<RolePermissionRespDTO> list = rolePermissionService.listRolePermissionByPermissionname(listRolePermissionByPermissionnameReqDTO).getData();
        List<RoleRespDTO> resultList = list.stream().map(e -> {
            String rolename = e.getRolename();
            GetRoleByRolenameReqDTO getRoleByRolenameReqDTO = new GetRoleByRolenameReqDTO();
            getRoleByRolenameReqDTO.setRolename(rolename);
            return roleService.getRoleByRolename(getRoleByRolenameReqDTO).getData();
        }).collect(Collectors.toList());
        return new Result<>(resultList);
    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByOrgname(ListPermissionByOrgnameReqDTO listPermissionByOrgnameReqDTO) {
        String orgname = listPermissionByOrgnameReqDTO.getOrgname();

        //获取角色
        ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
        listRoleByOrgnameReqDTO.setOrgname(orgname);
        Set<String> rolenameSet1 = orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData().stream().map(e -> e.getRolename()).collect(Collectors.toSet());
        //获取权限名称
        Set<String> permissionnameSet = rolenameSet1.stream().map(e -> {
            String rolename = e;
            ListRolePermissionByRolenameReqDTO listRolePermissionByRolenameReqDTO = new ListRolePermissionByRolenameReqDTO();
            listRolePermissionByRolenameReqDTO.setRolename(rolename);
            return rolePermissionService.listRolePermissionByRolename(listRolePermissionByRolenameReqDTO).getData();
        }).flatMap(Collection::stream).map(e -> e.getPermissionname()).collect(Collectors.toSet());
        //获取权限
        List<PermissionRespDTO> resultList = permissionnameSet.stream().map(e -> {
            GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
            getPermissionByPermissionnameReqDTO.setPermissionname(e);
            return permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        }).collect(Collectors.toList());
        return new Result<>(resultList);
    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> treePermissionByOrgname(ListPermissionByOrgnameReqDTO listPermissionByOrgnameReqDTO) {
        List<PermissionRespDTO> originList = rolePermissionService.listPermissionByOrgname(listPermissionByOrgnameReqDTO).getData();

        List<PermissionRespDTO> rootNodes = new ArrayList<>();

        Iterator<PermissionRespDTO> it = originList.iterator();
        while (it.hasNext()) {
            PermissionRespDTO next = it.next();
            if (ObjectUtils.isEmpty(next.getParentPermissionname())) {
                rootNodes.add(next);
            }
        }

        //遍历，找到二级节点
        for (PermissionRespDTO permissionRespDTO : rootNodes) {
            List<PermissionRespDTO> child = getPermissionChildren(originList, permissionRespDTO.getPermissionname());
            permissionRespDTO.setChildren(child);
        }

        return new Result<>(rootNodes);
    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByUsername(ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO) {

        //获取角色
        String username = listPermissionByUsernameReqDTO.getUsername();
        ListRoleByUsernameReqDTO listRoleByUsernameReqDTO = new ListRoleByUsernameReqDTO();
        listRoleByUsernameReqDTO.setUsername(username);
        Set<String> rolenameSet1 = userRoleService.listRoleByUsername(listRoleByUsernameReqDTO).getData().stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getRolename()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());

        ListOrgByUsernameReqDTO listOrgByUsernameReqDTO = new ListOrgByUsernameReqDTO();
        listOrgByUsernameReqDTO.setUsername(username);
        List<OrgRespDTO> orgRespDTOList = userOrgService.listOrgByUsername(listOrgByUsernameReqDTO).getData();

        Set<String> rolenameSet2 = orgRespDTOList.stream().map(e -> {
            String orgname = e.getOrgname();
            ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
            listRoleByOrgnameReqDTO.setOrgname(orgname);
            return orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData();
        }).flatMap(Collection::stream).filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getRolename()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());

        rolenameSet1.addAll(rolenameSet2);

        //获取权限名称
        Set<String> permissionnameSet = rolenameSet1.stream().map(e -> {
            String rolename = e;
            ListRolePermissionByRolenameReqDTO listRolePermissionByRolenameReqDTO = new ListRolePermissionByRolenameReqDTO();
            listRolePermissionByRolenameReqDTO.setRolename(rolename);
            return rolePermissionService.listRolePermissionByRolename(listRolePermissionByRolenameReqDTO).getData();
        }).filter(e -> !ObjectUtils.isEmpty(e)).flatMap(Collection::stream).filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getPermissionname()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());
        //获取权限
        List<PermissionRespDTO> resultList = permissionnameSet.stream().map(e -> {
            GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
            getPermissionByPermissionnameReqDTO.setPermissionname(e);
            return permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        }).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());

        if (listPermissionByUsernameReqDTO.getPermissionType() != 0) {

            Iterator<PermissionRespDTO> iterator = resultList.iterator();
            while (iterator.hasNext()) {
                PermissionRespDTO permissionRespDTO = iterator.next();
                if (permissionRespDTO.getPermissionType() != listPermissionByUsernameReqDTO.getPermissionType()) {
                    iterator.remove();
                }
            }
        }

        return new Result<>(resultList);

    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listCurrentLoginUserPermission(ListCurrentLoginUserPermissionReqDTO listCurrentLoginUserPermissionReqDTO) {
        ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO = new ListPermissionByUsernameReqDTO();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        listPermissionByUsernameReqDTO.setUsername(currentLoginUser.getUsername());
        listPermissionByUsernameReqDTO.setPermissionType(listCurrentLoginUserPermissionReqDTO.getPermissionType());
        return rolePermissionService.listPermissionByUsername(listPermissionByUsernameReqDTO);
    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> treePermissionByUsername(ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO) {
        List<PermissionRespDTO> originList = rolePermissionService.listPermissionByUsername(listPermissionByUsernameReqDTO).getData();

        if (listPermissionByUsernameReqDTO.getPermissionType() != 0) {

            Iterator<PermissionRespDTO> iterator = originList.iterator();
            while (iterator.hasNext()) {
                PermissionRespDTO permissionRespDTO = iterator.next();
                if (permissionRespDTO.getPermissionType() != listPermissionByUsernameReqDTO.getPermissionType()) {
                    iterator.remove();
                }
            }
        }

        List<PermissionRespDTO> rootNodes = new ArrayList<>();

        Iterator<PermissionRespDTO> it = originList.iterator();
        while (it.hasNext()) {
            PermissionRespDTO next = it.next();
            if (ObjectUtils.isEmpty(next.getParentPermissionname())) {

                rootNodes.add(next);

            }
        }

        //遍历，找到二级节点
        for (PermissionRespDTO permissionRespDTO : rootNodes) {
            List<PermissionRespDTO> child = getPermissionChildren(originList, permissionRespDTO.getPermissionname());
            permissionRespDTO.setChildren(child);
        }

        return new Result<>(rootNodes);
    }

    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> treeCurrentLoginUserPermission(ListCurrentLoginUserPermissionReqDTO listCurrentLoginUserPermissionReqDTO) {
        ListPermissionByUsernameReqDTO listPermissionByUsernameReqDTO = new ListPermissionByUsernameReqDTO();
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        listPermissionByUsernameReqDTO.setUsername(currentLoginUser.getUsername());
        listPermissionByUsernameReqDTO.setPermissionType(listCurrentLoginUserPermissionReqDTO.getPermissionType());
        return rolePermissionService.treePermissionByUsername(listPermissionByUsernameReqDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsCommonRoleByUsernameAndPermissionname(ExistsCommonRoleByUsernameAndPermissionnameReqDTO existsCommonRoleByUsernameAndPermissionnameReqDTO) {
        //获取用户的所有角色
        String username = existsCommonRoleByUsernameAndPermissionnameReqDTO.getUsername();
        ListRoleByUsernameReqDTO listRoleByUsernameReqDTO = new ListRoleByUsernameReqDTO();
        listRoleByUsernameReqDTO.setUsername(username);
        Set<String> rolenameSet1 = userRoleService.listRoleByUsername(listRoleByUsernameReqDTO).getData().stream().map(e -> e.getRolename()).collect(Collectors.toSet());

        ListOrgByUsernameReqDTO listOrgByUsernameReqDTO = new ListOrgByUsernameReqDTO();
        listOrgByUsernameReqDTO.setUsername(username);
        List<OrgRespDTO> orgRespDTOList = userOrgService.listOrgByUsername(listOrgByUsernameReqDTO).getData();

        Set<String> rolenameSet2 = orgRespDTOList.stream().map(e -> {
            String orgname = e.getOrgname();
            ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
            listRoleByOrgnameReqDTO.setOrgname(orgname);
            return orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData();
        }).flatMap(Collection::stream).map(e -> e.getRolename()).collect(Collectors.toSet());

        rolenameSet1.addAll(rolenameSet2);

        String permissionname = existsCommonRoleByUsernameAndPermissionnameReqDTO.getPermissionname();
        ListRoleByPermissionnameReqDTO listRoleByPermissionnameReqDTO = new ListRoleByPermissionnameReqDTO();
        listRoleByPermissionnameReqDTO.setPermissionname(permissionname);
        Set<String> rolenameSet3 = rolePermissionService.listRoleByPermissionname(listRoleByPermissionnameReqDTO).getData().stream().map(e -> e.getRolename()).collect(Collectors.toSet());

        boolean flag = false;
        for (String rolename : rolenameSet1) {
            if (rolenameSet3.contains(rolename)) {
                flag = true;
                break;
            }
        }

        return new Result<>(flag);
    }

    @Override
    @SneakyThrows
    public boolean decideRole(HttpServletRequest request, Authentication authentication) {
        log.debug("确定用户是否具备角色之一");
        if (!(authentication instanceof OAuth2Authentication)) {
            return false;
        }
        String requestUrl = request.getRequestURI();
        ListPermissionByRequestUrlReqDTO listPermissionByRequestUrlReqDTO = new ListPermissionByRequestUrlReqDTO();
        listPermissionByRequestUrlReqDTO.setRequestUrl(requestUrl);
        List<PermissionRespDTO> list = permissionService.listPermissionByRequestUrl(listPermissionByRequestUrlReqDTO).getData();
        Set<String> rolenames = list.stream().map(e -> {
            String permissionname = e.getPermissionname();
            ListRoleByPermissionnameReqDTO listRoleByPermissionnameReqDTO = new ListRoleByPermissionnameReqDTO();
            listRoleByPermissionnameReqDTO.setPermissionname(permissionname);
            return rolePermissionService.listRoleByPermissionname(listRoleByPermissionnameReqDTO).getData();
        }).flatMap(Collection::stream).map(e -> e.getRolename()).collect(Collectors.toSet());

        //uri不需要角色认证
        if (rolenames.isEmpty()) {
            return true;
        }
        Collection<? extends GrantedAuthority> authorities = ((OAuth2Authentication) authentication).getAuthorities();

        for (String roleName : rolenames) {
            for (GrantedAuthority authority : authorities) {
                if (roleName.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }

        log.debug("用户角色不足,用户角色={},url要求具备以下角色之一={}", authorities, rolenames);
        return false;

    }

}