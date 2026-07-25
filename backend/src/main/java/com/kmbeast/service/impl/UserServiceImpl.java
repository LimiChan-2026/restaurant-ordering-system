package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.*;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.pojo.vo.LoginVO;
import com.kmbeast.pojo.vo.UserVO;
import com.kmbeast.service.UserService;
import com.kmbeast.utils.JwtUtils;
import com.kmbeast.utils.PasswordUtils;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @Override
    public R<LoginVO> login(LoginDTO loginDTO) {
        // 1. 根据账号查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, loginDTO.getAccount());
        User user = userMapper.selectOne(queryWrapper);

        // 2. 验证用户是否存在
        if (user == null) {
            log.warn("登录失败：账号不存在 - {}", loginDTO.getAccount());
            return R.error(1001, "账号或密码错误");
        }

        // 3. 验证密码
        if (!PasswordUtils.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败：密码错误 - {}", loginDTO.getAccount());
            return R.error(1001, "账号或密码错误");
        }

        // 4. 验证用户状态（禁用/启用）
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败：账号已被禁用 - {}", loginDTO.getAccount());
            return R.error(1002, "账号已被禁用，请联系管理员");
        }

        // 5. 生成JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getAccount(), user.getRole());

        // 6. 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        // 将User转换为UserVO（不包含密码）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        loginVO.setUser(userVO);

        log.info("用户登录成功：{}", user.getAccount());
        return R.ok(loginVO, "登录成功");
    }

    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> register(RegisterDTO registerDTO) {
        // 1. 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            return R.error("两次输入的密码不一致");
        }

        // 2. 检查用户名是否已存在（用户名同时作为账号）
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, registerDTO.getUsername());
        Long count = userMapper.selectCount(queryWrapper);

        if (count > 0) {
            log.warn("注册失败：用户名已存在 - {}", registerDTO.getUsername());
            return R.error("用户名已存在");
        }

        // 3. 创建用户（用户名同时作为账号和昵称）
        User user = new User();
        user.setAccount(registerDTO.getUsername());
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtils.encode(registerDTO.getPassword()));
        user.setRole(1); // 默认为普通用户
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        // 4. 保存用户
        userMapper.insert(user);

        log.info("用户注册成功：{}", user.getAccount());
        return R.ok("注册成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public R<UserVO> getUserInfo() {
        // 从上下文获取当前用户ID
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.error(401, "未登录");
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        // 转换为VO（不包含密码）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return R.ok(userVO);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateUser(UpdateUserDTO updateUserDTO) {
        // 从上下文获取当前用户ID
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.error(401, "未登录");
        }
        if (updateUserDTO == null) {
            return R.error("更新信息不能为空");
        }
        if (StringUtils.hasText(updateUserDTO.getUsername()) && updateUserDTO.getUsername().trim().length() > 50) {
            return R.error("用户昵称不能超过50个字符");
        }
        if (updateUserDTO.getGender() != null && updateUserDTO.getGender() != 1 && updateUserDTO.getGender() != 2) {
            return R.error("性别参数不合法");
        }
        if (StringUtils.hasText(updateUserDTO.getPhone())
                && !updateUserDTO.getPhone().trim().matches("^1\\d{10}$")) {
            return R.error("手机号格式不正确");
        }
        if (StringUtils.hasText(updateUserDTO.getEmail())
                && !updateUserDTO.getEmail().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return R.error("邮箱格式不正确");
        }

        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);

        // 只更新非空字段
        if (StringUtils.hasText(updateUserDTO.getUsername())) {
            updateWrapper.set(User::getUsername, updateUserDTO.getUsername().trim());
        }
        if (StringUtils.hasText(updateUserDTO.getAvatar())) {
            updateWrapper.set(User::getAvatar, updateUserDTO.getAvatar());
        }
        if (updateUserDTO.getGender() != null) {
            updateWrapper.set(User::getGender, updateUserDTO.getGender());
        }
        if (updateUserDTO.getBirthday() != null) {
            updateWrapper.set(User::getBirthday, updateUserDTO.getBirthday());
        }
        if (StringUtils.hasText(updateUserDTO.getPhone())) {
            updateWrapper.set(User::getPhone, updateUserDTO.getPhone().trim());
        }
        if (StringUtils.hasText(updateUserDTO.getEmail())) {
            updateWrapper.set(User::getEmail, updateUserDTO.getEmail().trim());
        }

        // 执行更新
        userMapper.update(null, updateWrapper);

        log.info("用户信息更新成功：userId={}", userId);
        return R.ok("更新成功");
    }

    /**
     * 查询用户列表（管理员）
     */
    @Override
    public R<List<UserVO>> getUserList(UserQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new UserQueryDTO();
        }
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 按用户名模糊查询
        if (StringUtils.hasText(queryDTO.getUsername())) {
            queryWrapper.like(User::getUsername, queryDTO.getUsername());
        }

        // 按角色筛选
        if (queryDTO.getRole() != null) {
            queryWrapper.eq(User::getRole, queryDTO.getRole());
        }

        // 按创建时间降序
        queryWrapper.orderByDesc(User::getCreateTime);

        // 分页查询
        Page<User> page = PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize());
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 转换为VO列表（不包含密码）
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                })
                .collect(Collectors.toList());

        return R.ok(userVOList, userPage.getTotal());
    }

    /**
     * 更新用户角色/状态（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateUserRole(UpdateUserRoleDTO updateDTO) {
        // 验证用户是否存在
        User user = userMapper.selectById(updateDTO.getId());
        if (user == null) {
            return R.error("用户不存在");
        }
        Integer currentUserId = UserContext.getUserId();
        if (user.getId().equals(currentUserId)
                && ((updateDTO.getStatus() != null && updateDTO.getStatus() == 0)
                || (updateDTO.getRole() != null && updateDTO.getRole() != 2))) {
            return R.error("不能禁用或降级当前登录管理员账号");
        }

        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, updateDTO.getId());

        // 更新角色
        if (updateDTO.getRole() != null) {
            updateWrapper.set(User::getRole, updateDTO.getRole());
        }

        // 更新状态
        if (updateDTO.getStatus() != null) {
            updateWrapper.set(User::getStatus, updateDTO.getStatus());
        }

        // 执行更新
        userMapper.update(null, updateWrapper);

        log.info("用户角色/状态更新成功：userId={}, role={}, status={}",
                updateDTO.getId(), updateDTO.getRole(), updateDTO.getStatus());
        return R.ok("更新成功");
    }
}
