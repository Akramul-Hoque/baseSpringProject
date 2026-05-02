package org.averdev.basepeoject.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> rateLimitScript;

    public RateLimitService(RedisTemplate<String, Object> redisTemplate, RedisScript<Long> rateLimitScript) {
        this.redisTemplate = redisTemplate;
        this.rateLimitScript = rateLimitScript;
    }

    public boolean isAllowed(String key, int limit, int windowSeconds) {
        Long result = redisTemplate.execute(
            rateLimitScript,
            Collections.singletonList(key),
            String.valueOf(limit),
            String.valueOf(windowSeconds)
        );
        
        return result != null && result > 0;
    }

    public String generateKey(HttpServletRequest request, String prefix) {
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        return prefix + ":" + clientIp + ":" + endpoint;
    }

    public String generateUserKey(String userId, String prefix) {
        return prefix + ":user:" + userId;
    }

    public String generateGlobalKey(String prefix) {
        return prefix + ":global";
    }

    private String getClientIpAddress(HttpServletRequest request) {
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

    public void reset(String key) {
        redisTemplate.delete(key);
    }

    public Long getRemainingRequests(String key, int limit) {
        String current = (String) redisTemplate.opsForValue().get(key);
        if (current == null) {
            return (long) limit;
        }
        long count = Long.parseLong(current);
        return Math.max(0, limit - count);
    }

    public Long getTimeToReset(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
