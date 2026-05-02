package org.averdev.basepeoject.common.constants;

public final class ApplicationConstants {
    
    // Security
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String ROLE_PREFIX = "ROLE_";
    
    // API
    public static final String API_BASE_PATH = "/api";
    public static final String API_VERSION_PREFIX = "/v1";
    public static final String AUTH_BASE_PATH = "/auth";
    public static final String USER_BASE_PATH = "/users";
    public static final String HEALTH_BASE_PATH = "/health";
    
    // Cache
    public static final String USER_CACHE = "users";
    public static final String ROLE_CACHE = "roles";
    public static final String JWT_CACHE = "jwt";
    
    // Rate Limiting
    public static final String RATE_LIMIT_PREFIX = "rate_limit";
    public static final int DEFAULT_RATE_LIMIT_REQUESTS_PER_MINUTE = 60;
    public static final int DEFAULT_RATE_LIMIT_REQUESTS_PER_HOUR = 1000;
    public static final int DEFAULT_RATE_LIMIT_REQUESTS_PER_DAY = 10000;
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // JWT
    public static final int JWT_EXPIRATION_MS = 3600000; // 1 hour
    public static final int JWT_REFRESH_EXPIRATION_MS = 604800000; // 7 days
    
    // Password
    public static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    
    // Email
    public static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    // Headers
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final String X_REAL_IP_HEADER = "X-Real-IP";
    
    // Logging
    public static final String AUDIT_LOGGER = "AUDIT_LOGGER";
    public static final String SECURITY_LOGGER = "SECURITY_LOGGER";
    
    // Profiles
    public static final String DEV_PROFILE = "dev";
    public static final String TEST_PROFILE = "test";
    public static final String PROD_PROFILE = "prod";
    public static final String DOCKER_PROFILE = "docker";
    
    // Default Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    
    // Error Messages
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_USER_ALREADY_EXISTS = "User already exists";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ERROR_ACCESS_DENIED = "Access denied";
    public static final String ERROR_TOKEN_EXPIRED = "Token expired";
    public static final String ERROR_TOKEN_INVALID = "Invalid token";
    
    // Success Messages
    public static final String SUCCESS_LOGIN = "Login successful";
    public static final String SUCCESS_LOGOUT = "Logout successful";
    public static final String SUCCESS_USER_CREATED = "User created successfully";
    public static final String SUCCESS_USER_UPDATED = "User updated successfully";
    public static final String SUCCESS_USER_DELETED = "User deleted successfully";
    
    // Private constructor to prevent instantiation
    private ApplicationConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
