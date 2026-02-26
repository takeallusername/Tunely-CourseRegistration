package com.tunely.domain.enums;

public enum DifficultyLevel {
    EASY(100),
    MEDIUM(500),
    HARD(1000),
    VERY_HARD(5000),
    EXTREME(10000);

    private final int virtualUsers;

    DifficultyLevel(int virtualUsers) {
        this.virtualUsers = virtualUsers;
    }

    public int getVirtualUsers() {
        return virtualUsers;
    }
}
