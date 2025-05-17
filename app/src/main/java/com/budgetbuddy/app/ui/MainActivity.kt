package com.budgetbuddy.app.ui // Ana Activity'nin bulunduğu paket

// Android temel bileşenleri
import android.os.Bundle // Activity yaşam döngüsünü yönetmek için

// Loglama işlemleri için
import android.util.Log // Log.d ile debug çıktıları veririz (örneğin bağlantı durumu)

// AndroidX Compose ve Navigation bileşenleri
import androidx.activity.ComponentActivity // Compose tabanlı Activity
import androidx.activity.compose.setContent // UI’yi Compose ile ayarlamak için
import androidx.compose.material3.* // Material 3 bileşenleri (tema, buton, surface vs.)
import androidx.navigation.compose.rememberNavController // Navigation controller oluşturur

// 🔽 Coroutine ve lifecycle için
import androidx.lifecycle.lifecycleScope // Lifecycle-aware coroutine başlatmak için
import kotlinx.coroutines.launch // Coroutine başlatmak için

// Projedeki özel dosyalar
import com.budgetbuddy.app.ui.navigation.AppNavHost // Uygulama içi sayfa geçişleri
import com.budgetbuddy.app.ui.theme.BudgetBuddyTheme // Uygulama teması
import com.budgetbuddy.app.util.NetworkConnectivityObserver // Ağ bağlantısını dinleyen sınıf


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // İnternet bağlantısını dinle
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        lifecycleScope.launch {
            connectivityObserver.observe().collect { status ->
                Log.d("Connectivity", "Durum: $status")
                // Buraya API çağrısı yapılabilir.
            }
        }

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
