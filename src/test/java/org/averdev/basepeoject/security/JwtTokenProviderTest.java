package org.averdev.basepeoject.security;

import org.averdev.basepeoject.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringJUnitExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringJUnitExtension.class)
public class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "testSecretKeyForJWTTokenGenerationThatShouldBeLongEnough");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 3600000);
        ReflectionTestUtils.setField(tokenProvider, "refreshExpirationInMs", 86400000);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities())
                .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGenerateToken() {
        String token = tokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void testGenerateTokenFromUsername() {
        String token = tokenProvider.generateToken("testuser");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("testuser", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    void testGenerateRefreshToken() {
        String refreshToken = tokenProvider.generateRefreshToken("testuser");

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertEquals("testuser", tokenProvider.getUsernameFromToken(refreshToken));
    }

    @Test
    void testGetUsernameFromToken() {
        String token = tokenProvider.generateToken("testuser");

        String username = tokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void testGetExpirationDateFromToken() {
        String token = tokenProvider.generateToken("testuser");

        Long expirationTime = tokenProvider.getExpirationTime(token);

        assertNotNull(expirationTime);
        assertTrue(expirationTime > 0);
        assertTrue(expirationTime <= 3600000); // Should be less than or equal to expiration time
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = tokenProvider.generateToken("testuser");

        boolean isValid = tokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = tokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Create a token with very short expiration
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 1);
        String expiredToken = tokenProvider.generateToken("testuser");

        // Wait a bit to ensure token is expired
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isValid = tokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithUserDetails() {
        String token = tokenProvider.generateToken("testuser");
        org.springframework.security.core.userdetails.UserDetails userDetails = 
                new org.springframework.security.core.userdetails.User(
                        "testuser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean isValid = tokenProvider.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_WithWrongUserDetails() {
        String token = tokenProvider.generateToken("testuser");
        org.springframework.security.core.userdetails.UserDetails userDetails = 
                new org.springframework.security.core.userdetails.User(
                        "differentuser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean isValid = tokenProvider.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testGetClaimFromToken() {
        String token = tokenProvider.generateToken("testuser");

        String username = tokenProvider.getClaimFromToken(token, claims -> claims.getSubject());

        assertEquals("testuser", username);
    }
}
