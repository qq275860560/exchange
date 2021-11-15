package com.ghf.exchange.boss.authorization.userorg.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.GetUserByUsernameReqDTO;
import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.GetOrgByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.org.dto.ListAncestorByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.org.dto.TreeOrgReqDTO;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.userorg.dto.*;
import com.ghf.exchange.boss.authorization.userorg.entity.QUserOrg;
import com.ghf.exchange.boss.authorization.userorg.entity.UserOrg;
import com.ghf.exchange.boss.authorization.userorg.enums.UserOrgStatusEnum;
import com.ghf.exchange.boss.authorization.userorg.event.UpdateUserOrgEvent;
import com.ghf.exchange.boss.authorization.userorg.repository.UserOrgRepository;
import com.ghf.exchange.boss.authorization.userorg.service.UserOrgService;
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
public class UserOrgServiceImpl extends BaseServiceImpl<UserOrg, Long> implements UserOrgService {
    @Lazy
    @Resource
    private OrgService orgService;
    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private UserOrgService userOrgService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    public UserOrgServiceImpl(UserOrgRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "UserOrg", key = "'pageOrg:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.username) && T(org.springframework.util.StringUtils).isEmpty(#p0.orgname) && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<UserOrgRespDTO>> pageUserOrg(PageUserOrgReqDTO pageUserOrgReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageUserOrgReqDTO.getUsername())) {
            predicate.and(QUserOrg.userOrg.username.contains(pageUserOrgReqDTO.getUsername()));
        }
        if (!ObjectUtils.isEmpty(pageUserOrgReqDTO.getOrgname())) {
            predicate.and(QUserOrg.userOrg.orgname.contains(pageUserOrgReqDTO.getOrgname()));
        }
        PageRespDTO<UserOrgRespDTO> pageResult = userOrgService.page(predicate, pageUserOrgReqDTO, UserOrgRespDTO.class);
        return new Result<>(pageResult);
    }

    @Cacheable(cacheNames = "UserOrg", key = "'listUserOrgByUsername:'+#p0.username")
    @Override
    @SneakyThrows
    public Result<List<UserOrgRespDTO>> listUserOrgByUsername(ListUserOrgByUsernameReqDTO listUserOrgByUsernameReqDTO) {
        String username = listUserOrgByUsernameReqDTO.getUsername();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserOrg.userOrg.username.eq(username));
        //此接口只能获取启用状态的
        predicate.and(QUserOrg.userOrg.status.eq(UserOrgStatusEnum.ENABLE.getCode()));
        List<UserOrgRespDTO> list = userOrgService.list(predicate, UserOrgRespDTO.class);
        return new Result<>(list);
    }

    @Cacheable(cacheNames = "UserOrg", key = "'listUserOrgByOrgname:'+#p0.orgname")
    @Override
    @SneakyThrows
    public Result<List<UserOrgRespDTO>> listUserOrgByOrgname(ListUserOrgByOrgnameReqDTO listUserOrgByOrgnameReqDTO) {
        String orgname = listUserOrgByOrgnameReqDTO.getOrgname();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserOrg.userOrg.orgname.eq(orgname));
        //此接口只能获取启用状态的
        predicate.and(QUserOrg.userOrg.status.eq(UserOrgStatusEnum.ENABLE.getCode()));
        List<UserOrgRespDTO> list = userOrgService.list(predicate, UserOrgRespDTO.class);
        return new Result<>(list);
    }

    @SneakyThrows
    private Result<UserOrg> getUserOrgByUsernameAndOrgname(String username, String orgname) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserOrg.userOrg.username.eq(username));
        predicate.and(QUserOrg.userOrg.orgname.eq(orgname));
        UserOrg userOrg = userOrgService.get(predicate);
        return new Result<>(userOrg);
    }

    @Cacheable(cacheNames = "UserOrg", key = "'getUserOrgByUsernameAndOrgname:'.concat(#p0.username).concat(':').concat(#p0.orgname)")
    @Override
    @SneakyThrows
    public Result<UserOrgRespDTO> getUserOrgByUsernameAndOrgname(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        //TODO 权限判断
        String username = getUserOrgByUsernameAndOrgnameReqDTO.getUsername();
        String orgname = getUserOrgByUsernameAndOrgnameReqDTO.getOrgname();
        UserOrg userOrg = this.getUserOrgByUsernameAndOrgname(username, orgname).getData();
        //返回
        UserOrgRespDTO userOrgRespDTO = AutoMapUtils.map(userOrg, UserOrgRespDTO.class);
        return new Result<>(userOrgRespDTO);
    }

    @SneakyThrows
    private Result<Boolean> existsUserOrgByUsernameAndOrgname(String username, String orgname) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QUserOrg.userOrg.username.eq(username));
        predicate.and(QUserOrg.userOrg.orgname.eq(orgname));
        boolean b = userOrgService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsUserOrgByUsernameAndOrgname(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        //TODO 权限判断
        String username = getUserOrgByUsernameAndOrgnameReqDTO.getUsername();
        String orgname = getUserOrgByUsernameAndOrgnameReqDTO.getOrgname();
        return this.existsUserOrgByUsernameAndOrgname(username, orgname);
    }

    @CacheEvict(cacheNames = "UserOrg", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addUserOrg(AddUserOrgReqDTO addUserOrgReqDTO) {
        UserOrg userOrg = AutoMapUtils.map(addUserOrgReqDTO, UserOrg.class);
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        userOrg.setCreateUserId(currentLoginUser.getId());
        userOrg.setCreateUserName(currentLoginUser.getUsername());

        userOrg.setCreateTime(new Date());
        //判断唯一性
        String username = userOrg.getUsername();
        String orgname = userOrg.getOrgname();

        GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO = new GetUserOrgByUsernameAndOrgnameReqDTO();
        getUserOrgByUsernameAndOrgnameReqDTO.setUsername(username);
        getUserOrgByUsernameAndOrgnameReqDTO.setOrgname(orgname);

        boolean b = userOrgService.existsUserOrgByUsernameAndOrgname(getUserOrgByUsernameAndOrgnameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_ORG_EXISTS);
        }
        //初始化id
        userOrg.setId(IdUtil.generateLongId());
        userOrg.setStatus(UserOrgStatusEnum.ENABLE.getCode());
        //新增到数据库
        userOrgService.add(userOrg);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserOrgEvent(addUserOrgReqDTO.getUsername()));
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "UserOrg", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enableUserOrg(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        String username = getUserOrgByUsernameAndOrgnameReqDTO.getUsername();
        //加载
        UserOrgRespDTO afterUserOrgRespDTO = userOrgService.getUserOrgByUsernameAndOrgname(getUserOrgByUsernameAndOrgnameReqDTO).getData();
        UserOrg afterUserOrg = AutoMapUtils.map(afterUserOrgRespDTO, UserOrg.class);
        //初始化
        afterUserOrg.setStatus(UserOrgStatusEnum.ENABLE.getCode());
        //更新到数据库
        userOrgService.update(afterUserOrg);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserOrgEvent(getUserOrgByUsernameAndOrgnameReqDTO.getUsername()));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "UserOrg", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableUserOrg(GetUserOrgByUsernameAndOrgnameReqDTO getUserOrgByUsernameAndOrgnameReqDTO) {
        String username = getUserOrgByUsernameAndOrgnameReqDTO.getUsername();

        //加载
        UserOrgRespDTO afterUserOrgRespDTO = userOrgService.getUserOrgByUsernameAndOrgname(getUserOrgByUsernameAndOrgnameReqDTO).getData();
        UserOrg afterUserOrg = AutoMapUtils.map(afterUserOrgRespDTO, UserOrg.class);
        if (afterUserOrg == null) {
            return new Result<>(ResultCodeEnum.USER_ORG_NOT_EXISTS);
        }
        //初始化
        afterUserOrg.setStatus(UserOrgStatusEnum.DISABLE.getCode());
        //更新到数据库
        userOrgService.update(afterUserOrg);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserOrgEvent(getUserOrgByUsernameAndOrgnameReqDTO.getUsername()));
        return new Result<>(ResultCodeEnum.OK);
    }

    @Override
    @SneakyThrows
    public Result<List<OrgRespDTO>> listOrgByUsername(ListOrgByUsernameReqDTO listOrgByUsernameReqDTO) {
        String username = listOrgByUsernameReqDTO.getUsername();
        ListUserOrgByUsernameReqDTO listUserOrgByUsernameReqDTO = new ListUserOrgByUsernameReqDTO();
        listUserOrgByUsernameReqDTO.setUsername(username);
        List<UserOrgRespDTO> list = userOrgService.listUserOrgByUsername(listUserOrgByUsernameReqDTO).getData();
        List<OrgRespDTO> resultList = list.stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> {
            String orgname = e.getOrgname();
            GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
            getOrgByOrgnameReqDTO.setOrgname(orgname);
            return orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        }).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());
        return new Result<>(resultList);

    }

    @Override
    @SneakyThrows
    public Result<List<UserRespDTO>> listUserByOrgname(ListUserByOrgnameReqDTO listUserByOrgnameReqDTO) {
        String orgname = listUserByOrgnameReqDTO.getOrgname();
        ListUserOrgByOrgnameReqDTO listUserOrgByOrgnameReqDTO = new ListUserOrgByOrgnameReqDTO();
        listUserOrgByOrgnameReqDTO.setOrgname(orgname);
        List<UserOrgRespDTO> list = userOrgService.listUserOrgByOrgname(listUserOrgByOrgnameReqDTO).getData();
        List<UserRespDTO> resultList = list.stream().map(e -> {
            String username = e.getUsername();
            GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
            getUserByUsernameReqDTO.setUsername(username);
            return userService.getUserByUsername(getUserByUsernameReqDTO).getData();
        }).collect(Collectors.toList());
        return new Result<>(resultList);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existUserOrgOrUserAncestorOrgByUserAndOrgname(ExistUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO existUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO) {
        //加载组织及其祖先
        String orgname = existUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO.getOrgname();
        ListAncestorByOrgnameReqDTO listAncestorByOrgnameReqDTO = new ListAncestorByOrgnameReqDTO();
        listAncestorByOrgnameReqDTO.setOrgname(orgname);
        List<OrgRespDTO> orgRespDTOList = orgService.listAncestorByOrgname(listAncestorByOrgnameReqDTO).getData();

        //加载用户在哪些组织
        String username = existUserOrgOrUserAncestorOrgByUserAndOrgnameReqDTO.getUsername();
        ListUserOrgByUsernameReqDTO listUserOrgByUsernameReqDTO = new ListUserOrgByUsernameReqDTO();
        listUserOrgByUsernameReqDTO.setUsername(username);
        List<UserOrgRespDTO> userOrgRespDTOList = userOrgService.listUserOrgByUsername(listUserOrgByUsernameReqDTO).getData();

        //遍历匹配
        for (OrgRespDTO orgRespDTO : orgRespDTOList) {
            for (UserOrgRespDTO userOrgRespDTO : userOrgRespDTOList) {
                if (orgRespDTO.getOrgname().equals(userOrgRespDTO.getOrgname())) {
                    return new Result<>(true);
                }
            }
        }
        return new Result<>(false);

    }

    @Override
    @SneakyThrows
    public Result<Boolean> existUserOrgOrUserDescendantOrgByUserAndOrgname(ExistUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO) {
        //加载组织及其后代
        String orgname = existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO.getOrgname();
        TreeOrgReqDTO treeOrgReqDTO = new TreeOrgReqDTO();
        treeOrgReqDTO.setOrgname(orgname);
        OrgRespDTO orgRespDTO = orgService.treeOrg(treeOrgReqDTO).getData();

        //加载用户在哪些组织
        String username = existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO.getUsername();
        ListUserOrgByUsernameReqDTO listUserOrgByUsernameReqDTO = new ListUserOrgByUsernameReqDTO();
        listUserOrgByUsernameReqDTO.setUsername(username);
        List<UserOrgRespDTO> userOrgRespDTOList = userOrgService.listUserOrgByUsername(listUserOrgByUsernameReqDTO).getData();

        //遍历匹配

        for (UserOrgRespDTO userOrgRespDTO : userOrgRespDTOList) {
            if (orgRespDTO.getOrgname().equals(userOrgRespDTO.getOrgname())) {
                return new Result<>(true);
            }
        }
        //遍历后代
        List<OrgRespDTO> children = orgRespDTO.getChildren();
        for (OrgRespDTO child : children) {
            ExistUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO1 = new ExistUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO();
            existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO1.setOrgname(child.getOrgname());
            existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO1.setUsername(username);
            boolean b = this.existUserOrgOrUserDescendantOrgByUserAndOrgname(existUserOrgOrUserDescendantOrgByUserAndOrgnameReqDTO1).getData();
            if (b) {
                return new Result<>(true);
            }
        }

        return new Result<>(false);

    }

    /**
     * 根据组织名列表获取所有用户
     *
     * @param orgNames 组织名列表
     * @return
     */
    @Override
    public Result<List<Long>> listUserOrgByOrgNames(List<String> orgNames) {
        List<Long> result = orgNames.stream()
                .flatMap(e -> {
                    //此接口有缓存，速度较快
                    List<UserRespDTO> list = userOrgService.listUserByOrgname(ListUserByOrgnameReqDTO.builder().orgname(e).build()).getData();
                    return list.stream();
                })
                .map(e -> e.getId())
                .distinct()
                .collect(Collectors.toList());

        return new Result<>(result);
    }

}