package com.ghf.exchange.boss.authorization.orgrole.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.GetOrgByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.orgrole.dto.*;
import com.ghf.exchange.boss.authorization.orgrole.entity.OrgRole;
import com.ghf.exchange.boss.authorization.orgrole.entity.QOrgRole;
import com.ghf.exchange.boss.authorization.orgrole.enums.OrgRoleStatusEnum;
import com.ghf.exchange.boss.authorization.orgrole.event.UpdateOrgRoleEvent;
import com.ghf.exchange.boss.authorization.orgrole.repository.OrgRoleRepository;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.authorization.role.dto.GetRoleByRolenameReqDTO;
import com.ghf.exchange.boss.authorization.role.dto.RoleRespDTO;
import com.ghf.exchange.boss.authorization.role.service.RoleService;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
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
public class OrgRoleServiceImpl extends BaseServiceImpl<OrgRole, Long> implements OrgRoleService {

    @Lazy
    @Resource
    private RoleService roleService;

    @Lazy
    @Resource
    private OrgService orgService;

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @Lazy
    @Resource
    private UserRoleService userRoleService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    public OrgRoleServiceImpl(OrgRoleRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "OrgRole", key = "'pageOrgRole:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.orgname) && T(org.springframework.util.StringUtils).isEmpty(#p0.rolename) && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrgRoleRespDTO>> pageOrgRole(PageOrgRoleReqDTO pageOrgRoleReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageOrgRoleReqDTO.getOrgname())) {
            predicate.and(QOrgRole.orgRole.orgname.contains(pageOrgRoleReqDTO.getOrgname()));
        }
        if (!ObjectUtils.isEmpty(pageOrgRoleReqDTO.getRolename())) {
            predicate.and(QOrgRole.orgRole.rolename.contains(pageOrgRoleReqDTO.getRolename()));
        }
        PageRespDTO<OrgRoleRespDTO> pageRespDTO = orgRoleService.page(predicate, pageOrgRoleReqDTO, OrgRoleRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "OrgRole", key = "'listOrgRoleByOrgname:'+#p0.orgname")
    @Override
    @SneakyThrows
    public Result<List<OrgRoleRespDTO>> listOrgRoleByOrgname(ListOrgRoleByOrgnameReqDTO listOrgRoleByOrgnameReqDTO) {
        String orgname = listOrgRoleByOrgnameReqDTO.getOrgname();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrgRole.orgRole.orgname.eq(orgname));
        //此接口只能获取启用状态的
        predicate.and(QOrgRole.orgRole.status.eq(OrgRoleStatusEnum.ENABLE.getCode()));
        List<OrgRoleRespDTO> list = orgRoleService.list(predicate, OrgRoleRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "OrgRole", key = "'listOrgRoleByRolename:'+#p0.rolename")
    @Override
    @SneakyThrows
    public Result<List<OrgRoleRespDTO>> listOrgRoleByRolename(ListOrgRoleByRolenameReqDTO listOrgRoleByRolenameReqDTO) {
        String rolename = listOrgRoleByRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrgRole.orgRole.rolename.eq(rolename));
        //此接口只能获取启用状态的
        predicate.and(QOrgRole.orgRole.status.eq(OrgRoleStatusEnum.ENABLE.getCode()));
        List<OrgRoleRespDTO> list = orgRoleService.list(predicate, OrgRoleRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "OrgRole", key = "'getOrgRoleByOrgnameAndRolename:'.concat(#p0.orgname).concat(':').concat(#p0.rolename)")
    @Override
    @SneakyThrows
    public Result<OrgRoleRespDTO> getOrgRoleByOrgnameAndRolename(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        //TODO 权限判断
        String orgname = getOrgRoleByOrgnameAndRolenameReqDTO.getOrgname();
        String rolename = getOrgRoleByOrgnameAndRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrgRole.orgRole.orgname.eq(orgname));
        predicate.and(QOrgRole.orgRole.rolename.eq(rolename));
        OrgRole orgRole = orgRoleService.get(predicate);
        //返回
        OrgRoleRespDTO orgRoleRespDTO = ModelMapperUtil.map(orgRole, OrgRoleRespDTO.class);
        return new Result<>(orgRoleRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrgRoleByOrgnameAndRolename(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        //TODO 权限判断
        String orgname = getOrgRoleByOrgnameAndRolenameReqDTO.getOrgname();
        String rolename = getOrgRoleByOrgnameAndRolenameReqDTO.getRolename();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrgRole.orgRole.orgname.eq(orgname));
        predicate.and(QOrgRole.orgRole.rolename.eq(rolename));
        boolean b = orgRoleService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "OrgRole", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrgRole(AddOrgRoleReqDTO addOrgRoleReqDTO) {
        OrgRole orgRole = ModelMapperUtil.map(addOrgRoleReqDTO, OrgRole.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        orgRole.setCreateUserId(currentLoginUser.getId());
        orgRole.setCreateUserName(currentLoginUser.getUsername());

        orgRole.setCreateTime(new Date());
        //判断唯一性
        String orgname = orgRole.getOrgname();
        String rolename = orgRole.getRolename();
        GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO = new GetOrgRoleByOrgnameAndRolenameReqDTO();
        getOrgRoleByOrgnameAndRolenameReqDTO.setOrgname(orgname);
        getOrgRoleByOrgnameAndRolenameReqDTO.setRolename(rolename);
        boolean b = orgRoleService.existsOrgRoleByOrgnameAndRolename(getOrgRoleByOrgnameAndRolenameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ORG_ROLE_EXISTS);
        }
        //初始化id
        orgRole.setId(IdUtil.generateLongId());
        orgRole.setStatus(OrgRoleStatusEnum.ENABLE.getCode());
        //新增到数据库
        orgRoleService.add(orgRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateOrgRoleEvent(addOrgRoleReqDTO.getOrgname()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "OrgRole", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableOrgRole(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        String orgname = getOrgRoleByOrgnameAndRolenameReqDTO.getOrgname();
        String rolename = getOrgRoleByOrgnameAndRolenameReqDTO.getRolename();
        //加载
        OrgRoleRespDTO afterOrgRoleRespDTO = orgRoleService.getOrgRoleByOrgnameAndRolename(getOrgRoleByOrgnameAndRolenameReqDTO).getData();
        OrgRole afterOrgRole = ModelMapperUtil.map(afterOrgRoleRespDTO, OrgRole.class);
        if (afterOrgRole == null) {
            return new Result<>(ResultCodeEnum.ORG_ROLE_NOT_EXISTS);
        }
        //初始化
        afterOrgRole.setStatus(OrgRoleStatusEnum.ENABLE.getCode());
        //更新到数据库
        orgRoleService.update(afterOrgRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateOrgRoleEvent(getOrgRoleByOrgnameAndRolenameReqDTO.getOrgname()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "OrgRole", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableOrgRole(GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO) {
        String orgname = getOrgRoleByOrgnameAndRolenameReqDTO.getOrgname();
        String rolename = getOrgRoleByOrgnameAndRolenameReqDTO.getRolename();
        //加载
        OrgRoleRespDTO afterOrgRoleRespDTO = orgRoleService.getOrgRoleByOrgnameAndRolename(getOrgRoleByOrgnameAndRolenameReqDTO).getData();
        OrgRole afterOrgRole = ModelMapperUtil.map(afterOrgRoleRespDTO, OrgRole.class);
        if (afterOrgRole == null) {
            return new Result<>(ResultCodeEnum.ORG_ROLE_NOT_EXISTS);
        }
        //初始化
        afterOrgRole.setStatus(OrgRoleStatusEnum.DISABLE.getCode());
        //更新到数据库
        orgRoleService.update(afterOrgRole);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateOrgRoleEvent(getOrgRoleByOrgnameAndRolenameReqDTO.getOrgname()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<List<RoleRespDTO>> listRoleByOrgname(ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO) {
        String orgname = listRoleByOrgnameReqDTO.getOrgname();
        ListOrgRoleByOrgnameReqDTO listOrgRoleByOrgnameReqDTO = new ListOrgRoleByOrgnameReqDTO();
        listOrgRoleByOrgnameReqDTO.setOrgname(orgname);
        List<OrgRoleRespDTO> list = orgRoleService.listOrgRoleByOrgname(listOrgRoleByOrgnameReqDTO).getData();
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
    public Result<List<OrgRespDTO>> listOrgByRolename(ListOrgByRolenameReqDTO listOrgByRolenameReqDTO) {
        String rolename = listOrgByRolenameReqDTO.getRolename();
        ListOrgRoleByRolenameReqDTO listOrgRoleByRolenameReqDTO = new ListOrgRoleByRolenameReqDTO();
        listOrgRoleByRolenameReqDTO.setRolename(rolename);
        List<OrgRoleRespDTO> list = orgRoleService.listOrgRoleByRolename(listOrgRoleByRolenameReqDTO).getData();
        List<OrgRespDTO> resultList = list.stream().map(e -> {
            String orgname = e.getOrgname();
            GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
            getOrgByOrgnameReqDTO.setOrgname(orgname);
            return orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        }).collect(Collectors.toList());
        return new Result<>(resultList);
    }
}