package org.averdev.basepeoject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.averdev.basepeoject.validation.annotation.StrongPassword;
import org.averdev.basepeoject.validation.annotation.UniqueEmail;
import org.averdev.basepeoject.validation.annotation.UniqueUsername;

import java.util.Set;

public class SignUpRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    @UniqueUsername
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @UniqueEmail
    private String email;
    
    @NotBlank
    @StrongPassword
    private String password;
    
    private Set<String> roles;
    
    public SignUpRequest() {}
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
