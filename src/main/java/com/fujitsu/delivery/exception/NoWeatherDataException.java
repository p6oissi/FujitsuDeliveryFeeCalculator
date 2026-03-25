package com.fujitsu.delivery.exception;

public class NoWeatherDataException extends RuntimeException {
    public NoWeatherDataException(String city) {
        super("No weather data available for city: " + city);
    }
}
