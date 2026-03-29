package com.fujitsu.delivery.repository;

import com.fujitsu.delivery.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, UUID> {

    /** Returns the most recent observation for a station. */
    Optional<WeatherObservation> findTopByStationNameOrderByObservedAtDesc(String stationName);

    /** Returns the most recent observation for a station at or before the given time. */
    Optional<WeatherObservation> findTopByStationNameAndObservedAtLessThanEqualOrderByObservedAtDesc(
            String stationName,
            Instant at
    );
}