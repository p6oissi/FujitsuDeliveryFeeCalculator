package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.RegionalBaseFee;
import com.fujitsu.delivery.entity.WeatherFeeRule;
import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.FeeConditionType;
import com.fujitsu.delivery.enums.VehicleType;
import com.fujitsu.delivery.exception.ForbiddenVehicleException;
import com.fujitsu.delivery.exception.NoWeatherDataException;
import com.fujitsu.delivery.repository.RegionalBaseFeeRepository;
import com.fujitsu.delivery.repository.WeatherFeeRuleRepository;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
public class DeliveryFeeService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryFeeService.class);

    private final WeatherObservationRepository weatherRepository;
    private final RegionalBaseFeeRepository baseFeeRepository;
    private final WeatherFeeRuleRepository feeRuleRepository;

    public DeliveryFeeService(WeatherObservationRepository weatherRepository,
                              RegionalBaseFeeRepository baseFeeRepository,
                              WeatherFeeRuleRepository feeRuleRepository) {
        this.weatherRepository = weatherRepository;
        this.baseFeeRepository = baseFeeRepository;
        this.feeRuleRepository = feeRuleRepository;
    }

    /**
     * Calculates the total delivery fee for the given city and vehicle type.
     * Uses the most recent weather observation, or data at {@code at} if provided.
     *
     * @param city        the city of delivery
     * @param vehicleType the courier's vehicle type
     * @param at          optional: calculate based on weather at this point in time
     * @return total delivery fee in euros
     * @throws ForbiddenVehicleException if weather conditions forbid the vehicle
     * @throws NoWeatherDataException    if no weather data is found for the city
     */
    public BigDecimal calculateFee(City city,
                                   VehicleType vehicleType,
                                   Instant at) {
        WeatherObservation weather = resolveWeather(city, at);
        log.debug("Fee calculation: {}/{} using observation from {}", city, vehicleType, weather.getObservedAt());

        BigDecimal regBaseFee = getRegionalBaseFee(city, vehicleType);
        BigDecimal airTempFee = calcTemperatureFee(weather.getAirTemperature(), vehicleType);
        BigDecimal windSpeedFee = calcWindSpeedFee(weather.getWindSpeed(), vehicleType);
        BigDecimal phenomenonFee = calcPhenomenonFee(weather.getWeatherPhenomenon(), vehicleType);

        return regBaseFee.add(airTempFee).add(windSpeedFee).add(phenomenonFee);
    }

    private WeatherObservation resolveWeather(City city, Instant at) {
        Optional<WeatherObservation> obs = (at != null)
                ? weatherRepository.findTopByStationNameAndObservedAtLessThanEqualOrderByObservedAtDesc(
                        city.stationName(), at)
                : weatherRepository.findTopByStationNameOrderByObservedAtDesc(city.stationName());

        return obs.orElseThrow(() -> new NoWeatherDataException(city.name()));
    }

    private BigDecimal getRegionalBaseFee(City city,
                                          VehicleType vehicleType) {
        return baseFeeRepository.findByCityAndVehicleType(city, vehicleType)
                .map(RegionalBaseFee::getFee)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No base fee configured for " + city + " / " + vehicleType
                ));
    }

    private BigDecimal calcTemperatureFee(Double airTemperature,
                                          VehicleType vehicleType) {
        if (airTemperature == null || isWeatherFeeExempt(vehicleType)) {
            return BigDecimal.ZERO;
        }
        return matchNumericRule(FeeConditionType.TEMPERATURE, airTemperature, vehicleType);
    }

    private BigDecimal calcWindSpeedFee(Double windSpeed,
                                        VehicleType vehicleType) {
        if (windSpeed == null || vehicleType != VehicleType.BIKE) return BigDecimal.ZERO;
        return matchNumericRule(FeeConditionType.WIND_SPEED, windSpeed, vehicleType);
    }

    private BigDecimal calcPhenomenonFee(String weatherPhenomenon,
                                         VehicleType vehicleType) {
        if (weatherPhenomenon == null || weatherPhenomenon.isBlank() || isWeatherFeeExempt(vehicleType)) {
            return BigDecimal.ZERO;
        }

        String lower = weatherPhenomenon.toLowerCase();

        return feeRuleRepository.findByConditionType(FeeConditionType.PHENOMENON).stream()
                .filter(rule -> appliesToVehicle(rule, vehicleType))
                .filter(rule -> matchesPhenomenon(lower, rule.getPhenomenonKeywords()))
                .findFirst()
                .map(this::applyRule)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal matchNumericRule(FeeConditionType feeConditionType,
                                        Double airTemperature,
                                        VehicleType vehicleType) {
        return feeRuleRepository.findByConditionType(feeConditionType).stream()
                .filter(rule -> appliesToVehicle(rule, vehicleType))
                .filter(rule -> inRange(
                        airTemperature,
                        rule.getMinValue(),
                        rule.getMaxValue()))
                .findFirst()
                .map(this::applyRule)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal applyRule(WeatherFeeRule weatherFeeRule) {
        if (weatherFeeRule.isForbidden()) throw new ForbiddenVehicleException();
        return weatherFeeRule.getFee() != null ? weatherFeeRule.getFee() : BigDecimal.ZERO;
    }

    private boolean isWeatherFeeExempt(VehicleType vehicleType) {
        return vehicleType != VehicleType.SCOOTER && vehicleType != VehicleType.BIKE;
    }

    private boolean appliesToVehicle(WeatherFeeRule rule,
                                     VehicleType vehicleType) {
        return Arrays.asList(rule.getApplicableVehicles().split(",")).contains(vehicleType.name());
    }

    /**
     * Returns true if value is within [min, max] (both inclusive).
     * A null bound means unbounded on that side.
     */
    private boolean inRange(Double airTemperature,
                            Double minValue,
                            Double maxValue) {
        if (minValue != null && airTemperature < minValue) return false;
        return maxValue == null || airTemperature <= maxValue;
    }

    private boolean matchesPhenomenon(String phenomenon,
                                      String phenomenonKeywords) {
    if (phenomenonKeywords == null || phenomenonKeywords.isBlank()) return false;

    return Arrays.stream(phenomenonKeywords.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .anyMatch(phenomenon::contains);
    }
}
