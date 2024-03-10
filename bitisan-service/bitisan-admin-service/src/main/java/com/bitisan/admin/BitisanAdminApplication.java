package com.bitisan.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.bitisan")
@EnableFeignClients(basePackages = {"com.bitisan.**.feign"})
@EnableCaching
@EnableDiscoveryClient
public class BitisanAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitisanAdminApplication.class,args);
    }
}
