package org.averdev.basepeoject.common.aspect;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitService rateLimitService;

    @Value("${app.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${app.rate-limit.requests-per-hour:1000}")
    private int requestsPerHour;

    @Value("${app.rate-limit.requests-per-day:10000}")
    private int requestsPerDay;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Skip rate limiting for health checks and actuator endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/api/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check different rate limits
        boolean allowed = checkRateLimits(request, response);

        if (allowed) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean checkRateLimits(HttpServletRequest request, HttpServletResponse response) {
        String clientKey = rateLimitService.generateKey(request, "rate_limit");

        // Check per-minute limit
        if (!rateLimitService.isAllowed(clientKey + ":minute", requestsPerMinute, 60)) {
            setRateLimitHeaders(response, clientKey, requestsPerMinute, 60);
            try {
                sendRateLimitExceeded(response, "Too many requests per minute");
            } catch (IOException e) {
                // If we can't send the error response, just set the status
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            return false;
        }

        // Check per-hour limit
        if (!rateLimitService.isAllowed(clientKey + ":hour", requestsPerHour, 3600)) {
            setRateLimitHeaders(response, clientKey, requestsPerHour, 3600);
            try {
                sendRateLimitExceeded(response, "Too many requests per hour");
            } catch (IOException e) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            return false;
        }

        // Check per-day limit
        if (!rateLimitService.isAllowed(clientKey + ":day", requestsPerDay, 86400)) {
            setRateLimitHeaders(response, clientKey, requestsPerDay, 86400);
            try {
                sendRateLimitExceeded(response, "Too many requests per day");
            } catch (IOException e) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            return false;
        }

        setRateLimitHeaders(response, clientKey, requestsPerMinute, 60);
        return true;
    }

    private void setRateLimitHeaders(HttpServletResponse response, String key, int limit, int window) {
        Long remaining = rateLimitService.getRemainingRequests(key + ":minute", limit);
        Long resetTime = rateLimitService.getTimeToReset(key + ":minute");

        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining != null ? remaining : limit));
        response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime != null ? resetTime : 0));
    }

    private void sendRateLimitExceeded(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        errorResponse.put("error", "Too Many Requests");
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(convertToJson(errorResponse));
    }

    private String convertToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
