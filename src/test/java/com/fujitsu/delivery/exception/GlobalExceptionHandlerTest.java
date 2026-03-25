package com.fujitsu.delivery.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void forbiddenVehicle_shouldReturn422() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ForbiddenVehicleException exception = new ForbiddenVehicleException();

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleForbidden(exception);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody())
                .isNotNull()
                .containsEntry("error", "Usage of selected vehicle type is forbidden!");
    }

    @Test
    void noWeatherData_shouldReturn404AndIncludeCityInMessage() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        NoWeatherDataException exception = new NoWeatherDataException("TARTU");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleNoData(exception);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody())
                .isNotNull()
                .containsEntry("error", "No weather data available for city: TARTU");
    }
}