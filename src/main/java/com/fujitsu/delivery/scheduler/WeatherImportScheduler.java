package com.fujitsu.delivery.scheduler;

import com.fujitsu.delivery.service.WeatherImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherImportScheduler.class);

    private final WeatherImportService weatherImportService;

    public WeatherImportScheduler(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /**
     * Imports weather data on a configurable cron schedule.
     * Default: every hour at HH:15:00 (see weather.import.cron in application.properties).
     */
    @Scheduled(cron = "${weather.import.cron}")
    public void runImport() {
        log.info("Starting scheduled weather data import");
        weatherImportService.importWeatherData();
    }
}
