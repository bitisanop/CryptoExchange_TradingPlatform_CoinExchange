package com.bitisan.open;

import com.bitisan.open.interceptor.OpenApiInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.bitisan.*.feign"})
@SpringBootApplication(scanBasePackages = "com.bitisan")
public class BitisanOpenApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BitisanOpenApplication.class,args);
    }

    @Bean
    public OpenApiInterceptor openApiInterceptor() {
        return new OpenApiInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(openApiInterceptor()).addPathPatterns("/open/**", "/user/**");
    }
}

