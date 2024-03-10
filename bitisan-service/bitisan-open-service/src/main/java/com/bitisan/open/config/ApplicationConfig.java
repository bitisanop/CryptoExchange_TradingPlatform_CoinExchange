//package com.bitisan.open.config;
//
//import com.bitisan.open.interceptor.OpenApiInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
///**
// * @description: ApplicationConfig
// * @author Bitisan  E-mail:bizzanhevin@gmail.com
// * @create: 2019/05/06 16:32
// */
////@Configuration
//public class ApplicationConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new OpenApiInterceptor())
//                .addPathPatterns("/open/**", "/user/**")
//                .excludePathPatterns("/open/**");
//        super.addInterceptors(registry);
//    }
//
//
//}
