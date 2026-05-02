package org.averdev.basepeoject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.averdev.basepeoject.common.audit.BaseEntity;

@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {
    
    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String name;
    
    @Size(max = 200)
    private String description;
    
    public Permission() {}
    
    public Permission(String name) {
        this.name = name;
    }
    
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
