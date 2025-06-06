package com.budgetbuddy.app.util

import android.content.Context
import androidx.work.*
import com.budgetbuddy.app.util.DailySummaryWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleDailySummary(context: Context) {
        val now = LocalDateTime.now()
        val nextNinePM = now.withHour(21).withMinute(0).withSecond(0).withNano(0)
        val delay = Duration.between(now, nextNinePM).toMillis().coerceAtLeast(0)

        val workRequest = PeriodicWorkRequestBuilder<DailySummaryWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_summary_worker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelDailySummary(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("daily_summary_worker")
    }
}
