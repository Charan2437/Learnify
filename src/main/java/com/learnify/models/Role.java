package com.learnify.models;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;
    
    public String getAuthority() {
        return name();
    }
    
    public String getRoleName() {
        return name().substring("ROLE_".length());
    }
}