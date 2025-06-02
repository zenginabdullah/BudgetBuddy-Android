package com.budgetbuddy.app.ui.navigation

import com.budgetbuddy.app.ui.screens.AddExpenseScreen
import com.budgetbuddy.app.ui.screens.AddIncomeScreen
import com.budgetbuddy.app.ui.screens.HistoryScreen
import com.budgetbuddy.app.ui.screens.HomeScreen
import com.budgetbuddy.app.ui.screens.LoginScreen
import com.budgetbuddy.app.ui.screens.SignupScreen
import com.budgetbuddy.app.ui.screens.SettingsScreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
            LoginScreen(
                onLoginClick = { email, password ->
                    // Firebase auth işlemleri buraya gelecek
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignupScreen(
                onSignupClick = { email, password ->
                    // Firebase auth işlemleri buraya gelecek
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("home") {
            HomeScreen(
                currencySymbol = currency,
                onAddExpenseClick = { navController.navigate("add_expense") },
                onAddIncomeClick = { navController.navigate("add_income") },
                onHistoryClick = { navController.navigate("history") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        composable("add_expense") { AddExpenseScreen() }
        composable("add_income") { AddIncomeScreen() }
        composable("history") { HistoryScreen() }
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
