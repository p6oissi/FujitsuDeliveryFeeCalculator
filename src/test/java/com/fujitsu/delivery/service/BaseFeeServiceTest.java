package com.fujitsu.delivery.service;

import com.fujitsu.delivery.entity.RegionalBaseFee;
import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.VehicleType;
import com.fujitsu.delivery.repository.RegionalBaseFeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseFeeServiceTest {

    @Mock
    RegionalBaseFeeRepository repo;

    @InjectMocks
    BaseFeeService service;

    @Test
    void findById_unknownId_shouldThrowIllegalArgumentException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_shouldApplyAllFieldsIncludingFee() {
        // Arrange
        UUID id = UUID.randomUUID();
        RegionalBaseFee existing = baseFee(City.TALLINN, VehicleType.CAR, "4.0");
        RegionalBaseFee incoming = baseFee(City.TARTU, VehicleType.BIKE, "2.5");
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        RegionalBaseFee result = service.update(id, incoming);

        // Assert
        assertThat(result.getCity()).isEqualTo(City.TARTU);
        assertThat(result.getVehicleType()).isEqualTo(VehicleType.BIKE);
        assertThat(result.getFee()).isEqualByComparingTo("2.5");
    }

    @Test
    void delete_shouldDelegateToRepository() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        service.delete(id);

        // Assert
        verify(repo).deleteById(id);
    }

    private RegionalBaseFee baseFee(City city, VehicleType vehicleType, String fee) {
        RegionalBaseFee f = new RegionalBaseFee();
        f.setCity(city);
        f.setVehicleType(vehicleType);
        f.setFee(new BigDecimal(fee));
        return f;
    }
}
