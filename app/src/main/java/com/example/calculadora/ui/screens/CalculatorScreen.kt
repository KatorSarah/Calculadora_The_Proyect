package com.example.calculadora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadora.R
import com.example.calculadora.model.CalculationState
import com.example.calculadora.ui.components.CalculatorButton
import com.example.calculadora.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    state: CalculationState,
    onOpenHistory: () -> Unit
) {
    val buttons = listOf(
        listOf("AC", "+/-", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=",)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF111111))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Display area
            Text(
                text = state.displayValue,
                color = Color.White,
                fontSize = if (state.displayValue.length > 9) 38.sp else 64.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenHistory,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D))
                ) {
                    Text(text = stringResource(R.string.button_history), color = Color.White)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                items(buttons.flatten().size) { index ->
                    val label = buttons.flatten()[index]
                    val isOperator = label in listOf("+", "−", "×", "÷", "=")
                    val isSpecial = label in listOf("AC", "+/-", "%")
                    val color = when {
                        isOperator -> Color(0xFFFF9F0A)
                        isSpecial -> Color(0xFFA6A6A6)
                        else -> Color(0xFF333333)
                    }
                    val textColor = if (isSpecial) Color(0xFF111111) else Color.White

                    CalculatorButton(
                        label = label,
                        backgroundColor = color,
                        textColor = textColor,
                        onClick = {
                            when (label) {
                                "AC" -> viewModel.onClearPressed()
                                "+/-" -> viewModel.onToggleSignPressed()
                                "%" -> viewModel.onPercentPressed()
                                "÷" -> viewModel.onOperationPressed("÷")
                                "×" -> viewModel.onOperationPressed("×")
                                "−" -> viewModel.onOperationPressed("-")
                                "+" -> viewModel.onOperationPressed("+")
                                "=" -> viewModel.onEqualsPressed()
                                "." -> viewModel.onDecimalPressed()
                                else -> {
                                    val digit = label.toIntOrNull()
                                    if (digit != null) viewModel.onDigitPressed(digit)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(72.dp)
                    )
                }
            }
        }
    }
}
