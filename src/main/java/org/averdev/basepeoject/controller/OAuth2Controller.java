package org.averdev.basepeoject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@Tag(name = "OAuth2 Authentication", description = "OAuth2 authentication APIs")
public class OAuth2Controller {
    
    @GetMapping("/user")
    @Operation(summary = "Get OAuth2 user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", oauth2User.getAttribute("name"));
        userInfo.put("email", oauth2User.getAttribute("email"));
        userInfo.put("attributes", oauth2User.getAttributes());
        return userInfo;
    }
    
    @GetMapping("/success")
    @Operation(summary = "OAuth2 authentication success callback")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OAuth2 authentication successful")
    })
    public Map<String, Object> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OAuth2 authentication successful");
        response.put("user", oauth2User.getAttributes());
        return response;
    }
    
    @GetMapping("/failure")
    @Operation(summary = "OAuth2 authentication failure callback")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "OAuth2 authentication failed")
    })
    public Map<String, Object> oauth2Failure() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OAuth2 authentication failed");
        return response;
    }
}
