package com.budgetbuddy.app.data.remote

import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity

interface FirebaseDataSource {
    // --- Expense işlemleri ---
    suspend fun insertExpense(expense: ExpenseEntity)
    suspend fun getAllExpenses(): List<ExpenseEntity>
    suspend fun deleteExpense(expenseId: Int)

    // --- Income işlemleri---
    suspend fun insertIncome(income: IncomeEntity)
    suspend fun getAllIncomes(): List<IncomeEntity>
    suspend fun deleteIncome(incomeId: Int)
}
