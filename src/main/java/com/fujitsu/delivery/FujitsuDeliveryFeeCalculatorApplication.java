package com.fujitsu.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FujitsuDeliveryFeeCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FujitsuDeliveryFeeCalculatorApplication.class, args);
    }

}
