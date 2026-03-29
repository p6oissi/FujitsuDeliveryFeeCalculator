package com.fujitsu.delivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Fee Calculator API")
                        .description("Calculates food courier delivery fees based on city, vehicle type, and weather conditions.")
                        .version("1.0.0"));
    }
}
