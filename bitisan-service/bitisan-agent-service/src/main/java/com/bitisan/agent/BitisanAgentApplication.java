package com.bitisan.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.bitisan")
@EnableFeignClients(basePackages = {"com.bitisan.*.feign"})
public class BitisanAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitisanAgentApplication.class,args);
    }
}
