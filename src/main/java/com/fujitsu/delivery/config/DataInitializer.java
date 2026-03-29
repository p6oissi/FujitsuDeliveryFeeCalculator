package com.fujitsu.delivery.config;

import com.fujitsu.delivery.entity.RegionalBaseFee;
import com.fujitsu.delivery.entity.WeatherFeeRule;
import com.fujitsu.delivery.enums.City;
import com.fujitsu.delivery.enums.FeeConditionType;
import com.fujitsu.delivery.enums.VehicleType;
import com.fujitsu.delivery.repository.RegionalBaseFeeRepository;
import com.fujitsu.delivery.repository.WeatherFeeRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RegionalBaseFeeRepository baseFeeRepository;
    private final WeatherFeeRuleRepository weatherFeeRuleRepository;

    public DataInitializer(RegionalBaseFeeRepository baseFeeRepository,
                           WeatherFeeRuleRepository weatherFeeRuleRepository) {
        this.baseFeeRepository = baseFeeRepository;
        this.weatherFeeRuleRepository = weatherFeeRuleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (baseFeeRepository.count() == 0) seedBaseFees();
        if (weatherFeeRuleRepository.count() == 0) seedWeatherFeeRules();
    }

    private void seedBaseFees() {
        baseFeeRepository.saveAll(List.of(
                // Tallinn
                fee(City.TALLINN, VehicleType.CAR,     "4.0"),
                fee(City.TALLINN, VehicleType.SCOOTER, "3.5"),
                fee(City.TALLINN, VehicleType.BIKE,    "3.0"),
                // Tartu
                fee(City.TARTU,   VehicleType.CAR,     "3.5"),
                fee(City.TARTU,   VehicleType.SCOOTER, "3.0"),
                fee(City.TARTU,   VehicleType.BIKE,    "2.5"),
                // Pärnu
                fee(City.PÄRNU,   VehicleType.CAR,     "3.0"),
                fee(City.PÄRNU,   VehicleType.SCOOTER, "2.5"),
                fee(City.PÄRNU,   VehicleType.BIKE,    "2.0")
        ));
        log.info("Seeded 9 regional base fees");
    }

    private void seedWeatherFeeRules() {
        weatherFeeRuleRepository.saveAll(List.of(
                // TEMPERATURE — applies to SCOOTER and BIKE
                rule(FeeConditionType.TEMPERATURE, null,    -10.001, null,               "SCOOTER,BIKE", "1.0", false), // < -10°C  -> 1€
                rule(FeeConditionType.TEMPERATURE, -10.0,   0.0,    null,               "SCOOTER,BIKE", "0.5", false), // -10..0°C -> 0.5€

                // WIND SPEED — applies to BIKE only
                rule(FeeConditionType.WIND_SPEED,  10.0,   20.0,   null,               "BIKE",         "0.5", false), // 10..20 m/s -> 0.5€
                rule(FeeConditionType.WIND_SPEED,  20.001, null,   null,               "BIKE",         null,  true),  // > 20 m/s   -> forbidden

                // PHENOMENON — applies to SCOOTER and BIKE
                rule(FeeConditionType.PHENOMENON,  null,   null,   "snow,sleet",       "SCOOTER,BIKE", "1.0", false), // snow/sleet         -> 1€
                rule(FeeConditionType.PHENOMENON,  null,   null,   "rain",             "SCOOTER,BIKE", "0.5", false), // rain               -> 0.5€
                rule(FeeConditionType.PHENOMENON,  null,   null,   "glaze,hail,thunder","SCOOTER,BIKE", null, true)   // glaze/hail/thunder -> forbidden
        ));
        log.info("Seeded 7 weather extra fee rules");
    }

    private RegionalBaseFee fee(City city,
                                VehicleType vehicleType,
                                String amount) {
        RegionalBaseFee fee = new RegionalBaseFee();
        fee.setCity(city);
        fee.setVehicleType(vehicleType);
        fee.setFee(new BigDecimal(amount));

        return fee;
    }

    private WeatherFeeRule rule(FeeConditionType type,
                                Double min,
                                Double max,
                                String keywords,
                                String vehicles,
                                String amount,
                                boolean forbidden) {
        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setConditionType(type);
        rule.setMinValue(min);
        rule.setMaxValue(max);
        rule.setPhenomenonKeywords(keywords);
        rule.setApplicableVehicles(vehicles);
        rule.setFee(amount != null ? new BigDecimal(amount) : null);
        rule.setForbidden(forbidden);

        return rule;
    }
}
