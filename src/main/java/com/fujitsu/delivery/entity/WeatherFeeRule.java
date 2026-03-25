package com.fujitsu.delivery.entity;

import com.fujitsu.delivery.enums.FeeConditionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class WeatherFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeConditionType conditionType;

    // Inclusive range for TEMPERATURE / WIND_SPEED rules.
    private Double minValue;
    private Double maxValue;

    // Comma-separated weather words to match, e.g. "snow,sleet" or "glaze,hail,thunder"
    private String phenomenonKeywords;

    // Comma-separated vehicle types this rule applies to, e.g. "SCOOTER,BIKE"
    @Column(nullable = false)
    private String applicableVehicles;

    // Extra charge in euros. Ignored when forbidden=true.
    private BigDecimal fee;

    // If true, vehicle use is not allowed under this condition (instead of adding a fee).
    @Column(nullable = false)
    private boolean forbidden;

    public WeatherFeeRule() {}

    public UUID getId() { return id; }

    public FeeConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(FeeConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getPhenomenonKeywords() {
        return phenomenonKeywords;
    }

    public void setPhenomenonKeywords(String phenomenonKeywords) {
        this.phenomenonKeywords = phenomenonKeywords;
    }

    public String getApplicableVehicles() {
        return applicableVehicles;
    }

    public void setApplicableVehicles(String applicableVehicles) {
        this.applicableVehicles = applicableVehicles;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }
}
