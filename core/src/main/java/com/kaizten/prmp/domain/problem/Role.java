package com.kaizten.prmp.domain.problem;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public enum Role {

    AGENT,
    DRIVER,
    RAMP_MANAGER,
    MANAGER;

    public static Role fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new NullPointerException();
        }
        stringToCheck = stringToCheck.trim();
        String upperCasedStringToCheck = stringToCheck.toUpperCase();
        for (Role role : values()) {
            if (role.name().equals(upperCasedStringToCheck)) {
                return role;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int indexOf(String stringToCheck) {
        return Role
                .fromString(stringToCheck)
                .ordinal();
    }

    public static boolean isValid(String stringToCheck) {
        final Set<String> values = new HashSet<>();
        for (Role value : Role.values()) {
            values.add(value.name());
        }
        return values.contains(stringToCheck);
    }

    public static Role random() {
        return values()[new Random().nextInt(values().length)];
    }
}
