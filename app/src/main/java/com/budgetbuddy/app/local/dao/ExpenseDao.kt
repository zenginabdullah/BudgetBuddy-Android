package com.budgetbuddy.app.data.local.dao

import androidx.room.*
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses")
    suspend fun clearAllExpenses()

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Flow<Double?>

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpensesOnce(): List<ExpenseEntity>


    @Query("SELECT * FROM expenses WHERE userId = :uid")
    fun getAllExpenses(uid: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesByUserId(userId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date AND userId = :userId")
    suspend fun getTodayTotalExpense(date: String, userId: String): Double?

}
