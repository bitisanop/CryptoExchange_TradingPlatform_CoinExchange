package com.bitisan.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.bitisan.**.feign"})
@SpringBootApplication(scanBasePackages = "com.bitisan")
public class BitisanExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitisanExchangeApplication.class,args);
    }
}
