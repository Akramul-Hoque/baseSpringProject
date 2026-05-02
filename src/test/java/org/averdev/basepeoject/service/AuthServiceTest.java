package org.averdev.basepeoject.service;

import org.averdev.basepeoject.common.security.JwtTokenProvider;
import org.averdev.basepeoject.dto.JwtAuthenticationResponse;
import org.averdev.basepeoject.dto.LoginRequest;
import org.averdev.basepeoject.dto.SignUpRequest;
import org.averdev.basepeoject.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private SignUpRequest signUpRequest;
    private User mockUser;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testuser", "password");
        
        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password");
        signUpRequest.setRoles(Set.of("USER"));
        
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRoles(new HashSet<>());
        
        mockAuthentication = new UsernamePasswordAuthenticationToken(
                mockUser.getUsername(), mockUser.getPassword(), mockUser.getAuthorities());
    }

    @Test
    void testAuthenticateUser_Success() {
        // Given
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mockAuthentication);
        when(tokenProvider.generateToken(mockAuthentication))
                .thenReturn("test-access-token");
        when(tokenProvider.generateRefreshToken(anyString()))
                .thenReturn("test-refresh-token");
        when(tokenProvider.getExpirationTime(anyString()))
                .thenReturn(3600000L);

        // When
        JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(tokenProvider).generateToken(mockAuthentication);
        verify(tokenProvider).generateRefreshToken("testuser");
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        when(userService.createUser(signUpRequest)).thenReturn(mockUser);

        // When
        User result = authService.registerUser(signUpRequest);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        
        verify(userService).createUser(signUpRequest);
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn("testuser");
        when(tokenProvider.generateToken("testuser")).thenReturn("new-access-token");
        when(tokenProvider.generateRefreshToken("testuser")).thenReturn("new-refresh-token");
        when(tokenProvider.getExpirationTime(anyString())).thenReturn(3600000L);

        // When
        JwtAuthenticationResponse response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        
        verify(tokenProvider).validateToken(refreshToken);
        verify(tokenProvider).getUsernameFromToken(refreshToken);
        verify(tokenProvider).generateToken("testuser");
        verify(tokenProvider).generateRefreshToken("testuser");
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Given
        String invalidToken = "invalid-refresh-token";
        when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authService.refreshToken(invalidToken);
        });
        
        verify(tokenProvider).validateToken(invalidToken);
        verify(tokenProvider, never()).getUsernameFromToken(anyString());
    }

    @Test
    void testGetCurrentUser_Success() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // When
        User result = authService.getCurrentUser();

        // Then
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authService.getCurrentUser();
        });
    }
}
