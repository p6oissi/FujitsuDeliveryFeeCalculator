package com.fujitsu.delivery.enums;

public enum City {
    TALLINN,
    TARTU,
    PÄRNU;

    /** Returns the weather station name as used in the Estonian Environment Agency feed. */
    public String stationName() {
        return switch (this) {
            case TALLINN -> "Tallinn-Harku";
            case TARTU -> "Tartu-Tõravere";
            case PÄRNU -> "Pärnu";
        };
    }
}
