package com.fujitsu.delivery.dto;

import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.VehicleType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response representation of a regional base fee entry.
 */
public record RegionalBaseFeeResponse(UUID id,
                                      City city,
                                      VehicleType vehicleType,
                                      BigDecimal fee) {}
