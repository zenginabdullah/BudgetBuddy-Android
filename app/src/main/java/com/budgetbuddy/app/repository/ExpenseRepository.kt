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

    /**
     * Yeni bir gider eklerken:
     * 1) Room’a ekliyoruz, dönen Long → Int ID’yi alıyoruz.
     * 2) Firestore’a, Room’un atadığı bu Int ID’yi belge ID’si olarak kullanarak kaydediyoruz.
     */
    suspend fun insertExpense(expense: ExpenseEntity) {
        // 1. Room’a ekle, Long olarak dönen auto-generated ID’yi Int’e çevir
        val generatedIdLong: Long = expenseDao.insertExpense(expense)
        val generatedId: Int = generatedIdLong.toInt()

        // 2. Aynı ID’yi kullanarak expense nesnemizi güncelle
        val expenseWithId = expense.copy(id = generatedId)

        // 3. Firestore’a yaz
        firebaseDataSource.insertExpense(expenseWithId)
    }

    /**
     * Gider silme:
     * 1) Room’dan sil (deleteExpenseById)
     * 2) Firestore’dan sil (aynı Int ID’yi kullanarak doküman ID’si üzerinden)
     */
    suspend fun deleteExpense(expense: ExpenseEntity) {
        // 1. Room’dan silmek için doğrudan ID’yi kullanıyoruz:
        expenseDao.deleteExpenseById(expense.id)

        // 2. Firestore’dan sil
        firebaseDataSource.deleteExpense(expense.id)
    }

    /**
     * Room’daki tüm giderleri temizler
     */
    suspend fun clearAllExpenses() {
        expenseDao.clearAllExpenses()
    }

    /**
     * Room’daki tüm giderleri Flow olarak döner
     */
    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    /**
     * Income listesini de ihtiyacınız olursa Flow olarak döner (değişmedi)
     */
    fun getAllIncomes(): Flow<List<IncomeEntity>> = incomeDao.getAllIncomes()

    /**
     * “Bir kereye mahsus liste al” senkronizasyon için
     */
    suspend fun getAllExpensesOnce(): List<ExpenseEntity> {
        return expenseDao.getAllExpensesOnce()
    }

    /**
     * Firestore’daki tüm giderleri çekip Room’a kaydeder (pull senkronizasyonu)
     */
    suspend fun syncAllExpensesFromFirebase() {
        // 1. Firestore’dan liste çek
        val remoteExpenses: List<ExpenseEntity> = firebaseDataSource.getAllExpenses()

        // 2. Room’u temizle
        expenseDao.clearAllExpenses()

        // 3. Firestore’dan gelen her kaydı Room’a ekle
        //    Burada, ExpenseEntity.id zaten Firestore’dan Int tipi olarak geldiğine dikkat edin (getAllExpenses içinde mapleme yaptık)
        remoteExpenses.forEach { expenseDao.insertExpense(it) }
    }
}
