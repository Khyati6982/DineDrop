package com.dinedrop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.dinedrop.model")
public class DineDropApplication {

    public static void main(String[] args) {
        SpringApplication.run(DineDropApplication.class, args);
    }
}
