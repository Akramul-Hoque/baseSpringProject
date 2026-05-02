package org.averdev.basepeoject.common.aspect;

import org.averdev.basepeoject.common.annotation.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimitService rateLimitService;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        String key = generateKey(request, rateLimit);
        
        if (!rateLimitService.isAllowed(key, rateLimit.requests(), rateLimit.windowSeconds())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, rateLimit.message());
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String generateKey(HttpServletRequest request, RateLimit rateLimit) {
        String baseKey = rateLimit.key().isEmpty() ? 
            "custom_rate_limit" : rateLimit.key();
        
        return rateLimitService.generateKey(request, baseKey);
    }
}
