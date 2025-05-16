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

@Composable
fun AppNavHost(navController: NavHostController) {
    // Navigation sistemini başlatıyoruz, başlangıç sayfası "home"
    NavHost(navController = navController, startDestination = "home") {

        // Ana ekran rotası
        composable("home") {
            HomeScreen(
                onAddExpenseClick = { navController.navigate("add_expense") }, // Gider sayfasına git
                onAddIncomeClick = { navController.navigate("add_income") },   // Gelir sayfasına git
                onHistoryClick = { navController.navigate("history") }         // Geçmiş sayfasına git
            )
        }

        // Diğer ekranların rotaları
        composable("add_expense") { AddExpenseScreen() }
        composable("add_income") { AddIncomeScreen() }
        composable("history") { HistoryScreen() }
    }
}
