package com.fujitsu.delivery.repository;

import com.fujitsu.delivery.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, UUID> {}