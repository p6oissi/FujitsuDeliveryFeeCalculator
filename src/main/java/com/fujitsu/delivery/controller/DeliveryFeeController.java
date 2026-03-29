package com.fujitsu.delivery.controller;

import com.fujitsu.delivery.dto.DeliveryFeeResponse;
import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.VehicleType;
import com.fujitsu.delivery.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/delivery-fee")
@Tag(name = "Delivery Fee", description = "Calculate delivery fee based on city and vehicle type")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Calculates the total delivery fee for the given city and vehicle type.
     * Uses the latest available weather data unless {@code dateTime} is specified.
     *
     * @param city        the delivery city (TALLINN, TARTU, PÄRNU)
     * @param vehicleType the courier's vehicle type (CAR, SCOOTER, BIKE)
     * @param dateTime    optional datetime for historical calculation (e.g. 2024-03-20T12:00:00)
     * @return total delivery fee in euros
     */
    @GetMapping
    @Operation(summary = "Calculate delivery fee",
            description = "Returns total fee. Provide dateTime for historical weather-based calculation.")
    public ResponseEntity<DeliveryFeeResponse> getDeliveryFee(
            @RequestParam City city,
            @RequestParam VehicleType vehicleType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        Instant at = (dateTime != null) ? dateTime.toInstant(ZoneOffset.UTC) : null;
        BigDecimal fee = deliveryFeeService.calculateFee(city, vehicleType, at);
        return ResponseEntity.ok(new DeliveryFeeResponse(fee));
    }
}
