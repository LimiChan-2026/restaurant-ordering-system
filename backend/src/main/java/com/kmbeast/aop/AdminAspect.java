package com.kmbeast.aop;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.context.UserContext;
import com.kmbeast.pojo.api.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 管理员权限切面
 */
@Slf4j
@Aspect
@Component
public class AdminAspect {

    @Around("@annotation(adminOnly)")
    public Object checkAdmin(ProceedingJoinPoint joinPoint, AdminOnly adminOnly) throws Throwable {
        // 检查是否为管理员
        Integer role = UserContext.getRole();
        if (role == null || role != 2) {
            log.warn("权限不足：非管理员访问受限接口");
            return R.error(403, "无操作权限，仅管理员可访问");
        }

        // 继续执行
        return joinPoint.proceed();
    }
}
