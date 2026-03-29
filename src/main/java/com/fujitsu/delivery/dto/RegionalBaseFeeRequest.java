package com.fujitsu.delivery.dto;

import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.VehicleType;

import java.math.BigDecimal;

/**
 * Request body for creating or updating a regional base fee.
 */
public record RegionalBaseFeeRequest(City city,
                                     VehicleType vehicleType,
                                     BigDecimal fee) {}
