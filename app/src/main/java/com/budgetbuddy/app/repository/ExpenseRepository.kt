package com.budgetbuddy.app.data.repository

import com.budgetbuddy.app.data.local.dao.ExpenseDao
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val firebaseDataSource: FirebaseDataSource
) {

    suspend fun insertExpense(expense: ExpenseEntity) {
        val generatedIdLong: Long = expenseDao.insertExpense(expense)
        val generatedId: Int = generatedIdLong.toInt()
        val expenseWithId = expense.copy(id = generatedId)
        firebaseDataSource.insertExpense(expenseWithId)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpenseById(expense.id)
        firebaseDataSource.deleteExpense(expense.id)
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

    suspend fun syncAllExpensesFromFirebase() {
        val remoteExpenses: List<ExpenseEntity> = firebaseDataSource.getAllExpenses()
        expenseDao.clearAllExpenses()
        remoteExpenses.forEach { expenseDao.insertExpense(it) }
    }
}

