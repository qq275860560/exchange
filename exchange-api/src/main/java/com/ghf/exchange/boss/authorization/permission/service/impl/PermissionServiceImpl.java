package com.ghf.exchange.boss.authorization.permission.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.permission.dto.*;
import com.ghf.exchange.boss.authorization.permission.entity.Permission;
import com.ghf.exchange.boss.authorization.permission.entity.QPermission;
import com.ghf.exchange.boss.authorization.permission.enums.PermissionStatusEnum;
import com.ghf.exchange.boss.authorization.permission.event.UpdateFullPermissionEvent;
import com.ghf.exchange.boss.authorization.permission.repository.PermissionRepository;
import com.ghf.exchange.boss.authorization.permission.service.PermissionService;
import com.ghf.exchange.boss.authorization.rolepermission.dto.ListPermissionByRequestUrlReqDTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class PermissionServiceImpl extends BaseServiceImpl<Permission, Long> implements PermissionService {

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

    public PermissionServiceImpl(PermissionRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Permission", key = "'pagePermission:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(#p0.permissionType) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.permissionname)  && T(org.springframework.util.StringUtils).isEmpty(#p0.permissiondesc)  && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<PermissionRespDTO>> pagePermission(PagePermissionReqDTO pagePermissionReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pagePermissionReqDTO.getPermissionname())) {
            predicate.and(QPermission.permission.permissionname.contains(pagePermissionReqDTO.getPermissionname()));
        }
        if (!ObjectUtils.isEmpty(pagePermissionReqDTO.getPermissiondesc())) {
            predicate.and(QPermission.permission.permissiondesc.contains(pagePermissionReqDTO.getPermissiondesc()));
        }
        if (pagePermissionReqDTO.getPermissionType() != 0) {
            predicate.and(QPermission.permission.permissionType.eq(pagePermissionReqDTO.getPermissionType()));
        }
        PageRespDTO<PermissionRespDTO> pageRespDTO = permissionService.page(predicate, pagePermissionReqDTO, PermissionRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Permission", key = "'treePermission:'.concat(#p0.permissionname).concat(#p0.treeDeep)")
    @Override
    @SneakyThrows
    public Result<PermissionRespDTO> treePermission(TreePermissionReqDTO treePermissionReqDTO) {
        //根节点
        PermissionRespDTO permissionRespDTO = null;
        if (ObjectUtils.isEmpty(treePermissionReqDTO.getPermissionname())) {
            Predicate predicate = QPermission.permission.parentPermissionname.isEmpty().or(QPermission.permission.parentPermissionname.isNull());
            //有可能多个，此时此刻只选取第一个
            permissionRespDTO = this.list(predicate, PermissionRespDTO.class).get(0);

        } else {
            GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
            getPermissionByPermissionnameReqDTO.setPermissionname(treePermissionReqDTO.getPermissionname());
            permissionRespDTO = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        }

        BooleanBuilder predicate = new BooleanBuilder();
        //此接口只能获取启用状态的
        predicate.and(QPermission.permission.status.eq(PermissionStatusEnum.ENABLE.getCode()));
        if (treePermissionReqDTO.getTreeDeep() != 0) {
            predicate.and(QPermission.permission.deep.goe(permissionRespDTO.getDeep())
                    .and(QPermission.permission.deep.lt(permissionRespDTO.getDeep() + treePermissionReqDTO.getTreeDeep())));
        }
        predicate.and(QPermission.permission.fullPermissionname.contains("," + permissionRespDTO.getPermissionname() + ","));

        List<PermissionRespDTO> originList = permissionService.list(predicate, PermissionRespDTO.class);

        //遍历，找到二级节点
        List<PermissionRespDTO> child = getPermissionChildren(originList, permissionRespDTO.getPermissionname());
        permissionRespDTO.setChildren(child);

        return new Result<>(permissionRespDTO);
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

    @Cacheable(cacheNames = "Permission", key = "'listAncestorByPermissionname:'+#p0.permissionname")
    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listAncestorByPermissionname(ListAncestorByPermissionnameReqDTO listAncestorByPermissionnameReqDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        //此接口只能获取启用状态的
        predicate.and(QPermission.permission.status.eq(PermissionStatusEnum.ENABLE.getCode()));
        List<PermissionRespDTO> originList = permissionService.list(predicate, PermissionRespDTO.class);
        List<PermissionRespDTO> nodes = new ArrayList<>();

        //找到自己
        PermissionRespDTO currentPermissionRespDTO = null;
        Iterator<PermissionRespDTO> it0 = originList.iterator();
        while (it0.hasNext()) {
            PermissionRespDTO permissionRespDTO = it0.next();
            if (permissionRespDTO.getPermissionname().equals(listAncestorByPermissionnameReqDTO.getPermissionname())) {

                nodes.add(0, permissionRespDTO);
                currentPermissionRespDTO = permissionRespDTO;
                it0.remove();

                //如果输入不为空,叶子节点是自己，找到直接退出
                break;
            }
        }

        while (currentPermissionRespDTO != null && !ObjectUtils.isEmpty(currentPermissionRespDTO.getParentPermissionname())) {
            //说明此时理论上有父级节点
            boolean flag = false;
            Iterator<PermissionRespDTO> it = originList.iterator();
            while (it.hasNext()) {
                PermissionRespDTO next = it.next();
                if (currentPermissionRespDTO.getParentPermissionname().equals(next.getPermissionname())) {

                    nodes.add(0, next);
                    currentPermissionRespDTO = next;
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

    @Cacheable(cacheNames = "Permission", key = "'getPermissionByPermissionname:'+#p0.permissionname")
    @Override
    @SneakyThrows
    public Result<PermissionRespDTO> getPermissionByPermissionname(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        //TODO 权限判断
        String permissionname = getPermissionByPermissionnameReqDTO.getPermissionname();
        Predicate predicate = QPermission.permission.permissionname.eq(permissionname);
        Permission permission = permissionService.get(predicate);
        //返回
        PermissionRespDTO permissionRespDTO = AutoMapUtils.map(permission, PermissionRespDTO.class);
        return new Result<>(permissionRespDTO);
    }

    @Cacheable(cacheNames = "Permission", key = "'listPermissionByRequestUrl:'+#p0.requestUrl")
    @Override
    @SneakyThrows
    public Result<List<PermissionRespDTO>> listPermissionByRequestUrl(ListPermissionByRequestUrlReqDTO listPermissionByRequestUrlReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        String requestUrl = listPermissionByRequestUrlReqDTO.getRequestUrl();
        predicate.and(QPermission.permission.requestUrl.eq(requestUrl));
        List<PermissionRespDTO> list = permissionService.list(predicate, PermissionRespDTO.class);
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsPermissionByPermissionname(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        //TODO 权限判断
        String permissionname = getPermissionByPermissionnameReqDTO.getPermissionname();
        Predicate predicate = QPermission.permission.permissionname.eq(permissionname);
        boolean b = permissionService.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Permission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addPermission(AddPermissionReqDTO addPermissionReqDTO) {
        Permission permission = AutoMapUtils.map(addPermissionReqDTO, Permission.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        permission.setCreateUserId(currentLoginUser.getId());
        permission.setCreateUserName(currentLoginUser.getUsername());

        permission.setCreateTime(new Date());
        //判断唯一性
        String permissionname = permission.getPermissionname();
        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO.setPermissionname(permissionname);
        boolean b = permissionService.existsPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.ORG_EXISTS);
        }
        //初始化id
        permission.setId(IdUtil.generateLongId());
        permission.setStatus(PermissionStatusEnum.ENABLE.getCode());

        //初始化父节点
        if (!ObjectUtils.isEmpty(permission.getParentPermissionname())) {
            GetPermissionByPermissionnameReqDTO getParentPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
            getParentPermissionByPermissionnameReqDTO.setPermissionname(permission.getParentPermissionname());

            PermissionRespDTO parentPermissionRespDTO = permissionService.getPermissionByPermissionname(getParentPermissionByPermissionnameReqDTO).getData();
            Permission parentPermission = AutoMapUtils.map(parentPermissionRespDTO, Permission.class);
            permission.setParentPermissionId(parentPermission.getId());
            permission.setParentPermissionname(parentPermission.getPermissionname());
            permission.setParentPermissiondesc(parentPermission.getPermissiondesc());
        } else {
            permission.setParentPermissionId(0);
            permission.setParentPermissionname(null);
            permission.setParentPermissiondesc(null);
        }
        //新增到数据库
        permissionService.add(permission);

        //更新自身及其后代的完整权限信息
        applicationEventPublisher.publishEvent(new UpdateFullPermissionEvent(getPermissionByPermissionnameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Permission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updatePermissionByPermissionname(UpdatePermissionByPermissionnameReqDTO updatePermissionByPermissionnameReqDTO) {

        String permissionname = updatePermissionByPermissionnameReqDTO.getPermissionname();
        String permissiondesc = updatePermissionByPermissionnameReqDTO.getPermissiondesc();
        Integer permissionType = updatePermissionByPermissionnameReqDTO.getPermissionType();
        int orderNum = updatePermissionByPermissionnameReqDTO.getOrderNum();
        String icon = updatePermissionByPermissionnameReqDTO.getIcon();
        String pageUrl = updatePermissionByPermissionnameReqDTO.getPageUrl();
        String componentUrl = updatePermissionByPermissionnameReqDTO.getComponentUrl();
        String requestUrl = updatePermissionByPermissionnameReqDTO.getRequestUrl();
        String parentPermissionname = updatePermissionByPermissionnameReqDTO.getParentPermissionname();
        int display = updatePermissionByPermissionnameReqDTO.getDisplay();

        //加载
        GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
        getPermissionByPermissionnameReqDTO.setPermissionname(permissionname);
        PermissionRespDTO afterPermissionRespDTO = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        Permission afterPermission = AutoMapUtils.map(afterPermissionRespDTO, Permission.class);
        if (afterPermission == null) {
            return new Result<>(ResultCodeEnum.ORG_NOT_EXISTS);
        }
        //初始化
        afterPermission.setPermissionname(permissionname);
        afterPermission.setPermissiondesc(permissiondesc);
        afterPermission.setPermissionType(permissionType);
        afterPermission.setOrderNum(orderNum);
        afterPermission.setIcon(icon);
        afterPermission.setPageUrl(pageUrl);
        afterPermission.setComponentUrl(componentUrl);
        afterPermission.setRequestUrl(requestUrl);
        //初始化父节点
        if (!ObjectUtils.isEmpty(parentPermissionname)) {
            GetPermissionByPermissionnameReqDTO getParentPermissionByPermissionnameReqDTO = new GetPermissionByPermissionnameReqDTO();
            getParentPermissionByPermissionnameReqDTO.setPermissionname(parentPermissionname);
            PermissionRespDTO parentPermissionRespDTO = permissionService.getPermissionByPermissionname(getParentPermissionByPermissionnameReqDTO).getData();
            Permission parentPermission = AutoMapUtils.map(parentPermissionRespDTO, Permission.class);
            afterPermission.setParentPermissionId(parentPermission.getId());
            afterPermission.setParentPermissionname(parentPermission.getPermissionname());
            afterPermission.setParentPermissiondesc(parentPermission.getPermissiondesc());
        } else {
            afterPermission.setParentPermissionId(0);
            afterPermission.setParentPermissionname(null);
            afterPermission.setParentPermissiondesc(null);
        }

        afterPermission.setDisplay(display);
        //更新到数据库
        permissionService.update(afterPermission);

        //更新自身及其后代的完整权限信息
        applicationEventPublisher.publishEvent(new UpdateFullPermissionEvent(getPermissionByPermissionnameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Permission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enablePermission(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        String permissionname = getPermissionByPermissionnameReqDTO.getPermissionname();
        //加载
        PermissionRespDTO afterPermissionRespDTO = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        Permission afterPermission = AutoMapUtils.map(afterPermissionRespDTO, Permission.class);
        if (afterPermission == null) {
            return new Result<>(ResultCodeEnum.ORG_NOT_EXISTS);
        }
        //初始化
        afterPermission.setStatus(PermissionStatusEnum.ENABLE.getCode());
        //更新到数据库
        permissionService.update(afterPermission);

        //更新自身及其后代的完整权限信息
        applicationEventPublisher.publishEvent(new UpdateFullPermissionEvent(getPermissionByPermissionnameReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Permission", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disablePermission(GetPermissionByPermissionnameReqDTO getPermissionByPermissionnameReqDTO) {
        String permissionname = getPermissionByPermissionnameReqDTO.getPermissionname();
        //加载
        PermissionRespDTO afterPermissionRespDTO = permissionService.getPermissionByPermissionname(getPermissionByPermissionnameReqDTO).getData();
        Permission afterPermission = AutoMapUtils.map(afterPermissionRespDTO, Permission.class);
        if (afterPermission == null) {
            return new Result<>(ResultCodeEnum.ORG_NOT_EXISTS);
        }
        //初始化
        afterPermission.setStatus(PermissionStatusEnum.DISABLE.getCode());
        //更新到数据库
        permissionService.update(afterPermission);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("Permission", "RolePermission");

        return new Result<>(ResultCodeEnum.OK);
    }

}