package com.budgetbuddy.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.budgetbuddy.app.R
import com.budgetbuddy.app.data.local.AppDatabase
import com.budgetbuddy.app.data.remote.FirebaseDataSourceImpl
import com.budgetbuddy.app.data.repository.ExpenseRepository
import com.budgetbuddy.app.data.repository.IncomeRepository
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailySummaryWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val database = AppDatabase.getInstance(context)

        val firebaseDataSource = FirebaseDataSourceImpl()

        val expenseRepo = ExpenseRepository(
            database.expenseDao(),
            database.incomeDao(),
            firebaseDataSource
        )

        val incomeRepo = IncomeRepository(
            database.incomeDao(),
            firebaseDataSource
        )

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val today = LocalDate.now().format(formatter)
        val userId = getCurrentUserId()

        val expenses = expenseRepo.getExpensesByDate(today, userId)
        val incomes = incomeRepo.getIncomesByDate(today, userId)

        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = incomes.sumOf { it.amount }

        showSummaryNotification(context, totalIncome, totalExpense)

        return Result.success()
    }

    private fun showSummaryNotification(context: Context, income: Double, expense: Double) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_summary_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Gün Sonu Özeti",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📊 Gün Sonu Özeti")
            .setContentText("Gelir: ₺%.2f | Gider: ₺%.2f".format(income, expense))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1002, builder.build())
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}
