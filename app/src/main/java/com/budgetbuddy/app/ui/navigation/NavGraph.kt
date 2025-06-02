package com.budgetbuddy.app.ui.navigation // Bu dosyanın bulunduğu paket (klasör yapısına göre önemli)

// Diğer ekranları bu dosyada kullanabilmek için içe aktarıyoruz
import com.budgetbuddy.app.ui.screens.AddExpenseScreen // Gider ekleme ekranı
import com.budgetbuddy.app.ui.screens.AddIncomeScreen  // Gelir ekleme ekranı
import com.budgetbuddy.app.ui.screens.HistoryScreen    // Geçmiş işlemler ekranı
import com.budgetbuddy.app.ui.screens.HomeScreen       // Ana ekran

// Jetpack Compose fonksiyonları ve navigation bileşenleri
import androidx.compose.runtime.Composable // @Composable fonksiyon yazmamıza yarar
import androidx.navigation.NavHostController // Navigation'u yönetecek controller
import androidx.navigation.compose.NavHost // Navigation sisteminin iskeleti
import androidx.navigation.compose.composable // Her sayfa için route tanımlamaya yarar

import com.budgetbuddy.app.viewmodel.ExpenseViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.budgetbuddy.app.viewmodel.IncomeViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    // Navigation sistemini başlatıyoruz, başlangıç sayfası "home"
    NavHost(navController = navController, startDestination = "home") {

        // Ana ekran rotası
        composable("home") {
            val expenseViewModel: ExpenseViewModel = hiltViewModel()
            val incomeViewModel: IncomeViewModel = hiltViewModel()
            HomeScreen(
                expenseViewModel = expenseViewModel,
                incomeViewModel = incomeViewModel,
                onAddExpenseClick = { navController.navigate("add_expense") },
                onAddIncomeClick = { navController.navigate("add_income") },
                onHistoryClick = { navController.navigate("history") }
            )
        }

        // Diğer ekranların rotaları
        composable("add_expense") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            AddExpenseScreen(viewModel = viewModel)
        }
        composable("add_income") {
            val viewModel: IncomeViewModel = hiltViewModel()
            AddIncomeScreen(viewModel = viewModel)
        }
        composable("history") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            val incomeViewModel: IncomeViewModel = hiltViewModel()
            HistoryScreen(viewModel = viewModel, incomeViewModel = incomeViewModel)
        }
    }
}
