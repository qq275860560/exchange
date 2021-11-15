package com.ghf.exchange.boss.authorication.user.controller;

import com.ghf.exchange.boss.authorication.user.dto.*;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "用户接口", tags = {"用户接口"})
@RestController
@Lazy
@Slf4j
public class UserController {

    @Lazy
    @Resource
    private UserService userService;

    @ApiOperation(value = "分页搜索用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/pageUser")
    @SneakyThrows
    public Result<PageRespDTO<UserRespDTO>> pageUser(@RequestBody PageUserReqDTO pageUserReqDTO) {
        return userService.pageUser(pageUserReqDTO);
    }

    @ApiOperation(value = "列出所有用户(将来改成选人框)", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/listUser")
    public Result<List<UserRespDTO>> listUser(@RequestBody ListUserReqDTO listUserReqDTO) {
        return userService.listUser(listUserReqDTO);
    }

    @ApiOperation(value = "根据用户登录名称获取用户详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/getUserByUsername")
    @SneakyThrows
    public Result<UserRespDTO> getUserByUsername(@Valid @RequestBody GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        return userService.getUserByUsername(getUserByUsernameReqDTO);
    }

    @ApiOperation(value = "根据邮箱获取用户详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/getUserByEmail")
    @SneakyThrows
    public Result<UserRespDTO> getUserByEmail(@Valid @RequestBody GetUserByEmailReqDTO getUserByEmailReqDTO) {
        return userService.getUserByEmail(getUserByEmailReqDTO);
    }

    @ApiOperation(value = "根据手机获取用户详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/getUserByMobile")
    @SneakyThrows
    public Result<UserRespDTO> getUserByMobile(@Valid @RequestBody GetUserByMobileReqDTO getUserByMobileReqDTO) {
        return userService.getUserByMobile(getUserByMobileReqDTO);
    }

    @ApiOperation(value = "获取当前登录用户详情", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/getCurrentLoginUser")
    @SneakyThrows
    public Result<UserRespDTO> getCurrentLoginUser() {
        return userService.getCurrentLoginUser();
    }

    @ApiOperation(value = "登录", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/login")
    @SneakyThrows
    public Result<LoginRespDTO> login(@RequestBody LoginReqDTO loginReqDTO) {
        return userService.login(loginReqDTO);
    }

    @ApiOperation(value = "根据用户名判断用户是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/existsUserByUsername")
    @SneakyThrows
    public Result<Boolean> existsUserByUsername(@Valid @RequestBody GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        return userService.existsUserByUsername(getUserByUsernameReqDTO);
    }

    @ApiOperation(value = "根据邮箱判断用户是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/existsUserByEmail")
    @SneakyThrows
    public Result<Boolean> existsUserByEmail(@Valid @RequestBody ExistsUserByEmailReqDTO existsUserByEmailReqDTO) {
        return userService.existsUserByEmail(existsUserByEmailReqDTO);
    }

    @ApiOperation(value = "根据手机判断用户是否存在", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/existsUserByMobile")
    @SneakyThrows
    public Result<Boolean> existsUserByMobile(@Valid @RequestBody ExistsUserByMobileReqDTO existsUserByMobileReqDTO) {
        return userService.existsUserByMobile(existsUserByMobileReqDTO);
    }

    @ApiOperation(value = "新建用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/addUser")
    @SneakyThrows
    public Result<Void> addUser(@RequestBody AddUserReqDTO addUserReqDTO) {
        return userService.addUser(addUserReqDTO);
    }

    @ApiOperation(value = "更新当前登录用户的用户密码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateCurrentLoginUserPassword")
    @SneakyThrows
    public Result<Void> updateCurrentLoginUserPassword(@RequestBody UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {
        return userService.updateCurrentLoginUserPassword(updateUserPasswordReqDTO);
    }

    @ApiOperation(value = "根据用户名更新用户密码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateUserPasswordByUsername")
    @SneakyThrows
    public Result<Void> updateUserPasswordByUsername(@RequestBody UpdateUserPasswordByUsernameReqDTO updateUserPasswordByUsernameReqDTO) {
        return userService.updateUserPasswordByUsername(updateUserPasswordByUsernameReqDTO);
    }

    @ApiOperation(value = "根据用户名和旧密码更新用户密码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateUserPasswordByUsernameAndOldPassword")
    @SneakyThrows
    public Result<Void> updateUserPasswordByUsernameAndOldPassword(@RequestBody UpdateUserPasswordByUsernameAndOldPasswordReqDTO updateUserPasswordByUsernameAndOldPasswordReqDTO) {
        return userService.updateUserPasswordByUsernameAndOldPassword(updateUserPasswordByUsernameAndOldPasswordReqDTO);
    }

    @ApiOperation(value = "根据用户名更新用户昵称", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateUserNicknameByUsername")
    @SneakyThrows
    public Result<Void> updateUserNicknameByUsername(@RequestBody UpdateUserNicknameByUsernameReqDTO updateUserNicknameByUsernameReqDTO) {
        return userService.updateUserNicknameByUsername(updateUserNicknameByUsernameReqDTO);
    }

    @ApiOperation(value = "根据用户名更新用户手机", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateUserMobileByUsername")
    @SneakyThrows
    public Result<Void> updateUserMobileByUsername(@RequestBody UpdateUserMobileByUsernameReqDTO updateUserMobileByUsernameReqDTO) {
        return userService.updateUserMobileByUsername(updateUserMobileByUsernameReqDTO);
    }

    @ApiOperation(value = "根据用户名更新用户邮箱", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateUserEmailByUsername")
    @SneakyThrows
    public Result<Void> updateUserEmailByUsername(@RequestBody UpdateUserEmailByUsernameReqDTO updateUserEmailByUsernameReqDTO) {
        return userService.updateUserEmailByUsername(updateUserEmailByUsernameReqDTO);
    }

    @ApiOperation(value = "更新用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/updateUserByUsername")
    @SneakyThrows
    public Result<Void> updateUserByUsername(@RequestBody UpdateUserByUsernameReqDTO updateUserReqDTO) {
        return userService.updateUserByUsername(updateUserReqDTO);
    }

    @ApiOperation(value = "启用用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/enableUser")
    @SneakyThrows
    public Result<Void> enableUser(@RequestBody GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        return userService.enableUser(getUserByUsernameReqDTO);
    }

    @ApiOperation(value = "禁用用户", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/user/disableUser")
    @SneakyThrows
    public Result<Void> disableUser(@RequestBody GetUserByUsernameReqDTO getUserByUsernameReqDTO) {
        return userService.disableUser(getUserByUsernameReqDTO);
    }
}
