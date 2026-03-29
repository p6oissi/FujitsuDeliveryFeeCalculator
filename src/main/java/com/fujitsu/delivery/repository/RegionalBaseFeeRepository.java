package com.fujitsu.delivery.repository;

import com.fujitsu.delivery.entity.RegionalBaseFee;
import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, UUID> {

    Optional<RegionalBaseFee> findByCityAndVehicleType(City city, VehicleType vehicleType);
}
