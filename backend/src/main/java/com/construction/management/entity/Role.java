package com.construction.management.entity;

public enum Role {
    OWNER;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
