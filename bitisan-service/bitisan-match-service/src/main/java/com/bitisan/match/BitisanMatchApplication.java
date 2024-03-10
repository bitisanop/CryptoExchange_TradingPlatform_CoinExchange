package com.bitisan.match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bitisan")
public class BitisanMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitisanMatchApplication.class,args);
    }
}
