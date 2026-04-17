package com.inventory.entity;

/**
 * Roles in the system.
 * Spring Security expects role names prefixed with "ROLE_" internally,
 * but hasRole("ADMIN") strips the prefix automatically.
 */
public enum Role {
    ADMIN,
    SUPPLIER,
    CUSTOMER
}
