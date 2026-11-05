package com.example.calculadora.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal

class CalculatorEngineTest {

    private val engine = CalculatorEngine()

    @Test
    fun `sum works correctly`() {
        val result = engine.calculate(BigDecimal("5"), BigDecimal("3"), "+")
        assertEquals(BigDecimal("8"), result.getOrNull())
    }

    @Test
    fun `subtract works correctly`() {
        val result = engine.calculate(BigDecimal("10"), BigDecimal("4"), "-")
        assertEquals(BigDecimal("6"), result.getOrNull())
    }

    @Test
    fun `multiply works correctly`() {
        val result = engine.calculate(BigDecimal("5"), BigDecimal("4"), "×")
        assertEquals(BigDecimal("20"), result.getOrNull())
    }

    @Test
    fun `divide by zero is rejected`() {
        val result = engine.calculate(BigDecimal("10"), BigDecimal("0"), "÷")
        assertThrows(IllegalArgumentException::class.java) { result.getOrThrow() }
    }

    @Test
    fun `decimal addition is precise`() {
        val result = engine.calculate(BigDecimal("0.1"), BigDecimal("0.2"), "+")
        assertEquals(BigDecimal("0.3"), result.getOrNull())
    }

    @Test
    fun `toggle sign works`() {
        assertEquals("-5", engine.toggleSign("5"))
        assertEquals("5", engine.toggleSign("-5"))
    }

    @Test
    fun `percentage converts to hundredths`() {
        val result = engine.percentage("50")
        assertEquals("0.5", result)
    }
}
