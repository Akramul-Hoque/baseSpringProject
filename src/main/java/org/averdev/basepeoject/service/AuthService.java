package org.averdev.basepeoject.service;

import org.averdev.basepeoject.dto.JwtAuthenticationResponse;
import org.averdev.basepeoject.dto.LoginRequest;
import org.averdev.basepeoject.dto.SignUpRequest;
import org.averdev.basepeoject.entity.User;
import org.averdev.basepeoject.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getUsernameOrEmail());
        Long expiresIn = tokenProvider.getExpirationTime(jwt) / 1000;
        
        return new JwtAuthenticationResponse(jwt, expiresIn, refreshToken);
    }
    
    public User registerUser(SignUpRequest signUpRequest) {
        return userService.createUser(signUpRequest);
    }
    
    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = tokenProvider.generateToken(username);
            String newRefreshToken = tokenProvider.generateRefreshToken(username);
            Long expiresIn = tokenProvider.getExpirationTime(newAccessToken) / 1000;
            
            return new JwtAuthenticationResponse(newAccessToken, expiresIn, newRefreshToken);
        }
        throw new RuntimeException("Invalid refresh token");
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }
}
