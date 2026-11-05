package com.example.calculadora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculadora.ui.screens.CalculatorScreen
import com.example.calculadora.ui.screens.HistoryScreen
import com.example.calculadora.viewmodel.CalculatorViewModel

sealed class Screen(val route: String) {
    data object Calculator : Screen("calculator")
    data object History : Screen("history")
}

@Composable
fun AppNavigation(viewModel: CalculatorViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Calculator.route
    ) {
        composable(Screen.Calculator.route) {
            CalculatorScreen(
                viewModel = viewModel,
                state = uiState,
                onOpenHistory = { navController.navigate(Screen.History.route) }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                entries = uiState.history,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
