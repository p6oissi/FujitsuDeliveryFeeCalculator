package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.WeatherFeeRule;
import com.fujitsu.delivery.repository.WeatherFeeRuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WeatherFeeRuleService {

    private final WeatherFeeRuleRepository repository;

    public WeatherFeeRuleService(WeatherFeeRuleRepository repository) {
        this.repository = repository;
    }

    /** Returns all configured weather fee rules. */
    public List<WeatherFeeRule> findAll() {
        return repository.findAll();
    }

    /** Returns the rule with the given ID. Throws {@link IllegalArgumentException} if not found. */
    public WeatherFeeRule findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fee rule not found: " + id));
    }

    /** Creates a new weather extra fee rule. */
    public WeatherFeeRule create(WeatherFeeRule rule) {
        return repository.save(rule);
    }

    /** Replaces all fields of an existing rule. Throws {@link IllegalArgumentException} if not found. */
    public WeatherFeeRule update(UUID id, WeatherFeeRule updated) {
        WeatherFeeRule existing = findById(id);
        existing.setConditionType(updated.getConditionType());
        existing.setMinValue(updated.getMinValue());
        existing.setMaxValue(updated.getMaxValue());
        existing.setPhenomenonKeywords(updated.getPhenomenonKeywords());
        existing.setApplicableVehicles(updated.getApplicableVehicles());
        existing.setFee(updated.getFee());
        existing.setForbidden(updated.isForbidden());
        return repository.save(existing);
    }

    /** Deletes the rule with the given ID. Does nothing if the ID does not exist. */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
