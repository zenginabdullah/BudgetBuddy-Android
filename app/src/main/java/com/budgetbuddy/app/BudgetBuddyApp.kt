package com.budgetbuddy.app

import android.app.Application
import androidx.work.*
import com.budgetbuddy.app.util.MonthlySummaryWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class BudgetBuddyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupMonthlyWorker()
    }

    private fun setupMonthlyWorker() {
        val workRequest = PeriodicWorkRequestBuilder<MonthlySummaryWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(getDelayUntilNextMidnight(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "monthly_summary_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun getDelayUntilNextMidnight(): Long {
        val now = Calendar.getInstance()
        val nextMidnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        return nextMidnight.timeInMillis - now.timeInMillis
    }
}
