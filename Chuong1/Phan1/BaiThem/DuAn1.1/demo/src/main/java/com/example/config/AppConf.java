package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.service.GreetingService;

@Configuration
public class AppConf {

    @Bean
    public GreetingService greetingService() {
        GreetingService service = new GreetingService();
        service.setMessage("Hello from Spring IoC Container with Java-based Configuration!");
        return service;
    }
}
