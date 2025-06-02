package com.budgetbuddy.app.ui

// Android bileşenleri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController

// Tema ve navigation
import com.budgetbuddy.app.ui.navigation.AppNavHost
import com.budgetbuddy.app.ui.theme.BudgetBuddyTheme

// 🚨 Yeni eklenen import: Lokasyon uyarı yöneticisi
import com.budgetbuddy.app.sensors.LocationAlertManager
import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
        super.onCreate(savedInstanceState)

        // 📌 Lokasyon kontrolünü başlat!
        LocationAlertManager(this).startLocationCheck()

        setContent {
            BudgetBuddyTheme {
                val navController = rememberNavController()
                Surface {
                    AppNavHost(navController = navController)
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

        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Log.d("LocationDebug", "Konum izni verildi.")
            LocationAlertManager(this).startLocationCheck()
        } else {
            // Log.d("LocationDebug", "Konum izni reddedildi.")
        }
    }
}
