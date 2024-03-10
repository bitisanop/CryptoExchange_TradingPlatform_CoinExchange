package com.bitisan.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.bitisan.*.feign"})
@SpringBootApplication(scanBasePackages = "com.bitisan")
public class BitisanMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitisanMarketApplication.class,args);
    }
}
