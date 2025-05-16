package com.budgetbuddy.app.ui // Ana Activity'nin bulunduğu paket

// Android ve Compose bileşenlerini içe aktarıyoruz
import android.os.Bundle
import androidx.activity.ComponentActivity // Activity'nin temel sınıfı
import androidx.activity.compose.setContent // Compose ekranlarını göstermeye yarar
import androidx.compose.material3.* // Material 3 bileşenleri (tema, buton vs.)
import androidx.navigation.compose.rememberNavController // Navigation için controller oluşturur

// Navigation grafiğimizi (ekran geçişlerini) getiriyoruz
import com.budgetbuddy.app.ui.navigation.AppNavHost

// Temamızı getiriyoruz (renkler, yazı tipi vs.)
import com.budgetbuddy.app.ui.theme.BudgetBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Uygulama başlatıldığında Compose UI'yı göster
        setContent {
            BudgetBuddyTheme { // Tema ile uygulamayı sar
                val navController = rememberNavController() // Navigation controller'ı oluştur

                Surface { // Arka plan yüzeyi (Material)
                    AppNavHost(navController = navController) // Navigation başlat
                }
            }
        }
    }
}
