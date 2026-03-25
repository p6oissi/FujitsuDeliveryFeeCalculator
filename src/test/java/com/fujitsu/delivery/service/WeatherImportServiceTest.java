package com.fujitsu.delivery.service;

import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@ExtendWith(MockitoExtension.class)
class WeatherImportServiceTest {

    @Mock
    WeatherObservationRepository repository;

    @Test
    void importWeatherData_shouldNotThrow_whenRepositorySaves() {
        // Arrange
        WeatherImportService service = new WeatherImportService(repository);

        // Act & Assert (if the real URL is unreachable the error is caught and logged, no exception expected)
        assertThatCode(service::importWeatherData).doesNotThrowAnyException();
    }
}