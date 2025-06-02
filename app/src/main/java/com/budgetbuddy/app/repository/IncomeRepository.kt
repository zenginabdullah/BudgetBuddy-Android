package com.budgetbuddy.app.repository

import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IncomeRepository @Inject constructor(private val incomeDao: IncomeDao) {
    suspend fun insertIncome(income: IncomeEntity) {
        incomeDao.insertIncome(income)
    }
    suspend fun deleteIncome(income: IncomeEntity) {
        incomeDao.deleteIncome(income)
    }

    fun getAllIncomes(): Flow<List<IncomeEntity>> {
        return incomeDao.getAllIncomes()
    }
}