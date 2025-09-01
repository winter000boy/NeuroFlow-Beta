package com.jobapp.auth.model;

public enum Role {
    CANDIDATE("ROLE_CANDIDATE"),
    EMPLOYER("ROLE_EMPLOYER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return authority;
    }
}