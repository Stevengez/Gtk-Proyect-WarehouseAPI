package com.gtk.warehouse.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GtkWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GtkWarehouseApplication.class, args);
    }

}
