package com.example.calculadora.model

import java.math.BigDecimal

data class OperationHistoryEntry(
    val expression: String,
    val result: BigDecimal
)
