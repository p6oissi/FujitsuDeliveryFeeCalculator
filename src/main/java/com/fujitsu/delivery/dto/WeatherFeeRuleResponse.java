package com.fujitsu.delivery.dto;

import com.fujitsu.delivery.enums.FeeConditionType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response representation of a weather-based extra fee rule.
 */
public record WeatherFeeRuleResponse(UUID id,
                                     FeeConditionType conditionType,
                                     Double minValue,
                                     Double maxValue,
                                     String phenomenonKeywords,
                                     String applicableVehicles,
                                     BigDecimal fee,
                                     boolean forbidden) {}
