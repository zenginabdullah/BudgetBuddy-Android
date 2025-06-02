package com.budgetbuddy.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.budgetbuddy.app.ui.screens.*
import com.budgetbuddy.app.viewmodel.ExpenseViewModel
import com.budgetbuddy.app.viewmodel.IncomeViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    currency: String,
    isDark: Boolean,
    notificationsEnabled: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreenWrapper(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignupScreenWrapper(
                onSignupSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("home") {
            val expenseViewModel: ExpenseViewModel = hiltViewModel()
            val incomeViewModel: IncomeViewModel = hiltViewModel()
            HomeScreen(
                currencySymbol = currency,
                expenseViewModel = expenseViewModel,
                incomeViewModel = incomeViewModel,
                onAddExpenseClick = { navController.navigate("add_expense") },
                onAddIncomeClick = { navController.navigate("add_income") },
                onHistoryClick = { navController.navigate("history") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        composable("add_expense") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            AddExpenseScreen(viewModel = viewModel)
        }

        composable("add_income") {
            val viewModel: IncomeViewModel = hiltViewModel()
            AddIncomeScreen(viewModel = viewModel)
        }

        composable("history") {
            val expenseViewModel: ExpenseViewModel = hiltViewModel()
            val incomeViewModel: IncomeViewModel = hiltViewModel()
            HistoryScreen(viewModel = expenseViewModel, incomeViewModel = incomeViewModel)
        }

        composable("settings") {
            SettingsScreen(
                currency = currency,
                isDark = isDark,
                notificationsEnabled = notificationsEnabled,
                onThemeToggle = onThemeToggle,
                onCurrencyChange = onCurrencyChange,
                onNotificationsToggle = onNotificationsToggle
            )
        }
    }
}
