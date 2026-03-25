package com.fujitsu.delivery.controller;

import com.fujitsu.delivery.service.WeatherImportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final WeatherImportService weatherImportService;

    public AdminController(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /** Manually triggers a weather data import (for testing without waiting for cron). */
    @PostMapping("/weather/import")
    @ResponseStatus(HttpStatus.OK)
    public void triggerImport() {
        weatherImportService.importWeatherData();
    }
}
