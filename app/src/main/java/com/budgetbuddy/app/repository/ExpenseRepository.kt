package com.budgetbuddy.app.data.repository

import com.budgetbuddy.app.data.local.dao.ExpenseDao
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.data.local.dao.IncomeDao
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao) {

    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun clearAllExpenses() {
        expenseDao.clearAllExpenses()
    }

    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    fun getAllIncomes(): Flow<List<IncomeEntity>> = incomeDao.getAllIncomes()

    suspend fun getAllExpensesOnce(): List<ExpenseEntity> {
        return expenseDao.getAllExpensesOnce()
    }

    fun getExpensesByUserId(userId: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByUserId(userId)
    }

    suspend fun getTodayTotalExpense(date: String, userId: String): Double? {
        return expenseDao.getTodayTotalExpense(date, userId)
    }

}
