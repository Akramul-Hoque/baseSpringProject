package org.averdev.basepeoject.common.enums;

public enum Permission {
    READ_USERS("READ_USERS", "Read user information"),
    WRITE_USERS("WRITE_USERS", "Create and update users"),
    DELETE_USERS("DELETE_USERS", "Delete users"),
    READ_ROLES("READ_ROLES", "Read role information"),
    WRITE_ROLES("WRITE_ROLES", "Create and update roles"),
    DELETE_ROLES("DELETE_ROLES", "Delete roles"),
    ADMIN_PANEL("ADMIN_PANEL", "Access admin panel"),
    AUDIT_LOG("AUDIT_LOG", "View audit logs"),
    SYSTEM_CONFIG("SYSTEM_CONFIG", "Modify system configuration");

    private final String name;
    private final String description;

    Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Permission fromName(String name) {
        for (Permission permission : Permission.values()) {
            if (permission.name.equalsIgnoreCase(name)) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Unknown permission: " + name);
    }
}
