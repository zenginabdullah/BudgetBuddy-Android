package com.budgetbuddy.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.budgetbuddy.app.data.PreferencesManager
import com.budgetbuddy.app.sensors.LocationAlertManager
import com.budgetbuddy.app.ui.navigation.AppNavHost
import com.budgetbuddy.app.ui.theme.BudgetBuddyTheme
import com.budgetbuddy.app.util.NetworkConnectivityObserver
import com.budgetbuddy.app.util.NotificationScheduler
import com.budgetbuddy.app.util.NotificationHelper
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import com.budgetbuddy.app.util.MonthlySummaryWorker
import com.budgetbuddy.app.viewmodel.ChatBotViewModel
import java.util.concurrent.TimeUnit
import com.budgetbuddy.app.util.DailySummaryWorker
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ayın 1'inde Worker'ı tetikleyecek iş
        val monthlySummaryRequest = OneTimeWorkRequestBuilder<MonthlySummaryWorker>()
            .setInitialDelay(calculateNextMonthStart(), TimeUnit.MILLISECONDS) // Ayın 1'ine kadar bekle
            .build()

        Log.d("WorkManager", "Scheduling WorkManager...") // Bu satırla log mesajı ekliyoruz

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "monthly_summary_worker",
            ExistingWorkPolicy.REPLACE,
            monthlySummaryRequest
        )


        // 📅 MonthlySummaryWorker test
        val testRequest = OneTimeWorkRequestBuilder<MonthlySummaryWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "test_monthly_summary",
            ExistingWorkPolicy.REPLACE,
            testRequest
        )

        // Konum izni kontrolü
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        } else {
            LocationAlertManager(this).startLocationCheck()
        }

        // Android 13+ için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1002
                )
            }
        }

        // Günlük özet bildirimi planlayıcı
        NotificationScheduler.scheduleDailySummary(applicationContext)

        // TEST: Gün sonu özeti bildirimini 5 saniye sonra tetikle
        val testDailySummaryRequest = OneTimeWorkRequestBuilder<DailySummaryWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "test_daily_summary_worker",
            ExistingWorkPolicy.REPLACE,
            testDailySummaryRequest
        )


        // Bağlantı durumu gözlemleyici
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        lifecycleScope.launch {
            connectivityObserver.observe().collect { status ->
                Log.d("Connectivity", "Durum: $status")
                // Burada API senkronizasyonu yapılabilir
            }
        }
        

        // UI başlat
        setContent {
            val context = LocalContext.current
            val prefs = remember { PreferencesManager(context) }
            val navController = rememberNavController()

            // ChatBotViewModel'i oluştur
            val chatBotViewModel: ChatBotViewModel = hiltViewModel()

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
                                NotificationHelper.showInfoNotification(
                                    applicationContext,
                                    "Bildirimler Açıldı",
                                    "Günlük özet bildirimleri aktif hale getirildi"
                                )
                            } else {
                                NotificationScheduler.cancelDailySummary(applicationContext)
                                NotificationHelper.showInfoNotification(
                                    applicationContext,
                                    "Bildirimler Kapatıldı",
                                    "Bildirimler devre dışı bırakıldı"
                                )
                            }
                        },
                        chatBotViewModel = chatBotViewModel
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationAlertManager(this).startLocationCheck()
                }
            }
            1002 -> {
                Log.d("Permissions", "Bildirim izni verildi.")
            }
        }
    }
    fun calculateNextMonthStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1) // Gelecek ayı al
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Ayın 1'ini ayarla
        calendar.set(Calendar.HOUR_OF_DAY, 9) // Sabah 9'u ayarla
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // Eğer şu anki tarih 9:00'dan sonra ise, bir sonraki ayın 1'ine al
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.MONTH, 1)
        }

        Log.d("WorkManager", "Next month start time: ${calendar.timeInMillis}") // Log mesajı ekledik
        return calendar.timeInMillis - System.currentTimeMillis() // Gelecek ayın 1'ine kadar kalan süreyi hesapla
    }

}