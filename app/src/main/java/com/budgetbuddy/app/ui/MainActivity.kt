package com.budgetbuddy.app.ui // Ana Activity'nin bulunduğu paket

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.budgetbuddy.app.ui.navigation.AppNavHost
import com.budgetbuddy.app.ui.theme.BudgetBuddyTheme
import com.budgetbuddy.app.util.NotificationScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Android 13+ için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Test bildirimi (10 saniye sonra)
        NotificationScheduler.scheduleTestNotification(applicationContext)

        // Her gün saat 21:00'de çalışacak iş planı
        NotificationScheduler.scheduleDailySummary(applicationContext)

        // Compose UI'yı başlat
        setContent {
            BudgetBuddyTheme {
                val navController = rememberNavController()
                Surface {
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
