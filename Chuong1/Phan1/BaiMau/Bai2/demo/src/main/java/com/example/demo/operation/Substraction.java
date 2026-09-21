package com.example.demo.operation;

import com.example.demo.Operation;
import org.springframework.stereotype.Component;

@Component
public class Substraction implements Operation {
    @Override
    public int apply(int lhs, int rhs) {
        return lhs - rhs;
    }

    @Override
    public boolean handles(char op) {
        return '-' == op;
    }
}
