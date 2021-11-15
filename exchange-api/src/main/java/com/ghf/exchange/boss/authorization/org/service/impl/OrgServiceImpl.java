package com.ghf.exchange.boss.authorization.org.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.*;
import com.ghf.exchange.boss.authorization.org.entity.Org;
import com.ghf.exchange.boss.authorization.org.entity.QOrg;
import com.ghf.exchange.boss.authorization.org.enums.OrgStatusEnum;
import com.ghf.exchange.boss.authorization.org.event.UpdateFullOrgEvent;
import com.ghf.exchange.boss.authorization.org.event.UpdateOrgRoleEvent;
import com.ghf.exchange.boss.authorization.org.repository.OrgRepository;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.common.area.dto.AreaRespDTO;
import com.ghf.exchange.boss.common.area.dto.GetAreaByAreanameReqDTO;
import com.ghf.exchange.boss.common.area.service.AreaService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrgServiceImpl extends BaseServiceImpl<Org, Long> implements OrgService {

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
    private AreaService areaService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;
    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public OrgServiceImpl(OrgRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Org", key = "'pageOrg:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.orgType).concat(':').concat(#p0.orgname).concat(':').concat(#p0.rolename) ", condition = " T(org.springframework.util.StringUtils).isEmpty(#p0.orgdesc) &&  T(org.springframework.util.StringUtils).isEmpty(#p0.roledesc)  && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<OrgRespDTO>> pageOrg(PageOrgReqDTO pageOrgReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageOrgReqDTO.getOrgname())) {
            predicate.and(QOrg.org.orgname.contains(pageOrgReqDTO.getOrgname()));
        }
        if (!ObjectUtils.isEmpty(pageOrgReqDTO.getOrgdesc())) {
            predicate.and(QOrg.org.orgdesc.contains(pageOrgReqDTO.getOrgdesc()));
        }
        if (pageOrgReqDTO.getOrgType() != 0) {
            predicate.and(QOrg.org.orgType.eq(pageOrgReqDTO.getOrgType()));
        }
        if (!ObjectUtils.isEmpty(pageOrgReqDTO.getRolename())) {
            predicate.and(QOrg.org.rolenames.contains("," + pageOrgReqDTO.getRolename() + ","));
        }
        PageRespDTO<OrgRespDTO> pageResult = orgService.page(predicate, pageOrgReqDTO, OrgRespDTO.class);
        pageResult.getList().forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getRolenames())) {
                e.setRolenameSet(Arrays.stream(e.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getRoledescs())) {
                e.setRoledescSet(Arrays.stream(e.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getFullOrgname())) {
                e.setFullOrgnameList(Arrays.stream(e.getFullOrgname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullOrgdesc())) {
                e.setFullOrgdescList(Arrays.stream(e.getFullOrgdesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreaname())) {
                e.setFullAreanameList(Arrays.stream(e.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreadesc())) {
                e.setFullAreadescList(Arrays.stream(e.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }

        });
        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "Org", key = "'treeOrg:'.concat(#p0.orgname).concat(#p0.treeDeep)")
    @Override
    @SneakyThrows
    public Result<OrgRespDTO> treeOrg(TreeOrgReqDTO treeOrgReqDTO) {
        //根节点
        OrgRespDTO orgRespDTO = null;
        if (ObjectUtils.isEmpty(treeOrgReqDTO.getOrgname())) {
            Predicate predicate = QOrg.org.parentOrgname.isEmpty().or(QOrg.org.parentOrgname.isNull());
            //有可能多个，此时此刻只选取第一个
            orgRespDTO = this.list(predicate, OrgRespDTO.class).get(0);

        } else {
            GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
            getOrgByOrgnameReqDTO.setOrgname(treeOrgReqDTO.getOrgname());
            orgRespDTO = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        }

        BooleanBuilder predicate = new BooleanBuilder();
        //此接口只能获取启用状态的
        predicate.and(QOrg.org.status.eq(OrgStatusEnum.ENABLE.getCode()));
        if (treeOrgReqDTO.getTreeDeep() != 0) {
            predicate.and(QOrg.org.deep.goe(orgRespDTO.getDeep())
                    .and(QOrg.org.deep.lt(orgRespDTO.getDeep() + treeOrgReqDTO.getTreeDeep())));
        }
        predicate.and(QOrg.org.fullOrgname.contains("," + orgRespDTO.getOrgname() + ","));

        List<OrgRespDTO> originList = orgService.list(predicate, OrgRespDTO.class);

        originList.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getRolenames())) {
                e.setRolenameSet(Arrays.stream(e.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getRoledescs())) {
                e.setRoledescSet(Arrays.stream(e.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getFullOrgname())) {
                e.setFullOrgnameList(Arrays.stream(e.getFullOrgname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullOrgdesc())) {
                e.setFullOrgdescList(Arrays.stream(e.getFullOrgdesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreaname())) {
                e.setFullAreanameList(Arrays.stream(e.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreadesc())) {
                e.setFullAreadescList(Arrays.stream(e.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }

        });

        //遍历，找到二级节点
        List<OrgRespDTO> child = getOrgChildren(originList, orgRespDTO.getOrgname());
        orgRespDTO.setChildren(child);

        return new Result<>(orgRespDTO);
    }

    private List<OrgRespDTO> getOrgChildren(List<OrgRespDTO> originList, String parentOrgname) {
        //子节点列表
        List<OrgRespDTO> childList = new ArrayList<>();
        Iterator<OrgRespDTO> it = originList.iterator();
        while (it.hasNext()) {
            OrgRespDTO next = it.next();
            //确定当前层节点，此接口只能获取启用状态的
            if (parentOrgname.equals(next.getParentOrgname())) {
                childList.add(next);
                it.remove();
            }
        }

        //遍历 递归获取子节点的子节点
        for (OrgRespDTO orgRespDTO : childList) {
            List<OrgRespDTO> child = getOrgChildren(originList, orgRespDTO.getOrgname());
            orgRespDTO.setChildren(child);
        }
        return childList;
    }

    @Cacheable(cacheNames = "Org", key = "'ListAncestorByOrgname:'+#p0.orgname")
    @Override
    @SneakyThrows
    public Result<List<OrgRespDTO>> listAncestorByOrgname(ListAncestorByOrgnameReqDTO listAncestorByOrgnameReqDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        //此接口只能获取启用状态的
        predicate.and(QOrg.org.status.eq(OrgStatusEnum.ENABLE.getCode()));
        List<OrgRespDTO> originList = orgService.list(predicate, OrgRespDTO.class);
        originList.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getRolenames())) {
                e.setRolenameSet(Arrays.stream(e.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getRoledescs())) {
                e.setRoledescSet(Arrays.stream(e.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getFullOrgname())) {
                e.setFullOrgnameList(Arrays.stream(e.getFullOrgname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullOrgdesc())) {
                e.setFullOrgdescList(Arrays.stream(e.getFullOrgdesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }

            if (!ObjectUtils.isEmpty(e.getFullAreaname())) {
                e.setFullAreanameList(Arrays.stream(e.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
            if (!ObjectUtils.isEmpty(e.getFullAreadesc())) {
                e.setFullAreadescList(Arrays.stream(e.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
            }
        });
        List<OrgRespDTO> nodes = new ArrayList<>();

        //找到自己
        OrgRespDTO currentOrgRespDTO = null;
        Iterator<OrgRespDTO> it0 = originList.iterator();
        while (it0.hasNext()) {
            OrgRespDTO orgRespDTO = it0.next();
            if (orgRespDTO.getOrgname().equals(listAncestorByOrgnameReqDTO.getOrgname())) {

                nodes.add(0, orgRespDTO);
                currentOrgRespDTO = orgRespDTO;
                it0.remove();

                //如果输入不为空,叶子节点是自己，找到直接退出
                break;
            }
        }

        while (currentOrgRespDTO != null && !ObjectUtils.isEmpty(currentOrgRespDTO.getParentOrgname())) {
            //说明此时理论上有父级节点
            boolean flag = false;
            Iterator<OrgRespDTO> it = originList.iterator();
            while (it.hasNext()) {
                OrgRespDTO next = it.next();
                if (currentOrgRespDTO.getParentOrgname().equals(next.getOrgname())) {

                    nodes.add(0, next);
                    currentOrgRespDTO = next;
                    it.remove();
                    flag = true;

                    //如果输入不为空,叶子节点是自己，找到直接退出
                    break;
                }
            }
            //如果没有找到父级节点
            if (!flag) {
                break;
            }
        }
        return new Result<>(nodes);
    }

    @Cacheable(cacheNames = "Org", key = "'getOrgByOrgname:'+#p0.orgname")
    @Override
    @SneakyThrows
    public Result<OrgRespDTO> getOrgByOrgname(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        //TODO 权限判断
        String orgname = getOrgByOrgnameReqDTO.getOrgname();
        Predicate predicate = QOrg.org.orgname.eq(orgname);
        Org org = orgService.get(predicate);

        //返回
        OrgRespDTO orgRespDTO = AutoMapUtils.map(org, OrgRespDTO.class);

        if (!ObjectUtils.isEmpty(orgRespDTO.getRolenames())) {
            orgRespDTO.setRolenameSet(Arrays.stream(orgRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getRoledescs())) {
            orgRespDTO.setRoledescSet(Arrays.stream(orgRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getFullOrgname())) {
            orgRespDTO.setFullOrgnameList(Arrays.stream(orgRespDTO.getFullOrgname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getFullOrgdesc())) {
            orgRespDTO.setFullOrgdescList(Arrays.stream(orgRespDTO.getFullOrgdesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }

        if (!ObjectUtils.isEmpty(orgRespDTO.getFullAreaname())) {
            orgRespDTO.setFullAreanameList(Arrays.stream(orgRespDTO.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getFullAreadesc())) {
            orgRespDTO.setFullAreadescList(Arrays.stream(orgRespDTO.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }

        return new Result<>(orgRespDTO);
    }

    @Cacheable(cacheNames = "Org", key = "'getOrgByOrgdesc:'+#p0.orgdesc")
    @Override
    @SneakyThrows
    public Result<OrgRespDTO> getOrgByOrgdesc(GetOrgByOrgdescReqDTO getOrgByOrgdescReqDTO) {
        //TODO 权限判断
        String orgdesc = getOrgByOrgdescReqDTO.getOrgdesc();
        Predicate predicate = QOrg.org.orgdesc.eq(orgdesc);
        Org org = orgService.get(predicate);

        //返回
        OrgRespDTO orgRespDTO = AutoMapUtils.map(org, OrgRespDTO.class);

        if (!ObjectUtils.isEmpty(orgRespDTO.getRolenames())) {
            orgRespDTO.setRolenameSet(Arrays.stream(orgRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getRoledescs())) {
            orgRespDTO.setRoledescSet(Arrays.stream(orgRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        if (!ObjectUtils.isEmpty(orgRespDTO.getFullOrgname())) {
            orgRespDTO.setFullOrgnameList(Arrays.stream(orgRespDTO.getFullOrgname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getFullOrgdesc())) {
            orgRespDTO.setFullOrgdescList(Arrays.stream(orgRespDTO.getFullOrgdesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getFullAreaname())) {
            orgRespDTO.setFullAreanameList(Arrays.stream(orgRespDTO.getFullAreaname().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(orgRespDTO.getFullAreadesc())) {
            orgRespDTO.setFullAreadescList(Arrays.stream(orgRespDTO.getFullAreadesc().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList()));
        }

        return new Result<>(orgRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsOrgByOrgname(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        //TODO 权限判断
        String orgname = getOrgByOrgnameReqDTO.getOrgname();
        Predicate predicate = QOrg.org.orgname.eq(orgname);
        boolean b = orgService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Org", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addOrg(AddOrgReqDTO addOrgReqDTO) {
        Org org = AutoMapUtils.map(addOrgReqDTO, Org.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        org.setCreateUserId(currentLoginUser.getId());
        org.setCreateUserName(currentLoginUser.getUsername());

        org.setCreateTime(new Date());
        //判断唯一性
        String orgname = org.getOrgname();
        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        boolean b = orgService.existsOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ORG_EXISTS);
        }
        //初始化id
        org.setId(IdUtil.generateLongId());
        org.setStatus(OrgStatusEnum.ENABLE.getCode());

        //初始化父节点
        if (!ObjectUtils.isEmpty(org.getParentOrgname())) {
            GetOrgByOrgnameReqDTO getParentOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
            getParentOrgByOrgnameReqDTO.setOrgname(org.getParentOrgname());
            OrgRespDTO parentOrgRespDTO = orgService.getOrgByOrgname(getParentOrgByOrgnameReqDTO).getData();
            Org parentOrg = AutoMapUtils.map(parentOrgRespDTO, Org.class);
            org.setParentOrgId(parentOrg.getId());
            org.setParentOrgname(parentOrg.getOrgname());
            org.setParentOrgdesc(parentOrg.getOrgdesc());
        } else {
            org.setParentOrgId(0);
            org.setParentOrgname(null);
            org.setParentOrgdesc(null);
        }
        if (ObjectUtils.isEmpty(addOrgReqDTO.getAreaname())) {
            org.setAreaname(null);
            org.setAreadesc(null);
            org.setFullAreaname(null);
            org.setFullAreadesc(null);
        } else {
            AreaRespDTO areaRespDTO = areaService.getAreaByAreaname(GetAreaByAreanameReqDTO.builder().areaname(addOrgReqDTO.getAreaname()).build()).getData();
            org.setAreaname(areaRespDTO.getAreaname());
            org.setAreadesc(areaRespDTO.getAreadesc());
            org.setFullAreaname(areaRespDTO.getFullAreaname());
            org.setFullAreadesc(areaRespDTO.getFullAreadesc());
        }
        //新增到数据库
        orgService.add(org);

        //更新组织权限关联
        applicationEventPublisher.publishEvent(new UpdateOrgRoleEvent(UpdateOrgRoleEvent.Payload.builder().orgname(addOrgReqDTO.getOrgname()).rolenameSet(addOrgReqDTO.getRolenameSet()).build()));

        //更新自身及其后代的完整组织信息
        applicationEventPublisher.publishEvent(new UpdateFullOrgEvent(getOrgByOrgnameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Org", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateOrgByOrgname(UpdateOrgByOrgnameReqDTO updateOrgByOrgnameReqDTO) {

        String orgname = updateOrgByOrgnameReqDTO.getOrgname();
        String orgdesc = updateOrgByOrgnameReqDTO.getOrgdesc();
        Integer orgType = updateOrgByOrgnameReqDTO.getOrgType();
        int orderNum = updateOrgByOrgnameReqDTO.getOrderNum();
        String parentOrgname = updateOrgByOrgnameReqDTO.getParentOrgname();

        //加载
        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO afterOrgRespDTO = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        Org afterOrg = AutoMapUtils.map(afterOrgRespDTO, Org.class);
        if (afterOrg == null) {
            return new Result<>(ResultCodeEnum.ORG_NOT_EXISTS);
        }
        //初始化
        afterOrg.setOrgdesc(orgdesc);
        afterOrg.setOrgType(orgType);
        afterOrg.setOrderNum(orderNum);
        //初始化父节点
        if (!ObjectUtils.isEmpty(parentOrgname)) {
            GetOrgByOrgnameReqDTO getParentOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
            getParentOrgByOrgnameReqDTO.setOrgname(parentOrgname);
            OrgRespDTO parentOrgRespDTO = orgService.getOrgByOrgname(getParentOrgByOrgnameReqDTO).getData();
            Org parentOrg = AutoMapUtils.map(parentOrgRespDTO, Org.class);

            afterOrg.setParentOrgId(parentOrg.getId());
            afterOrg.setParentOrgname(parentOrg.getOrgname());
            afterOrg.setParentOrgdesc(parentOrg.getOrgdesc());
        } else {
            afterOrg.setParentOrgId(0);
            afterOrg.setParentOrgname(null);
            afterOrg.setParentOrgdesc(null);
        }
        if (ObjectUtils.isEmpty(updateOrgByOrgnameReqDTO.getAreaname())) {
            afterOrg.setAreaname(null);
            afterOrg.setAreadesc(null);
            afterOrg.setFullAreaname(null);
            afterOrg.setFullAreadesc(null);
        } else {
            AreaRespDTO areaRespDTO = areaService.getAreaByAreaname(GetAreaByAreanameReqDTO.builder().areaname(updateOrgByOrgnameReqDTO.getAreaname()).build()).getData();
            afterOrg.setAreaname(areaRespDTO.getAreaname());
            afterOrg.setAreadesc(areaRespDTO.getAreadesc());
            afterOrg.setFullAreaname(areaRespDTO.getFullAreaname());
            afterOrg.setFullAreadesc(areaRespDTO.getFullAreadesc());
        }
        //更新到数据库
        orgService.update(afterOrg);

        //更新组织权限关联
        applicationEventPublisher.publishEvent(new UpdateOrgRoleEvent(UpdateOrgRoleEvent.Payload.builder().orgname(updateOrgByOrgnameReqDTO.getOrgname()).rolenameSet(updateOrgByOrgnameReqDTO.getRolenameSet()).build()));

        //更新自身及其后代的完整组织信息
        applicationEventPublisher.publishEvent(new UpdateFullOrgEvent(getOrgByOrgnameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "User", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateOrgRolenamesByOrgname(UpdateOrgRolenamesByOrgnameReqDTO updateOrgRolenamesByOrgnameReqDTO) {
        String orgname = updateOrgRolenamesByOrgnameReqDTO.getOrgname();
        String rolenames = updateOrgRolenamesByOrgnameReqDTO.getRolenames();
        String roledescs = updateOrgRolenamesByOrgnameReqDTO.getRoledescs();
        //加载
        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO afterOrgRespDTO = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        Org afterOrg = AutoMapUtils.map(afterOrgRespDTO, Org.class);

        //初始化
        afterOrg.setRolenames(rolenames);
        afterOrg.setRoledescs(roledescs);
        //更新到数据库
        this.update(afterOrg);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("Org", "User", "UserOrg", "OrgRole");

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Org", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableOrg(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        String orgname = getOrgByOrgnameReqDTO.getOrgname();
        //加载
        OrgRespDTO afterOrgRespDTO = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        Org afterOrg = AutoMapUtils.map(afterOrgRespDTO, Org.class);
        if (afterOrg == null) {
            return new Result<>(ResultCodeEnum.ORG_NOT_EXISTS);
        }
        //初始化
        afterOrg.setStatus(OrgStatusEnum.ENABLE.getCode());
        //更新到数据库
        orgService.update(afterOrg);

        //更新组织权限关联
        applicationEventPublisher.publishEvent(new UpdateOrgRoleEvent(UpdateOrgRoleEvent.Payload.builder().orgname(afterOrgRespDTO.getOrgname()).rolenameSet(afterOrgRespDTO.getRolenameSet()).build()));

        //更新自身及其后代的完整组织信息
        applicationEventPublisher.publishEvent(new UpdateFullOrgEvent(getOrgByOrgnameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Org", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableOrg(GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO) {
        String orgname = getOrgByOrgnameReqDTO.getOrgname();
        //加载
        OrgRespDTO afterOrgRespDTO = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        Org afterOrg = AutoMapUtils.map(afterOrgRespDTO, Org.class);
        if (afterOrg == null) {
            return new Result<>(ResultCodeEnum.ORG_NOT_EXISTS);
        }
        //初始化
        afterOrg.setStatus(OrgStatusEnum.DISABLE.getCode());
        //更新到数据库
        orgService.update(afterOrg);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("Org", "User", "UserOrg", "OrgRole");

        return new Result<>(ResultCodeEnum.OK);
    }

}