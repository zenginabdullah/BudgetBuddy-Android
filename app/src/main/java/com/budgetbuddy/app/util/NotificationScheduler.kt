package com.budgetbuddy.app.util

import android.content.Context
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    fun scheduleTestNotification(context: Context) {
        val request = OneTimeWorkRequestBuilder<DailySummaryWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS) // 10 saniye sonra bildirim
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
    fun cancelDailySummary(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("daily_summary")
    }
    fun scheduleDailySummary(context: Context) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21) // Saat 21:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<DailySummaryWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_summary",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
