package org.averdev.basepeoject.common.enums;

public enum UserRole {
    USER("USER", "Regular user with limited access"),
    ADMIN("ADMIN", "Administrator with full access"),
    MANAGER("MANAGER", "Manager with intermediate access");

    private final String name;
    private final String description;

    UserRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromName(String name) {
        for (UserRole role : UserRole.values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + name);
    }
}
