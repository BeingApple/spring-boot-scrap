package com.yongbi.szsyongbi.security.domain;

import java.util.Arrays;

public enum Roles {
    ROLE_USER("ROLE_USER");

    private final String value;

    Roles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean canCast(final String value) {
        final var roleNames = Arrays.stream(Roles.values())
            .map(Roles::name)
            .toList();

        return roleNames.contains(value);
    }
}
