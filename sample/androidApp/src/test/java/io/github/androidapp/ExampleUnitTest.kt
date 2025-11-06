package io.github.androidapp

import io.github.calc.Calculator
import org.junit.Test

import org.junit.Assert.*

class CalculatorUniText {

    @Test
    fun calculator_addition() {
        Calculator.add(2.0, 2.0).also {
            assertEquals(4.0, it, 0.0001)
        }
    }

    @Test
    fun calculator_subtract() {
        Calculator.subtract(2.0, 2.0).also {
            assertEquals(0.0, it, 0.0001)
        }
    }

    @Test
    fun calculator_multiply() {
        Calculator.multiply(2.0, 3.0).also {
            assertEquals(6.0, it, 0.0001)
        }
    }

    @Test
    fun calculator_divide() {
        Calculator.divide(20.0, 2.0).also {
            assertEquals(10.0, it ?: 0.0, 0.0001)
        }
    }

    @Test
    fun calculator_divide_by_zero() {
        Calculator.divide(20.0, 0.0).also {
            assertEquals(0.0, it ?: 0.0, 0.0001)
        }
    }
}