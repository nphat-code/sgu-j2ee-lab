package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.config.AppConf;
import com.example.service.GreetingService;

class DuAn11Tests {

    @Test
    void testGreetingServiceBeanLoaded() {
        try (var context = new AnnotationConfigApplicationContext(AppConf.class)) {
            GreetingService greetingService = context.getBean(GreetingService.class);
            assertNotNull(greetingService);
            assertEquals("Hello from Spring IoC Container with Java-based Configuration!", greetingService.getMessage());
        }
    }
}
