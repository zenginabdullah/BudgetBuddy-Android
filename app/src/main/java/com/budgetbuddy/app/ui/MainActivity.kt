package com.budgetbuddy.app.ui // Ana Activity'nin bulunduğu paket

// Android temel bileşenleri
import android.Manifest // Bildirim izni için gerekli sabit
import android.content.pm.PackageManager // İzin kontrolü için
import android.os.Build // Sürüm kontrolü için
import android.os.Bundle // Activity yaşam döngüsü
import android.util.Log // Loglama (bağlantı durumu vs.)

// Compose ve Navigation bileşenleri
import androidx.activity.ComponentActivity // Temel Compose tabanlı Activity
import androidx.activity.compose.setContent // Compose UI’yi başlatmak için
import androidx.core.app.ActivityCompat // İzin istemek için
import androidx.core.content.ContextCompat // İzin kontrolü için
import androidx.compose.material3.* // Material 3 UI bileşenleri
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope // Yaşam döngüsüne bağlı coroutine başlatmak için
import androidx.navigation.compose.rememberNavController // Navigation controller
import com.budgetbuddy.app.data.PreferencesManager

// Projedeki özel sınıflar
import com.budgetbuddy.app.ui.navigation.AppNavHost // Sayfa geçişlerini yöneten yapı
import com.budgetbuddy.app.ui.theme.BudgetBuddyTheme // Uygulama teması (renk, yazı vs.)
import com.budgetbuddy.app.util.NotificationScheduler // Bildirimleri planlayan sınıf
import com.budgetbuddy.app.util.NetworkConnectivityObserver // Ağ bağlantı durumunu izleyen sınıf

// Coroutine başlatmak için
import kotlinx.coroutines.launch

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ için bildirim izni kontrolü (POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin yoksa kullanıcıdan istenir
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // 10 saniye sonra test bildirimi (çalıştığını görmek için)
        NotificationScheduler.scheduleTestNotification(applicationContext)

        // Her gün saat 21:00'de harcama özeti bildirim planı
        NotificationScheduler.scheduleDailySummary(applicationContext)

        // İnternet bağlantı durumunu dinle ve logla
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        lifecycleScope.launch {
            connectivityObserver.observe().collect { status ->
                Log.d("Connectivity", "Durum: $status")
                // API çağrısı burada yapılabilir (bağlantı varsa)
            }
        }

        // Uygulamanın UI'sını başlat
        setContent {
            val context = LocalContext.current
            val prefs = remember { PreferencesManager(context) }

            var isDark by remember { mutableStateOf(prefs.isDarkModeEnabled()) }
            var currency by remember { mutableStateOf(prefs.getCurrency()) }

            var notificationsEnabled by remember { mutableStateOf(prefs.areNotificationsEnabled()) }

            BudgetBuddyTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                Surface {
                    AppNavHost(
                        navController = navController,
                        currency = currency,
                        isDark = isDark,
                        notificationsEnabled = notificationsEnabled,
                        onThemeToggle = { newTheme ->
                            isDark = newTheme
                            prefs.setDarkModeEnabled(newTheme)
                        },
                        onCurrencyChange = { newCurrency ->
                            currency = newCurrency
                            prefs.setCurrency(newCurrency)
                        },
                        onNotificationsToggle = { isEnabled ->
                            notificationsEnabled = isEnabled
                            prefs.setNotificationsEnabled(isEnabled)

                            if (isEnabled) {
                                NotificationScheduler.scheduleDailySummary(applicationContext)
                            } else {
                                NotificationScheduler.cancelDailySummary(applicationContext)
                            }
                        }
                    )

                }
            }
        }

    }
}


