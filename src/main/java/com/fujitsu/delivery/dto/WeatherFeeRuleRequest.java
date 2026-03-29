package com.fujitsu.delivery.dto;

import com.fujitsu.delivery.enums.FeeConditionType;

import java.math.BigDecimal;

/**
 * Request body for creating or updating a weather-based extra fee rule.
 */
public record WeatherFeeRuleRequest(FeeConditionType conditionType,
                                    Double minValue,
                                    Double maxValue,
                                    String phenomenonKeywords,
                                    String applicableVehicles,
                                    BigDecimal fee,
                                    boolean forbidden) {}
