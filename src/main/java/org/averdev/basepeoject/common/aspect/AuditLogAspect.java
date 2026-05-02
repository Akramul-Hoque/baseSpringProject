package org.averdev.basepeoject.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getCurrentRequest();
        
        // Pre-execution logging
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("timestamp", LocalDateTime.now().toString());
        auditLog.put("event", "API_REQUEST");
        auditLog.put("user", getCurrentUser());
        auditLog.put("ip", getClientIp(request));
        auditLog.put("method", request != null ? request.getMethod() : "UNKNOWN");
        auditLog.put("endpoint", request != null ? request.getRequestURI() : "UNKNOWN");
        auditLog.put("controller", joinPoint.getTarget().getClass().getSimpleName());
        auditLog.put("methodSignature", getMethodSignature(joinPoint));
        auditLog.put("parameters", Arrays.toString(joinPoint.getArgs()));
        
        Object result = null;
        Exception exception = null;
        
        try {
            result = joinPoint.proceed();
            auditLog.put("status", "SUCCESS");
            auditLog.put("responseTime", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            exception = e;
            auditLog.put("status", "ERROR");
            auditLog.put("error", e.getMessage());
            auditLog.put("responseTime", System.currentTimeMillis() - startTime);
            throw e;
        } finally {
            // Log the audit entry
            try {
                auditLogger.info(objectMapper.writeValueAsString(auditLog));
            } catch (Exception e) {
                auditLogger.error("Failed to log audit entry: {}", e.getMessage());
            }
        }
        
        return result;
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "ANONYMOUS";
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private String getMethodSignature(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }
}
