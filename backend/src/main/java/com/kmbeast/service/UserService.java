package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.*;
import com.kmbeast.pojo.vo.LoginVO;
import com.kmbeast.pojo.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录结果（包含Token和用户信息）
     */
    R<LoginVO> login(LoginDTO loginDTO);

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果
     */
    R<Void> register(RegisterDTO registerDTO);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    R<UserVO> getUserInfo();

    /**
     * 更新用户信息
     *
     * @param updateUserDTO 更新参数
     * @return 更新结果
     */
    R<Void> updateUser(UpdateUserDTO updateUserDTO);

    /**
     * 查询用户列表（管理员）
     *
     * @param queryDTO 查询参数
     * @return 用户列表
     */
    R<List<UserVO>> getUserList(UserQueryDTO queryDTO);

    /**
     * 更新用户角色（管理员）
     *
     * @param updateDTO 更新参数
     * @return 更新结果
     */
    R<Void> updateUserRole(UpdateUserRoleDTO updateDTO);
}
