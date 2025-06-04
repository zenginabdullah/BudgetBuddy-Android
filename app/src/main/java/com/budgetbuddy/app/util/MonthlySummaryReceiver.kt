package com.budgetbuddy.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MonthlySummaryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val totalExpense = intent.getDoubleExtra("totalExpense", 0.0)

        // Log mesajı ekleyelim
        Log.d("MonthlySummaryReceiver", "Receiver triggered: Total Expense for last month: ₺%.2f".format(totalExpense))

        // Bildirim gönder
        NotificationHelper.showInfoNotification(
            context,
            "Geçen Ayki Harcama Özeti",
            "Geçen ayki toplam harcamanız: ₺%.2f".format(totalExpense)
        )

        // UI'da gösterilecek bir Toast ekleyelim
        Toast.makeText(context, "Geçen ayki harcama özetiniz: ₺%.2f".format(totalExpense), Toast.LENGTH_LONG).show()
    }
}

