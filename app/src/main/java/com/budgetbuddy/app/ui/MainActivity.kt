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
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Konum izni kontrolü
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

        // 🔔 Android 13+ için bildirim izni kontrolü
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

        // 📅 Bildirim planlayıcılar
        NotificationScheduler.scheduleTestNotification(applicationContext)
        NotificationScheduler.scheduleDailySummary(applicationContext)

        // 🌐 Bağlantı durumu gözlemleyici
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        lifecycleScope.launch {
            connectivityObserver.observe().collect { status ->
                Log.d("Connectivity", "Durum: $status")
                // Burada API senkronizasyonu yapılabilir
            }
        }

        // 🎨 UI başlat
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
}
