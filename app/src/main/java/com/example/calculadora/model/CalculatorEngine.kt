package com.example.calculadora.model

import java.math.BigDecimal
import java.math.RoundingMode

data class CalculationState(
    val displayValue: String = "0",
    val storedValue: BigDecimal? = null,
    val pendingOperation: String? = null,
    val lastOperand: BigDecimal? = null,
    val shouldClearDisplay: Boolean = false,
    val lastResult: BigDecimal? = null,
    val isError: Boolean = false,
    val history: List<OperationHistoryEntry> = emptyList(),
    val lastInput: String? = null,
    val pendingEquals: Boolean = false
)

class CalculatorEngine {
    companion object {
        private val ZERO = BigDecimal.ZERO
        private val SCALE = 12
        private val HALF_UP = RoundingMode.HALF_UP
    }

    fun appendDigit(current: String, digit: Int): String {
        if (current == "0" || current == "-0") {
            return if (digit == 0) "0" else digit.toString()
        }
        return current + digit
    }

    fun appendDecimal(current: String): String {
        if (current.isEmpty()) return "0."
        if (current.contains(".")) return current
        return "$current."
    }

    fun toggleSign(current: String): String {
        val value = current.toBigDecimalOrNull() ?: return current
        return if (value == ZERO) "0" else value.negate().stripTrailingZeros().toPlainString()
    }

    fun percentage(current: String): String {
        val value = current.toBigDecimalOrNull() ?: return current
        return value.divide(BigDecimal(100), SCALE, HALF_UP).stripTrailingZeros().toPlainString()
    }

    fun applyPercentage(firstValue: BigDecimal, secondValue: BigDecimal, operation: String): BigDecimal {
        val normalizedPercent = secondValue.divide(BigDecimal(100), SCALE, HALF_UP)
        return when (operation) {
            "+" -> firstValue.add(firstValue.multiply(normalizedPercent))
            "-" -> firstValue.subtract(firstValue.multiply(normalizedPercent))
            "×" -> firstValue.multiply(normalizedPercent)
            "÷" -> {
                if (normalizedPercent == ZERO) {
                    throw IllegalArgumentException(CalculationError.DIVISION_BY_ZERO.name)
                }
                firstValue.divide(normalizedPercent, SCALE, HALF_UP)
            }
            else -> throw IllegalArgumentException(CalculationError.INVALID_INPUT.name)
        }
    }

    fun calculate(
        firstValue: BigDecimal,
        secondValue: BigDecimal,
        operation: String
    ): Result<BigDecimal> {
        return runCatching {
            when (operation) {
                "+" -> firstValue.add(secondValue)
                "-" -> firstValue.subtract(secondValue)
                "×" -> firstValue.multiply(secondValue)
                "÷" -> {
                    if (secondValue == ZERO) {
                        throw IllegalArgumentException(CalculationError.DIVISION_BY_ZERO.name)
                    }
                    firstValue.divide(secondValue, SCALE, HALF_UP)
                }
                else -> throw IllegalArgumentException(CalculationError.INVALID_INPUT.name)
            }
        }
    }

    fun repeatOperation(baseValue: BigDecimal, lastOperand: BigDecimal, operation: String): BigDecimal {
        return calculate(baseValue, lastOperand, operation).getOrElse { throwable ->
            throw IllegalArgumentException(throwable.message ?: CalculationError.INVALID_INPUT.name)
        }
    }

    fun normalize(value: String): BigDecimal = value.toBigDecimalOrNull()
        ?: throw IllegalArgumentException(CalculationError.INVALID_INPUT.name)

    fun formatResult(value: BigDecimal): String = value.stripTrailingZeros().toPlainString()
}
