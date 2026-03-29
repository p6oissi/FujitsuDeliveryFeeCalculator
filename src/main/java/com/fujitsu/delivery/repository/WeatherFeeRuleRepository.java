package com.fujitsu.delivery.repository;

import com.fujitsu.delivery.entity.WeatherFeeRule;
import com.fujitsu.delivery.enums.FeeConditionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WeatherFeeRuleRepository extends JpaRepository<WeatherFeeRule, UUID> {
    List<WeatherFeeRule> findByConditionType(FeeConditionType conditionType);
}
