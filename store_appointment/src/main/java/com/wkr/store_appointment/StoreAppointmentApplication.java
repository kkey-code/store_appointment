package com.wkr.store_appointment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wkr.store_appointment.mapper")
@SpringBootApplication
public class StoreAppointmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreAppointmentApplication.class, args);
    }

}
