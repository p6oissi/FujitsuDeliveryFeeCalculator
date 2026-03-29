package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.RegionalBaseFee;
import com.fujitsu.delivery.repository.RegionalBaseFeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BaseFeeService {

    private final RegionalBaseFeeRepository baseFeeRepository;

    public BaseFeeService(RegionalBaseFeeRepository baseFeeRepository) {
        this.baseFeeRepository = baseFeeRepository;
    }

    /** Returns all configured regional base fees. */
    public List<RegionalBaseFee> findAll() {
        return baseFeeRepository.findAll();
    }

    /** Returns the base fee with the given ID. Throws {@link IllegalArgumentException} if not found. */
    public RegionalBaseFee findById(UUID id) {
        return baseFeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Base fee not found: " + id));
    }

    /** Creates a new regional base fee entry. */
    public RegionalBaseFee create(RegionalBaseFee fee) {
        return baseFeeRepository.save(fee);
    }

    /** Updates city, vehicleType, and fee of an existing entry. Throws {@link IllegalArgumentException} if not found. */
    public RegionalBaseFee update(UUID id, RegionalBaseFee updated) {
        RegionalBaseFee existing = findById(id);
        existing.setCity(updated.getCity());
        existing.setVehicleType(updated.getVehicleType());
        existing.setFee(updated.getFee());

        return baseFeeRepository.save(existing);
    }

    /** Deletes the base fee with the given ID. Does nothing if the ID does not exist. */
    public void delete(UUID id) {
        baseFeeRepository.deleteById(id);
    }
}
