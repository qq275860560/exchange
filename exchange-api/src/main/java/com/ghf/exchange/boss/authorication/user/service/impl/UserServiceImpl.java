package com.ghf.exchange.boss.authorication.user.service.impl;

import com.ghf.exchange.boss.authorication.client.enums.ClientGrantTypeEnum;
import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.dto.*;
import com.ghf.exchange.boss.authorication.user.entity.QUser;
import com.ghf.exchange.boss.authorication.user.entity.User;
import com.ghf.exchange.boss.authorication.user.enums.UsersStatusEnum;
import com.ghf.exchange.boss.authorication.user.event.AddUserEvent;
import com.ghf.exchange.boss.authorication.user.event.UpdateUserLastLoginTimeAndLastLoginIpEvent;
import com.ghf.exchange.boss.authorication.user.repository.UserRepository;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorication.user.util.IpUtil;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
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
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
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
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

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
    private ClientService clientService;
    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }

    //只缓存第一页，并且有一个排序条件时的第一页

    @Cacheable(cacheNames = "User", key = "'pageUser:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.username).concat(':').concat(#p0.orgname).concat(':').concat(#p0.rolename) ", condition = " T(org.springframework.util.StringUtils).isEmpty(#p0.nickname)  && T(org.springframework.util.StringUtils).isEmpty(#p0.orgdesc)  && T(org.springframework.util.StringUtils).isEmpty(#p0.roledesc)  && #p0.sort!=null && #p0.sort.size()==1 ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<UserRespDTO>> pageUser(PageUserReqDTO pageUserReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageUserReqDTO.getUsername())) {
            predicate.and(QUser.user.username.contains(pageUserReqDTO.getUsername()));
        }
        if (!ObjectUtils.isEmpty(pageUserReqDTO.getNickname())) {
            predicate.and(QUser.user.nickname.contains(pageUserReqDTO.getNickname()));
        }
        if (!ObjectUtils.isEmpty(pageUserReqDTO.getOrgname())) {
            predicate.and(QUser.user.orgnames.contains("," + pageUserReqDTO.getOrgname() + ","));
        }
        if (!ObjectUtils.isEmpty(pageUserReqDTO.getRolename())) {
            predicate.and(QUser.user.rolenames.contains("," + pageUserReqDTO.getRolename() + ","));
        }
        PageRespDTO<UserRespDTO> pageRespDTO = userService.page(predicate, pageUserReqDTO, UserRespDTO.class);
        pageRespDTO.getList().forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getRolenames())) {
                e.setRolenameSet(Arrays.stream(e.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getRoledescs())) {
                e.setRoledescSet(Arrays.stream(e.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getOrgnames())) {
                e.setOrgnameSet(Arrays.stream(e.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getOrgdescs())) {
                e.setOrgdescSet(Arrays.stream(e.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
        });
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "User", key = "'listUser:'.concat(':').concat(#p0.username).concat(':').concat(#p0.orgname).concat(':').concat(#p0.rolename)", condition = " T(org.springframework.util.StringUtils).isEmpty(#p0.nickname)  && T(org.springframework.util.StringUtils).isEmpty(#p0.orgdesc)  && T(org.springframework.util.StringUtils).isEmpty(#p0.roledesc) ")
    @Override
    public Result<List<UserRespDTO>> listUser(ListUserReqDTO listUserReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listUserReqDTO.getUsername())) {
            predicate.and(QUser.user.username.contains(listUserReqDTO.getUsername()));
        }
        if (!ObjectUtils.isEmpty(listUserReqDTO.getNickname())) {
            predicate.and(QUser.user.nickname.contains(listUserReqDTO.getNickname()));
        }
        if (!ObjectUtils.isEmpty(listUserReqDTO.getOrgname())) {
            predicate.and(QUser.user.orgnames.contains("," + listUserReqDTO.getOrgname() + ","));
        }
        if (!ObjectUtils.isEmpty(listUserReqDTO.getRolename())) {
            predicate.and(QUser.user.rolenames.contains("," + listUserReqDTO.getRolename() + ","));
        }
        //此接口只能获取启用状态的
        predicate.and(QUser.user.status.eq(UsersStatusEnum.ENABLE.getCode()));
        List<UserRespDTO> list = userService.list(predicate, UserRespDTO.class);
        list.forEach(e -> {
            if (!ObjectUtils.isEmpty(e.getRolenames())) {
                e.setRolenameSet(Arrays.stream(e.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getRoledescs())) {
                e.setRoledescSet(Arrays.stream(e.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getOrgnames())) {
                e.setOrgnameSet(Arrays.stream(e.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
            if (!ObjectUtils.isEmpty(e.getOrgdescs())) {
                e.setOrgdescSet(Arrays.stream(e.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
            }
        });
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<UserRespDTO> getCurrentLoginUser() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (oAuth2Authentication == null) {
            return new Result<>(UserRespDTO.builder().build());
        }
        if (oAuth2Authentication.getUserAuthentication() == null) {
            return new Result<>(UserRespDTO.builder().build());
        }
        if (oAuth2Authentication.getUserAuthentication().getPrincipal() == null) {
            return new Result<>(UserRespDTO.builder().build());
        }
        //获取登陆账号
        String username = ((org.springframework.security.core.userdetails.User) oAuth2Authentication.getUserAuthentication().getPrincipal()).getUsername();

        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        //返回
        UserRespDTO userRespDTO = userService.getUserByUsername(getUserByUsernameReqDTO).getData();

        if (!ObjectUtils.isEmpty(userRespDTO.getRolenames())) {
            userRespDTO.setRolenameSet(Arrays.stream(userRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getRoledescs())) {
            userRespDTO.setRoledescSet(Arrays.stream(userRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgnames())) {
            userRespDTO.setOrgnameSet(Arrays.stream(userRespDTO.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgdescs())) {
            userRespDTO.setOrgdescSet(Arrays.stream(userRespDTO.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        return new Result<>(userRespDTO);

    }

    @Cacheable(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    @Override
    @SneakyThrows
    public Result<UserRespDTO> getUserByUsername(GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        //TODO 权限判断
        String username = getUserByUsernameReqDTO.getUsername();
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);
        if (null == user) {
            return new Result<>();
        }
        //返回
        UserRespDTO userRespDTO = ModelMapperUtil.map(user, UserRespDTO.class);

        if (!ObjectUtils.isEmpty(userRespDTO.getRolenames())) {
            userRespDTO.setRolenameSet(Arrays.stream(userRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getRoledescs())) {
            userRespDTO.setRoledescSet(Arrays.stream(userRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgnames())) {
            userRespDTO.setOrgnameSet(Arrays.stream(userRespDTO.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgdescs())) {
            userRespDTO.setOrgdescSet(Arrays.stream(userRespDTO.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        return new Result<>(userRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<UserRespDTO> getUserByEmail(GetUserByEmailReqDTO getUserByEmailReqDTO) {
        //TODO 权限判断
        String email = getUserByEmailReqDTO.getEmail();
        Predicate predicate = QUser.user.email.eq(email);
        User user = this.get(predicate);
        //返回
        UserRespDTO userRespDTO = ModelMapperUtil.map(user, UserRespDTO.class);

        if (!ObjectUtils.isEmpty(userRespDTO.getRolenames())) {
            userRespDTO.setRolenameSet(Arrays.stream(userRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getRoledescs())) {
            userRespDTO.setRoledescSet(Arrays.stream(userRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgnames())) {
            userRespDTO.setOrgnameSet(Arrays.stream(userRespDTO.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgdescs())) {
            userRespDTO.setOrgdescSet(Arrays.stream(userRespDTO.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        return new Result<>(userRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<UserRespDTO> getUserByMobile(GetUserByMobileReqDTO getUserByMobileReqDTO) {
        //TODO 权限判断
        String mobile = getUserByMobileReqDTO.getMobile();
        Predicate predicate = QUser.user.mobile.eq(mobile);
        User user = this.get(predicate);
        //返回
        UserRespDTO userRespDTO = ModelMapperUtil.map(user, UserRespDTO.class);

        if (!ObjectUtils.isEmpty(userRespDTO.getRolenames())) {
            userRespDTO.setRolenameSet(Arrays.stream(userRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getRoledescs())) {
            userRespDTO.setRoledescSet(Arrays.stream(userRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgnames())) {
            userRespDTO.setOrgnameSet(Arrays.stream(userRespDTO.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(userRespDTO.getOrgdescs())) {
            userRespDTO.setOrgdescSet(Arrays.stream(userRespDTO.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        return new Result<>(userRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsUserByUsername(GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        //TODO 权限判断
        String username = getUserByUsernameReqDTO.getUsername();
        Predicate predicate = QUser.user.username.eq(username);
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsUserByEmail(ExistsUserByEmailReqDTO existsUserByEmailReqDTO) {
        //TODO 权限判断
        String username = existsUserByEmailReqDTO.getUsername();
        String email = existsUserByEmailReqDTO.getEmail();
        if (ObjectUtils.isEmpty(email)) {
            return new Result<>(false);
        }
        Predicate predicate = QUser.user.email.eq(email).and(QUser.user.username.ne(username));
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsUserByMobile(ExistsUserByMobileReqDTO existsUserByMobileReqDTO) {
        //TODO 权限判断
        String username = existsUserByMobileReqDTO.getUsername();
        String mobile = existsUserByMobileReqDTO.getMobile();
        if (ObjectUtils.isEmpty(mobile)) {
            return new Result<>(false);
        }
        Predicate predicate = QUser.user.mobile.eq(mobile).and(QUser.user.username.ne(username));
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    /**
     * app客户端id
     */
    private static final String APP_CLIENT_ID = "app";

    /**
     * 浏览器客户端id
     */
    private static final String BROWSER_CLIENT_ID = "browser";

    @Override
    public Result<LoginRespDTO> login(LoginReqDTO loginReqDTO) {

        //TODO 校验验证码

        Map<String, String> requestParameters = new HashMap<String, String>(16) {{
            //授权类型,此接口默认密码模式登录
            put(OAuth2Utils.GRANT_TYPE, ClientGrantTypeEnum.PASSWORD.getCode());
            //客户端id，此接口默认使用app登录
            put(OAuth2Utils.CLIENT_ID, APP_CLIENT_ID);
        }};

        String clientId = requestParameters.get(OAuth2Utils.CLIENT_ID);
        String grantType = requestParameters.get(OAuth2Utils.GRANT_TYPE);
        ClientDetails clientDetails = clientService.getClientDetailsByClientId(clientId);
        Set<String> scopes = clientDetails.getScope();
        TokenRequest tokenRequest = new TokenRequest(requestParameters, clientId, scopes, grantType);

        String username = loginReqDTO.getUsername();
        String password = loginReqDTO.getPassword();

        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        if (user == null) {
            return new Result<>(ResultCodeEnum.USER_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new Result<>(ResultCodeEnum.PASSWORD_ERROR);
        }
        if (user.getStatus() == UsersStatusEnum.DISABLE.getCode()) {
            return new Result<>(ResultCodeEnum.USER_STATUS_DISABLE);
        }

        //参考以下实现org.springframework.security.oauth2.provider.password.ResourceBusinessPasswordTokenGranter.getOAuth2Authentication

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);

        ((AbstractAuthenticationToken) userAuth).setDetails(requestParameters);

        userAuth = authenticationManager.authenticate(userAuth);

        OAuth2Request storedOauth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(storedOauth2Request, userAuth);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        OAuth2AccessToken oAuth2AccessToken = jwtAccessTokenConverter.enhance(token, oAuth2Authentication);

        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);

        //发送到消息队列
        UpdateUserLastLoginTimeAndLastLoginIpReqDTO updateUserLastLoginTimeAndLastLoginIpDto = new UpdateUserLastLoginTimeAndLastLoginIpReqDTO();
        updateUserLastLoginTimeAndLastLoginIpDto.setUsername(username);
        updateUserLastLoginTimeAndLastLoginIpDto.setLastLoginTime(new Date());
        updateUserLastLoginTimeAndLastLoginIpDto.setLastLoginIp(IpUtil.getIpAddr());
        applicationEventPublisher.publishEvent(new UpdateUserLastLoginTimeAndLastLoginIpEvent(updateUserLastLoginTimeAndLastLoginIpDto));

        //TODO封装返回对象，包括个人信息菜单按钮请求url权限
        LoginRespDTO loginRespDTO = ModelMapperUtil.map(user, LoginRespDTO.class);
        loginRespDTO.setAccessToken(oAuth2AccessToken.getValue());
        loginRespDTO.setTokenType(oAuth2AccessToken.getTokenType());

        //返回

        if (!ObjectUtils.isEmpty(loginRespDTO.getRolenames())) {
            loginRespDTO.setRolenameSet(Arrays.stream(loginRespDTO.getRolenames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(loginRespDTO.getRoledescs())) {
            loginRespDTO.setRoledescSet(Arrays.stream(loginRespDTO.getRoledescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(loginRespDTO.getOrgnames())) {
            loginRespDTO.setOrgnameSet(Arrays.stream(loginRespDTO.getOrgnames().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }
        if (!ObjectUtils.isEmpty(loginRespDTO.getOrgdescs())) {
            loginRespDTO.setOrgdescSet(Arrays.stream(loginRespDTO.getOrgdescs().split(",")).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toSet()));
        }

        return new Result<>(loginRespDTO);

    }

    @CacheEvict(cacheNames = "User", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> addUser(AddUserReqDTO addUserReqDTO) {
        User user = ModelMapperUtil.map(addUserReqDTO, User.class);
        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = this.getCurrentLoginUser().getData();
        user.setCreateUserId(currentLoginUser.getId());
        user.setCreateUserName(currentLoginUser.getUsername());

        user.setCreateTime(new Date());
        //判断唯一性
        String username = user.getUsername();
        GetUserByUsernameReqDTO getUserByUsernameReqDTO = new GetUserByUsernameReqDTO();
        getUserByUsernameReqDTO.setUsername(username);
        boolean b = userService.existsUserByUsername(getUserByUsernameReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_EXISTS);
        }
        String email = user.getEmail();
        ExistsUserByEmailReqDTO existsUserByEmailReqDTO = new ExistsUserByEmailReqDTO();
        existsUserByEmailReqDTO.setUsername("");
        existsUserByEmailReqDTO.setEmail(email);
        b = userService.existsUserByEmail(existsUserByEmailReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_EMAIL_EXISTS);
        }
        String mobile = user.getMobile();
        ExistsUserByMobileReqDTO existsUserByMobileReqDTO = new ExistsUserByMobileReqDTO();
        existsUserByMobileReqDTO.setUsername("");
        existsUserByMobileReqDTO.setMobile(mobile);
        b = userService.existsUserByMobile(existsUserByMobileReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_MOBILE_EXISTS);
        }

        //初始化id
        user.setId(IdUtil.generateLongId());
        //密码脱敏处理
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //用户默认设置为启用
        user.setStatus(UsersStatusEnum.ENABLE.getCode());
        //新增到数据库
        this.add(user);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new AddUserEvent(addUserReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "User", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateUserByUsername(UpdateUserByUsernameReqDTO updateUserReqDTO) {
        String username = updateUserReqDTO.getUsername();
        String nickname = updateUserReqDTO.getNickname();
        String mobile = updateUserReqDTO.getMobile();
        String email = updateUserReqDTO.getEmail();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);
        //初始化
        user.setNickname(nickname);

        ExistsUserByEmailReqDTO existsUserByEmailReqDTO = new ExistsUserByEmailReqDTO();
        existsUserByEmailReqDTO.setUsername(username);
        existsUserByEmailReqDTO.setEmail(email);
        boolean b = userService.existsUserByEmail(existsUserByEmailReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_EMAIL_EXISTS);
        }

        ExistsUserByMobileReqDTO existsUserByMobileReqDTO = new ExistsUserByMobileReqDTO();
        existsUserByMobileReqDTO.setUsername(username);
        existsUserByMobileReqDTO.setMobile(mobile);
        b = userService.existsUserByMobile(existsUserByMobileReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.USER_MOBILE_EXISTS);
        }

        user.setMobile(mobile);
        user.setEmail(email);
        //更新到数据库
        this.update(user);

        //发送到消息队列
        applicationEventPublisher.publishEvent(new UpdateUserByUsernameEvent(updateUserReqDTO));

        return new Result<>(ResultCodeEnum.OK);
    }

    @SneakyThrows
    private Result<Void> updateUserPasswordByUsername(String username, String password) {
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);
        //初始化
        user.setPassword(passwordEncoder.encode(password));
        //更新到数据库
        this.update(user);
        return new Result<>(ResultCodeEnum.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    })
    @Override
    @SneakyThrows
    public Result<Void> updateUserPasswordByUsername(UpdateUserPasswordByUsernameReqDTO updateUserPasswordByUsernameReqDTO) {
        String username = updateUserPasswordByUsernameReqDTO.getUsername();
        String password = updateUserPasswordByUsernameReqDTO.getPassword();
        return this.updateUserPasswordByUsername(username, password);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    })
    @Override
    @SneakyThrows
    public Result<Void> updateUserPasswordByUsernameAndOldPassword(UpdateUserPasswordByUsernameAndOldPasswordReqDTO updateUserPasswordByUsernameAndOldPasswordReqDTO) {
        String username = updateUserPasswordByUsernameAndOldPasswordReqDTO.getUsername();
        String oldPassword = updateUserPasswordByUsernameAndOldPasswordReqDTO.getOldPassword();
        String newPassword = updateUserPasswordByUsernameAndOldPasswordReqDTO.getNewPassword();

        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return new Result<>(ResultCodeEnum.USER_OLD_PASSWORD_ERROR);
        }
        if (newPassword.equals(oldPassword)) {
            return new Result<>(ResultCodeEnum.USER_REPEAT_PASSWORD_ERROR);
        }

        return this.updateUserPasswordByUsername(username, newPassword);
    }

    @Override
    @SneakyThrows
    public Result<Void> updateCurrentLoginUserPassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {

        //获取登陆账号
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);
        UpdateUserPasswordByUsernameAndOldPasswordReqDTO updateUserPasswordByUsernameAndOldPasswordReqDTO = ModelMapperUtil.map(updateUserPasswordReqDTO, UpdateUserPasswordByUsernameAndOldPasswordReqDTO.class);
        updateUserPasswordByUsernameAndOldPasswordReqDTO.setUsername(user.getUsername());
        return userService.updateUserPasswordByUsernameAndOldPassword(updateUserPasswordByUsernameAndOldPasswordReqDTO);

    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    })

    @Override
    @SneakyThrows
    public Result<Void> updateUserNicknameByUsername(UpdateUserNicknameByUsernameReqDTO updateUserNicknameByUsernameReqDTO) {
        String username = updateUserNicknameByUsernameReqDTO.getUsername();
        String nickname = updateUserNicknameByUsernameReqDTO.getNickname();
        String realname = updateUserNicknameByUsernameReqDTO.getNickname();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        //初始化
        user.setNickname(nickname);
        user.setRealname(realname);
        //更新到数据库
        this.update(user);
        return new Result<>(ResultCodeEnum.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    })
    @Override
    @SneakyThrows
    public Result<Void> updateUserMobileByUsername(UpdateUserMobileByUsernameReqDTO updateUserMobileByUsernameReqDTO) {
        String username = updateUserMobileByUsernameReqDTO.getUsername();
        String mobile = updateUserMobileByUsernameReqDTO.getMobile();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        //初始化
        user.setMobile(mobile);
        //更新到数据库
        this.update(user);
        return new Result<>(ResultCodeEnum.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    })
    @Override
    @SneakyThrows
    public Result<Void> updateUserEmailByUsername(UpdateUserEmailByUsernameReqDTO updateUserEmailByUsernameReqDTO) {
        String username = updateUserEmailByUsernameReqDTO.getUsername();
        String email = updateUserEmailByUsernameReqDTO.getEmail();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        //初始化
        user.setEmail(email);
        //更新到数据库
        this.update(user);
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "User", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateUserRolenamesByUsername(UpdateUserRolenamesByUsernameReqDTO updateUserRolenamesByUsernameReqDTO) {
        String username = updateUserRolenamesByUsernameReqDTO.getUsername();
        String rolenames = updateUserRolenamesByUsernameReqDTO.getRolenames();
        String roledescs = updateUserRolenamesByUsernameReqDTO.getRoledescs();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        //初始化
        user.setRolenames(rolenames);
        user.setRoledescs(roledescs);
        //更新到数据库
        this.update(user);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("User", "UserRole", "UserOrg");

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "User", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updateUserOrgnamesByUsername(UpdateUserOrgnamesByUsernameReqDTO updateUserOrgnamesByUsernameReqDTO) {
        String username = updateUserOrgnamesByUsernameReqDTO.getUsername();
        String orgnames = updateUserOrgnamesByUsernameReqDTO.getOrgnames();
        String orgdescs = updateUserOrgnamesByUsernameReqDTO.getOrgdescs();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User user = this.get(predicate);

        //初始化
        user.setOrgnames(orgnames);
        user.setOrgdescs(orgdescs);
        //更新到数据库
        this.update(user);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("User", "UserRole", "UserOrg");

        return new Result<>(ResultCodeEnum.OK);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "User", key = "'getUserByUsername:'+#p0.username")
    })
    @Override
    @SneakyThrows
    public Result<Void> enableUser(GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        String username = getUserByUsernameReqDTO.getUsername();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User afterUser = this.get(predicate);
        if (afterUser == null) {
            return new Result<>(ResultCodeEnum.USER_NOT_EXISTS);
        }
        //初始化
        afterUser.setStatus(UsersStatusEnum.ENABLE.getCode());
        //更新到数据库
        userService.update(afterUser);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("User", "UserRole", "UserOrg");

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "User", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disableUser(GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        String username = getUserByUsernameReqDTO.getUsername();
        //加载
        Predicate predicate = QUser.user.username.eq(username);
        User afterUser = this.get(predicate);
        if (afterUser == null) {
            return new Result<>(ResultCodeEnum.USER_NOT_EXISTS);
        }
        //初始化
        afterUser.setStatus(UsersStatusEnum.DISABLE.getCode());
        //更新到数据库
        userService.update(afterUser);

        //发送到消息队列处理
        clearRedisService.clearPrefixs("User", "UserRole", "UserOrg");

        return new Result<>(ResultCodeEnum.OK);
    }
}
