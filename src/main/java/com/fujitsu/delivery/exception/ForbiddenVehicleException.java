package com.fujitsu.delivery.exception;

public class ForbiddenVehicleException extends RuntimeException {
    public ForbiddenVehicleException() {
        super("Usage of selected vehicle type is forbidden!");
    }
}
