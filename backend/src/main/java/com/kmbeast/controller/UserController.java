package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.*;
import com.kmbeast.pojo.vo.LoginVO;
import com.kmbeast.pojo.vo.UserVO;
import com.kmbeast.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录结果（包含Token和用户信息）
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public R<UserVO> getUserInfo() {
        return userService.getUserInfo();
    }

    /**
     * 更新用户信息
     *
     * @param updateUserDTO 更新参数（昵称、头像、性别、生日、手机号、邮箱）
     * @return 更新结果
     */
    @PutMapping("/updateEntity")
    public R<Void> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(updateUserDTO);
    }

    /**
     * 查询用户列表（管理员）
     *
     * @param queryDTO 查询参数（current, size, username, role）
     * @return 用户列表
     */
    @AdminOnly
    @PostMapping("/list")
    public R<List<UserVO>> getUserList(@RequestBody UserQueryDTO queryDTO) {
        return userService.getUserList(queryDTO);
    }

    /**
     * 更新用户角色（管理员）
     *
     * @param updateDTO 更新参数（id, role, status）
     * @return 更新结果
     */
    @AdminOnly
    @PutMapping("/updateRole")
    public R<Void> updateUserRole(@Valid @RequestBody UpdateUserRoleDTO updateDTO) {
        return userService.updateUserRole(updateDTO);
    }
}
