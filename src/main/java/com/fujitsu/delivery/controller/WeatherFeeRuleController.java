package com.fujitsu.delivery.controller;

import com.fujitsu.delivery.dto.WeatherFeeRuleRequest;
import com.fujitsu.delivery.dto.WeatherFeeRuleResponse;
import com.fujitsu.delivery.entity.WeatherFeeRule;
import com.fujitsu.delivery.service.WeatherFeeRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/weather-fee-rules")
@Tag(name = "Weather Fee Rules", description = "Manage weather-based extra fee rules (CRUD)")
public class WeatherFeeRuleController {

    private final WeatherFeeRuleService service;

    public WeatherFeeRuleController(WeatherFeeRuleService service) {
        this.service = service;
    }

    /** Returns all weather fee rules. */
    @GetMapping
    public List<WeatherFeeRuleResponse> getAll() {
        return service.findAll().stream().map(WeatherFeeRuleController::toResponse).toList();
    }

    /** Returns one weather fee rule by ID. Returns 400 if the ID does not exist. */
    @GetMapping("/{id}")
    public WeatherFeeRuleResponse getById(@PathVariable UUID id) {
        return toResponse(service.findById(id));
    }

    /** Creates a new weather fee rule. Returns 201 with the saved rule. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherFeeRuleResponse create(@RequestBody WeatherFeeRuleRequest request) {
        return toResponse(service.create(toEntity(request)));
    }

    /** Replaces an existing weather fee rule. Returns 400 if the ID does not exist. */
    @PutMapping("/{id}")
    public WeatherFeeRuleResponse update(@PathVariable UUID id, @RequestBody WeatherFeeRuleRequest request) {
        return toResponse(service.update(id, toEntity(request)));
    }

    /** Deletes a weather fee rule. Returns 204 with no body. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private static WeatherFeeRuleResponse toResponse(WeatherFeeRule rule) {
        return new WeatherFeeRuleResponse(
                rule.getId(),
                rule.getConditionType(),
                rule.getMinValue(),
                rule.getMaxValue(),
                rule.getPhenomenonKeywords(),
                rule.getApplicableVehicles(),
                rule.getFee(),
                rule.isForbidden());
    }

    private static WeatherFeeRule toEntity(WeatherFeeRuleRequest request) {
        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setConditionType(request.conditionType());
        rule.setMinValue(request.minValue());
        rule.setMaxValue(request.maxValue());
        rule.setPhenomenonKeywords(request.phenomenonKeywords());
        rule.setApplicableVehicles(request.applicableVehicles());
        rule.setFee(request.fee());
        rule.setForbidden(request.forbidden());
        return rule;
    }
}
