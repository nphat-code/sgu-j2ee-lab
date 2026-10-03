package com.example.implementation;

import org.springframework.stereotype.Component;
import com.example.interfaces.Tyres;

@Component
public class BridgeStoneTyres implements Tyres {
    public String rotate() {
        return "Vehicle moving with BridgeStone tyres";
    }
}
