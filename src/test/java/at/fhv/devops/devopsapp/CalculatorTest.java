package at.fhv.devops.devopsapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addReturnsSum() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void divideReturnsQuotient() {
        assertEquals(4, calculator.divide(8, 2));
    }

    @Test
    void divideByZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.divide(1, 0));
    }
}