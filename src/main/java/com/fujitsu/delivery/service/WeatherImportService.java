package com.fujitsu.delivery.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import com.fujitsu.delivery.xml.Observations;
import com.fujitsu.delivery.xml.StationData;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class WeatherImportService {

    private static final Logger log = LoggerFactory.getLogger(WeatherImportService.class);

    private static final String WEATHER_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private static final Set<String> TARGET_STATIONS = Set.of(
            "Tallinn-Harku",
            "Tartu-Tõravere",
            "Pärnu"
    );

    private final WeatherObservationRepository repository;
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    public WeatherImportService(WeatherObservationRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Fetches current weather observations from the Estonian Environment Agency
     * and persists data for Tallinn-Harku, Tartu-Tõravere, and Pärnu stations.
     * Each call inserts new rows and existing observations are never overwritten.
     */
    public void importWeatherData() {
        try {
            String xml = restTemplate.getForObject(WEATHER_URL, String.class);
            Observations observations = xmlMapper.readValue(xml, Observations.class);
            Instant observedAt = Instant.ofEpochSecond(observations.timestamp);

            List<WeatherObservation> entities = observations.stations.stream()
                    .filter(s -> s.name != null && TARGET_STATIONS.contains(s.name))
                    .map(s -> toEntity(s, observedAt))
                    .toList();

            repository.saveAll(entities);
            log.info("Weather import complete: {} stations saved at {}", entities.size(), observedAt);

        } catch (Exception e) {
            log.error("Weather import failed: {}", e.getMessage(), e);
        }
    }

    private WeatherObservation toEntity(StationData s, Instant observedAt) {
        WeatherObservation obs = new WeatherObservation();
        obs.setStationName(s.name);
        obs.setWmoCode(s.wmocode);
        obs.setAirTemperature(s.airtemperature);
        obs.setWindSpeed(s.windspeed);
        obs.setWeatherPhenomenon(s.phenomenon);
        obs.setObservedAt(observedAt);
        return obs;
    }


}
