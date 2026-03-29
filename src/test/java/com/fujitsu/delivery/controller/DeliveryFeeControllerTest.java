package com.fujitsu.delivery.controller;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeliveryFeeControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WeatherObservationRepository weatherRepo;

    @BeforeEach
    void seedWeather() {
        weatherRepo.deleteAll(); // clear any persisted data from previous runs

        WeatherObservation w = new WeatherObservation();
        w.setStationName("Tallinn-Harku");
        w.setAirTemperature(5.0);
        w.setWindSpeed(3.0);
        w.setWeatherPhenomenon("Clear");
        w.setObservedAt(Instant.now());
        weatherRepo.save(w);
    }

    @Test
    void tallinnCar_shouldReturn200WithFee4() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(4.0));
    }

    @Test
    void tallinnScooter_coldWeather_shouldIncludeTemperatureExtra() throws Exception {
        // Arrange
        // replaces @BeforeEach weather with a colder observation (newer timestamp wins)
        WeatherObservation cold = new WeatherObservation();
        cold.setStationName("Tallinn-Harku");
        cold.setAirTemperature(-11.0); // below -10 → ATEF = 1€
        cold.setWindSpeed(3.0);
        cold.setWeatherPhenomenon("Clear");
        cold.setObservedAt(Instant.now().plusSeconds(1));
        weatherRepo.save(cold);

        // Act & Assert
        mockMvc.perform(get("/api/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "SCOOTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(4.5));
    }

    @Test
    void invalidCity_shouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/delivery-fee")
                        .param("city", "LONDON")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noWeatherData_shouldReturn404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/delivery-fee")
                        .param("city", "TARTU")
                        .param("vehicleType", "BIKE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void forbiddenVehicle_shouldReturn422() throws Exception {
        // Arrange
        WeatherObservation highWind = new WeatherObservation();
        highWind.setStationName("Tallinn-Harku");
        highWind.setAirTemperature(5.0);
        highWind.setWindSpeed(25.0);
        highWind.setWeatherPhenomenon("Clear");
        highWind.setObservedAt(Instant.now().plusSeconds(1));
        weatherRepo.save(highWind);

        // Act & Assert
        mockMvc.perform(get("/api/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "BIKE"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.error").value("Usage of selected vehicle type is forbidden!"));
    }
}
