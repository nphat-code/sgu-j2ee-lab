package com.example.implementation;

import org.springframework.stereotype.Component;
import com.example.interfaces.Speakers;

@Component
public class BoseSpeakers implements Speakers {
    public String makeSound() {
        return "Playing music with Bose speakers";
    }
}
