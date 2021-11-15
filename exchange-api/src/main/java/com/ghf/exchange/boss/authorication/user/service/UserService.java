package com.ghf.exchange.boss.authorication.user.service;

import com.ghf.exchange.boss.authorication.user.dto.*;
import com.ghf.exchange.boss.authorication.user.entity.User;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;

import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */

public interface UserService extends BaseService<User, Long> {

    /**
     * 分页搜索用户
     *
     * @param pageUserReqDTO
     * @return
     */
    Result<PageRespDTO<UserRespDTO>> pageUser(PageUserReqDTO pageUserReqDTO);

    /**
     * 列出所有用户
     * l@param  listUserReqDTO
     *
     * @return
     */
    Result<List<UserRespDTO>> listUser(ListUserReqDTO listUserReqDTO);

    /**
     * 获取当前登录用户详情(认证服务器自身调用)
     *
     * @return
     */
    Result<UserRespDTO> getCurrentLoginUser();

    /**
     * 根据用户登录名称获取用户详情
     *
     * @param getUserByUsernameReqDTO
     * @return
     */
    Result<UserRespDTO> getUserByUsername(GetUserByUsernameReqDTO getUserByUsernameReqDTO);

    /**
     * 根据邮箱获取用户详情
     *
     * @param getUserByEmailReqDTO
     * @return
     */
    Result<UserRespDTO> getUserByEmail(GetUserByEmailReqDTO getUserByEmailReqDTO);

    /**
     * 根据手机获取用户详情
     *
     * @param getUserByMobileReqDTO
     * @return
     */
    Result<UserRespDTO> getUserByMobile(GetUserByMobileReqDTO getUserByMobileReqDTO);

    /**
     * 根据用户登录名称判断用户是否存在
     *
     * @param getUserByUsernameReqDTO
     * @return
     */
    Result<Boolean> existsUserByUsername(GetUserByUsernameReqDTO getUserByUsernameReqDTO);

    /**
     * 根据邮箱判断用户是否存在
     *
     * @param existsUserByEmailReqDTO
     * @return
     */
    Result<Boolean> existsUserByEmail(ExistsUserByEmailReqDTO existsUserByEmailReqDTO);

    /**
     * 根据手机判断用户是否存在
     *
     * @param existsUserByMobileReqDTO
     * @return
     */
    Result<Boolean> existsUserByMobile(ExistsUserByMobileReqDTO existsUserByMobileReqDTO);

    /**
     * 登录
     *
     * @param loginReqDTO
     * @return
     */
    Result<LoginRespDTO> login(LoginReqDTO loginReqDTO);

    /**
     * 保存用户
     *
     * @param addUserReqDTO
     * @return
     */
    Result<Void> addUser(AddUserReqDTO addUserReqDTO);

    /**
     * 更新用户
     *
     * @param updateUserReqDTO
     * @return
     */
    Result<Void> updateUserByUsername(UpdateUserByUsernameReqDTO updateUserReqDTO);

    /**
     * 更新当前登录用户的用户密码
     *
     * @param updateUserPasswordReqDTO
     * @return
     */
    Result<Void> updateCurrentLoginUserPassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO);

    /**
     * 根据用户名更新用户密码
     *
     * @param updateUserPasswordByUsernameReqDTO
     * @return
     */
    Result<Void> updateUserPasswordByUsername(UpdateUserPasswordByUsernameReqDTO updateUserPasswordByUsernameReqDTO);

    /**
     * 根据用户名和旧密码更新用户密码
     *
     * @param updateUserPasswordByUsernameAndOldPasswordReqDTO
     * @return
     */
    Result<Void> updateUserPasswordByUsernameAndOldPassword(UpdateUserPasswordByUsernameAndOldPasswordReqDTO updateUserPasswordByUsernameAndOldPasswordReqDTO);

    /**
     * 根据用户名更新用户昵称
     *
     * @param updateUserNicknameByUsernameReqDTO
     * @return
     */
    Result<Void> updateUserNicknameByUsername(UpdateUserNicknameByUsernameReqDTO updateUserNicknameByUsernameReqDTO);

    /**
     * 根据用户名更新用户手机号码
     *
     * @param updateUserMobileByUsernameReqDTO
     * @return
     */
    Result<Void> updateUserMobileByUsername(UpdateUserMobileByUsernameReqDTO updateUserMobileByUsernameReqDTO);

    /**
     * 根据用户名更新用户邮箱
     *
     * @param updateUserEmailByUsernameReqDTO
     * @return
     */
    Result<Void> updateUserEmailByUsername(UpdateUserEmailByUsernameReqDTO updateUserEmailByUsernameReqDTO);

    /**
     * 根据用户名更新角色列表
     *
     * @param updateUserRolenamesByUsernameReqDTO
     * @return
     */
    Result<Void> updateUserRolenamesByUsername(UpdateUserRolenamesByUsernameReqDTO updateUserRolenamesByUsernameReqDTO);

    /**
     * 根据用户名更新组织列表
     *
     * @param updateUserOrgnamesByUsernameReqDTO
     * @return
     */
    Result<Void> updateUserOrgnamesByUsername(UpdateUserOrgnamesByUsernameReqDTO updateUserOrgnamesByUsernameReqDTO);

    /**
     * 启用用户
     *
     * @param getUserByUsernameReqDTO
     * @return
     */
    Result<Void> enableUser(GetUserByUsernameReqDTO getUserByUsernameReqDTO);

    /**
     * 禁用用户
     *
     * @param getUserByUsernameReqDTO
     * @return
     */
    Result<Void> disableUser(GetUserByUsernameReqDTO getUserByUsernameReqDTO);
}