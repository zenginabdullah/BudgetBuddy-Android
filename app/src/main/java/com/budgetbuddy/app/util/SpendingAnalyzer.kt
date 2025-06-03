package com.budgetbuddy.app.util

import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity

object SpendingAnalyzer {

    fun generateSuggestion(expenses: List<ExpenseEntity>, incomes: List<IncomeEntity>): String {
        if (expenses.isEmpty() || incomes.isEmpty()) {
            return "Yeterli veri yok 😔. Birkaç harcama ve gelir girdikten sonra sana özel öneriler verebilirim!"
        }

        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = incomes.sumOf { it.amount }
        val ratio = totalExpense / totalIncome

        val mostSpentCategory = expenses
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .maxByOrNull { it.value }?.key ?: "Bilinmeyen"

        return when {
            ratio > 0.9 -> "Bu ay gelirin %90'ından fazlasını harcadın 😱. Özellikle \"$mostSpentCategory\" kategorisine dikkat etmeni öneririm!"
            ratio > 0.7 -> "Harcamaların gelirin %70'ini geçti. \"$mostSpentCategory\" kategorisinde tasarruf yaparak kontrolü ele alabilirsin 💪"
            else -> "Harcamaların oldukça dengeli 👏. Böyle devam! Belki \"$mostSpentCategory\" harcamalarını biraz kısarak daha fazla tasarruf edebilirsin 💰"
        }
    }
}
