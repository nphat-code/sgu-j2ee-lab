package com.example.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.config.AppConf;
import com.example.service.GreetingService;

public class SpringApplication {
    public static void main(String[] args) {
        // Khởi tạo Spring IoC Container sử dụng Java-based Configuration
        var context = new AnnotationConfigApplicationContext(AppConf.class);

        // Truy vấn Bean từ Spring Container
        GreetingService greetingService = context.getBean(GreetingService.class);

        // Thực thi phương thức của Bean
        greetingService.sayGreeting();

        // Đóng context để giải phóng tài nguyên
        context.close();
    }
}
