package com.fujitsu.delivery.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WeatherXmlParsingTest {

    @Test
    void shouldParseObservationsXml() throws Exception {
        // Arrange
        String xml = """
                <observations timestamp="1711234567">
                  <station>
                    <name>Tallinn-Harku</name>
                    <wmocode>26038</wmocode>
                    <airtemperature>-2.1</airtemperature>
                    <windspeed>3.5</windspeed>
                    <phenomenon>Light snow shower</phenomenon>
                    <someOtherField>ignored</someOtherField>
                  </station>
                </observations>
                """;
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Act
        Observations obs = xmlMapper.readValue(xml, Observations.class);

        // Assert
        assertThat(obs.timestamp).isEqualTo(1711234567L);
        assertThat(obs.stations).hasSize(1);
        assertThat(obs.stations.get(0).name).isEqualTo("Tallinn-Harku");
        assertThat(obs.stations.get(0).airtemperature).isEqualTo(-2.1);
        assertThat(obs.stations.get(0).windspeed).isEqualTo(3.5);
        assertThat(obs.stations.get(0).phenomenon).isEqualTo("Light snow shower");
    }
}
