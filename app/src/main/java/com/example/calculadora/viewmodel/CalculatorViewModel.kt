package com.example.calculadora.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calculadora.model.CalculationError
import com.example.calculadora.model.CalculationState
import com.example.calculadora.model.CalculatorEngine
import com.example.calculadora.model.OperationHistoryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()

    private val _uiState = MutableStateFlow(
        CalculationState(
            displayValue = "0",
            storedValue = null,
            pendingOperation = null,
            lastOperand = null,
            shouldClearDisplay = false,
            lastResult = null,
            history = emptyList(),
            isError = false,
            lastInput = null,
            pendingEquals = false
        )
    )

    val uiState: StateFlow<CalculationState> = _uiState.asStateFlow()

    fun onDigitPressed(digit: Int) {
        val state = _uiState.value
        if (state.isError) {
            _uiState.value = state.copy(
                displayValue = digit.toString(),
                storedValue = null,
                pendingOperation = null,
                shouldClearDisplay = false,
                lastResult = null,
                isError = false,
                lastInput = digit.toString(),
                pendingEquals = false
            )
            return
        }

        val current = if (state.shouldClearDisplay) "0" else state.displayValue
        val nextDisplay = engine.appendDigit(current, digit)
        _uiState.value = state.copy(
            displayValue = nextDisplay,
            shouldClearDisplay = false,
            lastInput = digit.toString(),
            pendingEquals = false
        )
    }

    fun onDecimalPressed() {
        val state = _uiState.value
        if (state.isError) {
            _uiState.value = state.copy(
                displayValue = "0.",
                storedValue = null,
                pendingOperation = null,
                shouldClearDisplay = false,
                lastResult = null,
                isError = false,
                pendingEquals = false
            )
            return
        }

        _uiState.value = state.copy(
            displayValue = engine.appendDecimal(state.displayValue),
            shouldClearDisplay = false,
            pendingEquals = false
        )
    }

    fun onClearPressed() {
        _uiState.value = CalculationState()
    }

    fun onToggleSignPressed() {
        val state = _uiState.value
        if (state.isError) return
        _uiState.value = state.copy(
            displayValue = engine.toggleSign(state.displayValue),
            shouldClearDisplay = false,
            pendingEquals = false
        )
    }

    fun onPercentPressed() {
        val state = _uiState.value
        if (state.isError) return
        val current = normalizeDisplay(state.displayValue)
        val nextValue = engine.percentage(state.displayValue)
        _uiState.value = state.copy(
            displayValue = nextValue,
            storedValue = current,
            pendingEquals = false,
            lastOperand = current
        )
    }

    fun onOperationPressed(operation: String) {
        val state = _uiState.value
        val current = normalizeDisplay(state.displayValue)
        if (state.pendingOperation != null && state.storedValue != null && !state.pendingEquals) {
            val result = performPendingCalculation(state.storedValue, current, state.pendingOperation)
            if (result.isFailure) {
                handleError(result.exceptionOrNull() as? IllegalArgumentException)
                return
            }
            val resolved = result.getOrThrow()
            _uiState.value = state.copy(
                displayValue = engine.formatResult(resolved),
                storedValue = resolved,
                pendingOperation = operation,
                shouldClearDisplay = true,
                lastResult = resolved,
                lastOperand = current,
                lastInput = operation
            )
            return
        }

        _uiState.value = state.copy(
            storedValue = current,
            pendingOperation = operation,
            displayValue = engine.formatResult(current),
            shouldClearDisplay = true,
            lastInput = operation,
            lastOperand = current,
            pendingEquals = false
        )
    }

    fun onEqualsPressed() {
        val state = _uiState.value
        val current = normalizeDisplay(state.displayValue)

        if (state.pendingOperation == null || state.storedValue == null) {
            _uiState.value = state.copy(
                lastResult = current,
                displayValue = engine.formatResult(current),
                pendingEquals = false
            )
            return
        }

        val result = performPendingCalculation(state.storedValue, current, state.pendingOperation)
        if (result.isFailure) {
            handleError(result.exceptionOrNull() as? IllegalArgumentException)
            return
        }

        val resolved = result.getOrThrow()
        val expression = "${engine.formatResult(state.storedValue)} ${state.pendingOperation} ${engine.formatResult(current)}"
        _uiState.value = state.copy(
            displayValue = engine.formatResult(resolved),
            storedValue = resolved,
            pendingOperation = null,
            shouldClearDisplay = true,
            lastResult = resolved,
            history = state.history + OperationHistoryEntry(expression, resolved),
            lastOperand = current,
            pendingEquals = true,
            lastInput = "="
        )
    }

    private fun performPendingCalculation(
        firstValue: BigDecimal,
        secondValue: BigDecimal,
        operation: String
    ): Result<BigDecimal> {
        return engine.calculate(firstValue, secondValue, operation)
    }

    private fun normalizeDisplay(value: String): BigDecimal {
        return try {
            engine.normalize(value)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    private fun handleError(error: IllegalArgumentException?) {
        val state = _uiState.value
        _uiState.value = state.copy(
            displayValue = "Error",
            isError = true,
            storedValue = null,
            pendingOperation = null,
            shouldClearDisplay = false,
            lastResult = null,
            pendingEquals = false
        )
    }
}
