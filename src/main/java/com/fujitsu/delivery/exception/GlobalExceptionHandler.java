package com.fujitsu.delivery.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenVehicleException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenVehicleException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(422)).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NoWeatherDataException.class)
    public ResponseEntity<Map<String, String>> handleNoData(NoWeatherDataException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(Map.of("error", ex.getMessage()));
    }
}
