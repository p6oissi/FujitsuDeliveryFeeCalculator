package com.fujitsu.delivery.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CityTest {

    @Test
    void stationName_shouldReturnCorrectStation_forEachCity() {
        // Act & Assert
        assertThat(City.TALLINN.stationName()).isEqualTo("Tallinn-Harku");
        assertThat(City.TARTU.stationName()).isEqualTo("Tartu-Tõravere");
        assertThat(City.PÄRNU.stationName()).isEqualTo("Pärnu");
    }

}