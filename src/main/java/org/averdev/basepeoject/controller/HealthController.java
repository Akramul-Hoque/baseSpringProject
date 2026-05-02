package org.averdev.basepeoject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Application health monitoring APIs")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    @Operation(summary = "Basic health check")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy"),
        @ApiResponse(responseCode = "503", description = "Application is unhealthy")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("application", "Spring Boot Base Project");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check with component status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detailed health information"),
        @ApiResponse(responseCode = "503", description = "Application is unhealthy")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // Database health check
        Map<String, Object> database = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            database.put("status", "UP");
            database.put("url", connection.getMetaData().getURL());
            database.put("product", connection.getMetaData().getDatabaseProductName());
            database.put("version", connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
        }
        health.put("database", database);
        
        // Memory information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());
        memory.put("used", runtime.totalMemory() - runtime.freeMemory());
        memory.put("max", runtime.maxMemory());
        health.put("memory", memory);
        
        // System information
        Map<String, Object> system = new HashMap<>();
        system.put("processors", runtime.availableProcessors());
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        health.put("system", system);
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/readiness")
    @Operation(summary = "Readiness probe for Kubernetes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is ready"),
        @ApiResponse(responseCode = "503", description = "Application is not ready")
    })
    public ResponseEntity<Map<String, Object>> readinessProbe() {
        Map<String, Object> readiness = new HashMap<>();
        
        // Check database connectivity
        boolean databaseReady = checkDatabaseConnectivity();
        
        if (databaseReady) {
            readiness.put("status", "READY");
            readiness.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(readiness);
        } else {
            readiness.put("status", "NOT_READY");
            readiness.put("timestamp", LocalDateTime.now());
            readiness.put("reason", "Database connection failed");
            return ResponseEntity.status(503).body(readiness);
        }
    }

    @GetMapping("/liveness")
    @Operation(summary = "Liveness probe for Kubernetes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is alive"),
        @ApiResponse(responseCode = "503", description = "Application is not alive")
    })
    public ResponseEntity<Map<String, Object>> livenessProbe() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now());
        liveness.put("uptime", System.currentTimeMillis());
        
        return ResponseEntity.ok(liveness);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Application metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        
        // Memory metrics
        Map<String, Object> memoryMetrics = new HashMap<>();
        memoryMetrics.put("heap.used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        memoryMetrics.put("heap.total", runtime.totalMemory() / 1024 / 1024);
        memoryMetrics.put("heap.max", runtime.maxMemory() / 1024 / 1024);
        memoryMetrics.put("heap.free", runtime.freeMemory() / 1024 / 1024);
        metrics.put("memory", memoryMetrics);
        
        // Thread metrics
        Map<String, Object> threadMetrics = new HashMap<>();
        threadMetrics.put("count", Thread.activeCount());
        threadMetrics.put("peak", Thread.activeCount()); // Simplified
        metrics.put("threads", threadMetrics);
        
        // GC metrics (simplified)
        Map<String, Object> gcMetrics = new HashMap<>();
        gcMetrics.put("collections", "N/A"); // Would need MemoryMXBean for actual values
        gcMetrics.put("time", "N/A");
        metrics.put("gc", gcMetrics);
        
        metrics.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(metrics);
    }

    private boolean checkDatabaseConnectivity() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (Exception e) {
            return false;
        }
    }
}
