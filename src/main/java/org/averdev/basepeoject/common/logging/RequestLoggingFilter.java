package org.averdev.basepeoject.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Autowired
    private CorrelationIdFilter correlationIdFilter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip logging for health check endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/actuator/health") || path.startsWith("/api/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 1024);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(requestWrapper, responseWrapper, duration);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        try {
            String correlationId = MDC.get("correlationId");
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("Request Details - ");
            logMessage.append("CorrelationId: ").append(correlationId != null ? correlationId : "N/A").append(", ");
            logMessage.append("Method: ").append(request.getMethod()).append(", ");
            logMessage.append("URI: ").append(request.getRequestURI()).append(", ");
            logMessage.append("Status: ").append(response.getStatus()).append(", ");
            logMessage.append("Duration: ").append(duration).append("ms, ");
            logMessage.append("RemoteAddr: ").append(getClientIpAddress(request)).append(", ");
            logMessage.append("UserAgent: ").append(request.getHeader("User-Agent"));
            
            // Log request body for POST/PUT requests (excluding sensitive endpoints)
            if (shouldLogRequestBody(request)) {
                String requestBody = getRequestBody(request);
                if (requestBody != null && !requestBody.isEmpty()) {
                    logMessage.append(", RequestBody: ").append(requestBody);
                }
            }
            
            // Log response body for errors
            if (response.getStatus() >= 400) {
                String responseBody = getResponseBody(response);
                if (responseBody != null && !responseBody.isEmpty()) {
                    logMessage.append(", ResponseBody: ").append(responseBody);
                }
            }
            
            if (response.getStatus() >= 400) {
                logger.error(logMessage.toString());
            } else {
                logger.info(logMessage.toString());
            }
            
        } catch (Exception e) {
            logger.error("Error logging request: {}", e.getMessage(), e);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] buf = request.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, request.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error reading request body: {}", e.getMessage());
        }
        return null;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] buf = response.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, response.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error reading response body: {}", e.getMessage());
        }
        return null;
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

    private boolean shouldLogRequestBody(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Don't log sensitive endpoints
        if (path.contains("/auth/signin") || path.contains("/auth/signup") || 
            path.contains("/password") || path.contains("/token")) {
            return false;
        }
        
        // Only log POST, PUT, PATCH requests
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") || 
               path.startsWith("/api/health") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}
