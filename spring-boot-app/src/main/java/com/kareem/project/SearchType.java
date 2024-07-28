package com.kareem.project;

public enum SearchType {
    NORMAL,
    WILDCARD,
    REGEX,
    NEURAL;

    // Static method to map integer to enum
    public static SearchType fromInt(int value) {
        switch (value) {
            case 0:
                return NORMAL;
            case 1:
                return WILDCARD;
            case 2:
                return REGEX;
            case 3:
                return NEURAL;
            default:
                throw new IllegalArgumentException("Invalid value: " + value);
        }
    }
}
