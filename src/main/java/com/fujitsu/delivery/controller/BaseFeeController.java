package com.fujitsu.delivery.controller;

import com.fujitsu.delivery.dto.RegionalBaseFeeRequest;
import com.fujitsu.delivery.dto.RegionalBaseFeeResponse;
import com.fujitsu.delivery.entity.RegionalBaseFee;
import com.fujitsu.delivery.service.BaseFeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/base-fees")
@Tag(name = "Base Fees", description = "Manage regional base fees (CRUD)")
public class BaseFeeController {

    private final BaseFeeService service;

    public BaseFeeController(BaseFeeService service) {
        this.service = service;
    }

    /** Returns all base fees. */
    @GetMapping
    public List<RegionalBaseFeeResponse> getAll() {
        return service.findAll().stream().map(BaseFeeController::toResponse).toList();
    }

    /** Returns one base fee by ID. Returns 400 if the ID does not exist. */
    @GetMapping("/{id}")
    public RegionalBaseFeeResponse getById(@PathVariable UUID id) {
        return toResponse(service.findById(id));
    }

    /** Creates a new base fee entry. Returns 201 with the saved entry. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegionalBaseFeeResponse create(@RequestBody RegionalBaseFeeRequest request) {
        return toResponse(service.create(toEntity(request)));
    }

    /** Updates an existing base fee entry. Returns 400 if the ID does not exist. */
    @PutMapping("/{id}")
    public RegionalBaseFeeResponse update(@PathVariable UUID id, @RequestBody RegionalBaseFeeRequest request) {
        return toResponse(service.update(id, toEntity(request)));
    }

    /** Deletes a base fee entry. Returns 204 with no body. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private static RegionalBaseFeeResponse toResponse(RegionalBaseFee fee) {
        return new RegionalBaseFeeResponse(fee.getId(), fee.getCity(), fee.getVehicleType(), fee.getFee());
    }

    private static RegionalBaseFee toEntity(RegionalBaseFeeRequest request) {
        RegionalBaseFee fee = new RegionalBaseFee();
        fee.setCity(request.city());
        fee.setVehicleType(request.vehicleType());
        fee.setFee(request.fee());
        return fee;
    }
}
