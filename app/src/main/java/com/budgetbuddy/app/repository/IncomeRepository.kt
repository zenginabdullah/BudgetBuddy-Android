package com.budgetbuddy.app.data.repository

import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow

class IncomeRepository(
    private val incomeDao: IncomeDao,
    private val firebaseDataSource: FirebaseDataSource
) {

    /**
     * 1) Room’a yeni geliri ekle => Room auto-generated Long ID dönüyor.
     * 2) Bu ID’yi Int’e çevir ve IncomeEntity.id = o Int olsun.
     * 3) Firestore’a, bu Int ID’yi String’e çevirerek yaz.
     */
    suspend fun insertIncome(income: IncomeEntity) {
        // 1. Room’a ekle, Long ID'yi yakala
        val generatedIdLong: Long = incomeDao.insertIncome(income)
        val generatedId: Int = generatedIdLong.toInt()

        // 2. Aynı ID ile yeni nesne oluştur
        val incomeWithId = income.copy(id = generatedId)

        // 3. Firestore’a yaz (id => String)
        firebaseDataSource.insertIncome(incomeWithId)
    }

    /**
     * Gelir silme:
     * 1) Room’dan id ile sil
     * 2) Firestore’dan da aynı ID’ye göre sil
     */
    suspend fun deleteIncome(income: IncomeEntity) {
        // 1. Room’dan sil
        incomeDao.deleteIncomeById(income.id)
        // 2. Firestore’dan sil
        firebaseDataSource.deleteIncome(income.id)
    }

    /**
     * Room’daki tüm gelirleri temizle
     */
    suspend fun clearAllIncomes() {
        incomeDao.clearAllIncomes()
    }

    /**
     * Room’da kayıtlı gelirleri Flow olarak döner
     */
    fun getAllIncomes(): Flow<List<IncomeEntity>> {
        return incomeDao.getAllIncomes()
    }

    /**
     * Firestore’daki tüm gelirleri Room’a senkronize eder
     */
    suspend fun syncAllIncomesFromFirebase() {
        // 1. Firestore’dan tüm gelir listesini al
        val remoteIncomes: List<IncomeEntity> = firebaseDataSource.getAllIncomes()

        // 2. Room’u temizle
        incomeDao.clearAllIncomes()

        // 3. Remote’dan gelen her kaydı Room’a ekle (autoGenerate devrede)
        remoteIncomes.forEach { incomeDao.insertIncome(it) }
    }
}
