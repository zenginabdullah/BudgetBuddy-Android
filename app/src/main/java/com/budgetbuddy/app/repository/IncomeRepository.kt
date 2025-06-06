package com.budgetbuddy.app.data.repository

import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow

class IncomeRepository(
    private val incomeDao: IncomeDao,
    private val firebaseDataSource: FirebaseDataSource
) {

    suspend fun insertIncome(income: IncomeEntity) {
        val generatedIdLong: Long = incomeDao.insertIncome(income)
        val generatedId: Int = generatedIdLong.toInt()
        val incomeWithId = income.copy(id = generatedId)
        firebaseDataSource.insertIncome(incomeWithId)
    }

    suspend fun deleteIncome(income: IncomeEntity) {
        incomeDao.deleteIncomeById(income.id)
        firebaseDataSource.deleteIncome(income.id)
    }

    suspend fun clearAllIncomes() {
        incomeDao.clearAllIncomes()
    }

    fun getAllIncomes(): Flow<List<IncomeEntity>> {
        return incomeDao.getAllIncomes()
    }

    fun getIncomesByUserId(userId: String): Flow<List<IncomeEntity>> {
        return incomeDao.getIncomesByUserId(userId)
    }

    suspend fun syncAllIncomesFromFirebase() {
        val remoteIncomes: List<IncomeEntity> = firebaseDataSource.getAllIncomes()
        incomeDao.clearAllIncomes()
        remoteIncomes.forEach { incomeDao.insertIncome(it) }
    }

    suspend fun getIncomesByDate(date: String, userId: String): List<IncomeEntity> {
        return incomeDao.getIncomesByDate(date, userId)
    }

}
