package com.budgetbuddy.app.sensors

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import android.util.Log
import com.budgetbuddy.app.sensors.LocationAlertManager


class LocationAlertManager(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Örnek alışveriş merkezi koordinatı (Capacity AVM)
    private val targetLatitude = 40.9771
    private val targetLongitude = 28.8720
    private val radius = 200.0 // metre

    fun startLocationCheck() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {

                    val location = result.lastLocation ?: return
                    if (isInsideTarget(location)) {
                        showNotification()
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }


    private fun isInsideTarget(location: Location): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(
            location.latitude, location.longitude,
            targetLatitude, targetLongitude,
            result
        )
        return result[0] <= radius
    }

    private fun showNotification() {
        val channelId = "location_alert_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lokasyon Uyarıları",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("Dikkatli Harca!")
            .setContentText("Alışveriş bölgesindesin. Harcamalarına dikkat et 🛍️")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(1212, notification)
    }
}
