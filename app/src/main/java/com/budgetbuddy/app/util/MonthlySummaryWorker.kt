package com.budgetbuddy.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.budgetbuddy.app.data.local.AppDatabase
import com.budgetbuddy.app.data.remote.FirebaseDataSourceImpl
import com.budgetbuddy.app.data.repository.ExpenseRepository
import java.text.SimpleDateFormat
import java.util.*

class MonthlySummaryWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Coroutine başlatıyoruz
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        coroutineScope.launch {
            try {
                // Geçen ayın verilerini al
                val totalExpense = getLastMonthTotalExpense(applicationContext)

                // BroadcastReceiver tetikle
                val intent = Intent(applicationContext, MonthlySummaryReceiver::class.java)
                intent.putExtra("totalExpense", totalExpense)

                // Broadcast gönder
                applicationContext.sendBroadcast(intent)

                // Sonuç başarılı
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                // Sonuç başarısız
                Result.failure()
            }
        }

        // Çalışmayı hemen sonlandırma, çünkü coroutine başlatıldı
        return Result.success()
    }

    // Suspend function'u CoroutineScope ile çağırıyoruz
    private suspend fun getLastMonthTotalExpense(context: Context): Double {
        return withContext(Dispatchers.IO) {
            val database = AppDatabase.getInstance(context)
            val firebaseDataSource = FirebaseDataSourceImpl()
            val expenseRepository = ExpenseRepository(
                database.expenseDao(),
                database.incomeDao(),
                firebaseDataSource
            )

            val allExpenses = expenseRepository.getAllExpensesOnce()
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            val lastMonth = calendar.get(Calendar.MONTH)
            val lastYear = calendar.get(Calendar.YEAR)

            allExpenses.filter {
                try {
                    val date = sdf.parse(it.date)
                    val cal = Calendar.getInstance().apply { time = date!! }
                    cal.get(Calendar.MONTH) == lastMonth && cal.get(Calendar.YEAR) == lastYear
                } catch (e: Exception) {
                    false
                }
            }.sumOf { it.amount }
        }
    }
}
