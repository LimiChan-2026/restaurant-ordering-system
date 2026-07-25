package com.kmbeast.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbeast.context.UserContext;
import com.kmbeast.pojo.api.R;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取Token
        String token = request.getHeader("Authorization");

        // 检查Token是否存在
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response, "未登录，请先登录");
            return false;
        }

        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (!jwtUtils.validateToken(token)) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }

        // 检查Token是否过期
        if (jwtUtils.isTokenExpired(token)) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }

        // 解析用户信息并存入上下文
        try {
            Integer userId = jwtUtils.getUserId(token);
            User user = userMapper.selectById(userId);
            if (user == null || Integer.valueOf(0).equals(user.getStatus())) {
                writeUnauthorized(response, "账号不存在或已被禁用");
                return false;
            }
            String account = user.getAccount();
            Integer role = user.getRole();

            UserContext.setUserId(userId);
            UserContext.setAccount(account);
            UserContext.setRole(role);

            log.debug("用户认证成功：userId={}, account={}, role={}", userId, account, role);
            return true;
        } catch (Exception e) {
            log.error("Token解析失败", e);
            writeUnauthorized(response, "登录信息异常，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除用户上下文
        UserContext.clear();
    }

    /**
     * 写入未授权响应
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        R<Void> result = R.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
