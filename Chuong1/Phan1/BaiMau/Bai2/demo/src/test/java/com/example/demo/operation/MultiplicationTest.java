package com.example.demo.operation;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class MultiplicationTest {
    private final Multiplication multiplication = new Multiplication();

    @Test
    void shouldMatchSign() {
        assertThat(multiplication.handles('*')).isTrue();
        assertThat(multiplication.handles('/')).isFalse();
    }

    @Test
    void shouldCorrectlyApplyFormula() {
        assertThat(multiplication.apply(2, 2)).isEqualTo(4);
        assertThat(multiplication.apply(12, 10)).isEqualTo(120);
    }
}
