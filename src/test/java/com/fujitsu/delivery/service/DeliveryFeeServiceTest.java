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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceTest {

    @Mock
    WeatherObservationRepository weatherRepo;
    @Mock
    RegionalBaseFeeRepository baseFeeRepo;
    @Mock
    WeatherFeeRuleRepository feeRuleRepo;

    @InjectMocks
    DeliveryFeeService service;

    // Reusable fee rules matching the default seeded data
    private final WeatherFeeRule tempBelow10 = rule(FeeConditionType.TEMPERATURE, null, -10.001, null, "SCOOTER,BIKE", "1.0", false);
    private final WeatherFeeRule tempMinus10To0 = rule(FeeConditionType.TEMPERATURE, -10.0, 0.0, null, "SCOOTER,BIKE", "0.5", false);
    private final WeatherFeeRule wind10To20 = rule(FeeConditionType.WIND_SPEED, 10.0, 20.0, null, "BIKE", "0.5", false);
    private final WeatherFeeRule windOver20 = rule(FeeConditionType.WIND_SPEED, 20.001, null, null, "BIKE", null, true);
    private final WeatherFeeRule snowSleet = rule(FeeConditionType.PHENOMENON, null, null, "snow,sleet", "SCOOTER,BIKE", "1.0", false);
    private final WeatherFeeRule rain = rule(FeeConditionType.PHENOMENON, null, null, "rain", "SCOOTER,BIKE", "0.5", false);
    private final WeatherFeeRule glazeHailThunder = rule(FeeConditionType.PHENOMENON, null, null, "glaze,hail,thunder", "SCOOTER,BIKE", null, true);

    @Test
    void tartuBike_snowAndColdTemp_shouldMatch4EurosFromSpec() {
        // Arrange
        givenWeather("Tartu-Tõravere", -2.1, 4.7, "Light snow shower");
        givenBaseFee(City.TARTU, VehicleType.BIKE, "2.5");
        givenAllFeeRules();

        // Act
        BigDecimal fee = service.calculateFee(City.TARTU, VehicleType.BIKE, null);

        // Assert
        assertThat(fee).isEqualByComparingTo("4.0");
    }

    @Test
    void tallinnCar_neverHasExtraFees_regardlessOfWeather() {
        // Arrange
        givenWeather("Tallinn-Harku", -15.0, 30.0, "Thunder");
        givenBaseFee(City.TALLINN, VehicleType.CAR, "4.0");

        // Act
        BigDecimal fee = service.calculateFee(City.TALLINN, VehicleType.CAR, null);

        // Assert
        assertThat(fee).isEqualByComparingTo("4.0");
        verifyNoInteractions(feeRuleRepo); // confirms CAR never hits the rule table
    }

    @Test
    void tempBelow10_scooter_shouldAdd1Euro() {
        // Arrange
        givenWeather("Tallinn-Harku", -11.0, 5.0, "Clear");
        givenBaseFee(City.TALLINN, VehicleType.SCOOTER, "3.5");
        when(feeRuleRepo.findByConditionType(FeeConditionType.TEMPERATURE))
                .thenReturn(List.of(tempBelow10, tempMinus10To0));
        when(feeRuleRepo.findByConditionType(FeeConditionType.PHENOMENON)).thenReturn(List.of());

        // Act & Assert
        assertThat(service.calculateFee(City.TALLINN, VehicleType.SCOOTER, null))
                .isEqualByComparingTo("4.5"); // 3.5 + 1.0
    }

    @Test
    void tempExactlyMinus10_shouldAdd0Point5Euro_notOneEuro() {
        // boundary test: -10 must fall in the -10..0 rule (0.5€), NOT the < -10 rule (1€)
        // Arrange
        givenWeather("Tallinn-Harku", -10.0, 5.0, "Clear");
        givenBaseFee(City.TALLINN, VehicleType.BIKE, "3.0");
        when(feeRuleRepo.findByConditionType(FeeConditionType.TEMPERATURE))
                .thenReturn(List.of(tempBelow10, tempMinus10To0));
        when(feeRuleRepo.findByConditionType(FeeConditionType.WIND_SPEED)).thenReturn(List.of());
        when(feeRuleRepo.findByConditionType(FeeConditionType.PHENOMENON)).thenReturn(List.of());

        // Act & Assert
        assertThat(service.calculateFee(City.TALLINN, VehicleType.BIKE, null))
                .isEqualByComparingTo("3.5"); // 3.0 + 0.5
    }

    @Test
    void windOver20_bike_shouldThrowForbiddenVehicleException() {
        // Arrange
        givenWeather("Tallinn-Harku", 5.0, 25.0, "Clear");
        givenBaseFee(City.TALLINN, VehicleType.BIKE, "3.0");
        when(feeRuleRepo.findByConditionType(FeeConditionType.TEMPERATURE)).thenReturn(List.of());
        when(feeRuleRepo.findByConditionType(FeeConditionType.WIND_SPEED))
                .thenReturn(List.of(wind10To20, windOver20));

        // Act & Assert
        assertThatThrownBy(() -> service.calculateFee(City.TALLINN, VehicleType.BIKE, null))
                .isInstanceOf(ForbiddenVehicleException.class)
                .hasMessage("Usage of selected vehicle type is forbidden!");
    }

    @Test
    void thunderPhenomenon_scooter_shouldThrowForbiddenVehicleException() {
        // Arrange
        givenWeather("Pärnu", 10.0, 5.0, "Thunder");
        givenBaseFee(City.PÄRNU, VehicleType.SCOOTER, "2.5");
        when(feeRuleRepo.findByConditionType(FeeConditionType.TEMPERATURE)).thenReturn(List.of());
        when(feeRuleRepo.findByConditionType(FeeConditionType.PHENOMENON))
                .thenReturn(List.of(snowSleet, rain, glazeHailThunder));

        // Act & Assert
        assertThatThrownBy(() -> service.calculateFee(City.PÄRNU, VehicleType.SCOOTER, null))
                .isInstanceOf(ForbiddenVehicleException.class);
    }

    @Test
    void noWeatherData_shouldThrowNoWeatherDataException() {
        // Arrange
        when(weatherRepo.findTopByStationNameOrderByObservedAtDesc(any()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.calculateFee(City.TALLINN, VehicleType.CAR, null))
                .isInstanceOf(NoWeatherDataException.class)
                .hasMessageContaining("TALLINN");
    }

    @Test
    void rain_bike_shouldAdd0Point5Euro() {
        // Arrange
        givenWeather("Tartu-Tõravere", 5.0, 3.0, "Moderate rain");
        givenBaseFee(City.TARTU, VehicleType.BIKE, "2.5");
        when(feeRuleRepo.findByConditionType(FeeConditionType.TEMPERATURE)).thenReturn(List.of());
        when(feeRuleRepo.findByConditionType(FeeConditionType.WIND_SPEED)).thenReturn(List.of());
        when(feeRuleRepo.findByConditionType(FeeConditionType.PHENOMENON))
                .thenReturn(List.of(snowSleet, rain, glazeHailThunder));

        // Act & Assert
        assertThat(service.calculateFee(City.TARTU, VehicleType.BIKE, null))
                .isEqualByComparingTo("3.0"); // 2.5 + 0.5
    }

    // helpers

    private void givenWeather(String station, Double temp, Double wind, String phenomenon) {
        WeatherObservation w = new WeatherObservation();
        w.setStationName(station);
        w.setAirTemperature(temp);
        w.setWindSpeed(wind);
        w.setWeatherPhenomenon(phenomenon);
        w.setObservedAt(Instant.now());
        when(weatherRepo.findTopByStationNameOrderByObservedAtDesc(station))
                .thenReturn(Optional.of(w));
    }

    private void givenBaseFee(City city, VehicleType type, String amount) {
        RegionalBaseFee f = new RegionalBaseFee();
        f.setCity(city);
        f.setVehicleType(type);
        f.setFee(new BigDecimal(amount));
        when(baseFeeRepo.findByCityAndVehicleType(city, type)).thenReturn(Optional.of(f));
    }

    private void givenAllFeeRules() {
        when(feeRuleRepo.findByConditionType(FeeConditionType.TEMPERATURE))
                .thenReturn(List.of(tempBelow10, tempMinus10To0));
        when(feeRuleRepo.findByConditionType(FeeConditionType.WIND_SPEED))
                .thenReturn(List.of(wind10To20, windOver20));
        when(feeRuleRepo.findByConditionType(FeeConditionType.PHENOMENON))
                .thenReturn(List.of(snowSleet, rain, glazeHailThunder));
    }

    private WeatherFeeRule rule(FeeConditionType type,
                                Double min,
                                Double max,
                                String keywords,
                                String vehicles,
                                String amount,
                                boolean forbidden) {
        WeatherFeeRule r = new WeatherFeeRule();
        r.setConditionType(type);
        r.setMinValue(min);
        r.setMaxValue(max);
        r.setPhenomenonKeywords(keywords);
        r.setApplicableVehicles(vehicles);
        r.setFee(amount != null ? new BigDecimal(amount) : null);
        r.setForbidden(forbidden);

        return r;
    }
}
