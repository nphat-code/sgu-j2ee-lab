package com.example.service;

public class GreetingService {
    private String message;

    public GreetingService() {
    }

    public GreetingService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void sayGreeting() {
        System.out.println("Greeting message: " + message);
    }
}
