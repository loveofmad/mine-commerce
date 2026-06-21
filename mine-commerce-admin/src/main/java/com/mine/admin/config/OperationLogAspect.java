package com.mine.admin.config;

import com.mine.mapper.OperationLogMapper;
import com.mine.model.entity.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Pointcut("@annotation(com.mine.admin.config.LogOperation)")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        try {
            LogOperation annotation = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
                    .getMethod().getAnnotation(LogOperation.class);

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            OperationLog logEntity = new OperationLog();
            logEntity.setModule(annotation.module());
            logEntity.setAction(annotation.action());
            logEntity.setDetail(joinPoint.getSignature().getName());
            logEntity.setIp(request != null ? request.getRemoteAddr() : "unknown");
            logEntity.setCreateTime(LocalDateTime.now());

            Object userIdObj = request != null ? request.getAttribute("userId") : null;
            if (userIdObj instanceof Number) logEntity.setAdminId(((Number) userIdObj).longValue());
            Object usernameObj = request != null ? request.getAttribute("username") : null;
            if (usernameObj instanceof String) logEntity.setAdminUsername((String) usernameObj);

            operationLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
        return result;
    }
}
