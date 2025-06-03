package com.budgetbuddy.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.budgetbuddy.app.data.local.AppDatabase
import com.budgetbuddy.app.data.repository.ExpenseRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MonthlySummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // ✅ Hem expense hem income dao'yu veriyoruz
    private val database = AppDatabase.getInstance(appContext)
    private val expenseRepository = ExpenseRepository(
        database.expenseDao(),
        database.incomeDao()
    )

    override suspend fun doWork(): Result {
        // TEST AMAÇLI true dön
        // if (!isFirstDayOfMonth()) return Result.success()

        val lastMonthTotal = getLastMonthTotalExpense()
        showMonthlyNotification(lastMonthTotal)

        return Result.success()
    }

    private fun isFirstDayOfMonth(): Boolean {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH) == 1
    }

    private suspend fun getLastMonthTotalExpense(): Double {
        val allExpenses = expenseRepository.getAllExpensesOnce()
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val lastMonth = calendar.get(Calendar.MONTH)
        val lastYear = calendar.get(Calendar.YEAR)

        allExpenses.forEach {
            Log.d("🧾Harcamalar", "Tarih: ${it.date} - Tutar: ${it.amount}")
        }

        return allExpenses.filter {
            try {
                val date = sdf.parse(it.date)
                val cal = Calendar.getInstance().apply { time = date!! }
                Log.d("🕵️Filtreleme", "Harcamadaki ay: ${cal.get(Calendar.MONTH)} - Yıl: ${cal.get(Calendar.YEAR)}")
                cal.get(Calendar.MONTH) == lastMonth && cal.get(Calendar.YEAR) == lastYear
            } catch (e: Exception) {
                Log.e("❌TarihParse", "Parse edilemedi: ${it.date}")
                false
            }
        }.sumOf { it.amount }
    }



    private fun showMonthlyNotification(totalExpense: Double) {
        val channelId = "monthly_summary_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Aylık Harcama Özeti",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📅 Aylık Harcama Özeti")
            .setContentText("Geçen ayki toplam harcamanız: ₺%.2f".format(totalExpense))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1001, notification)
    }

}
